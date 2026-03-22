package pw.kaboom.extras.modules.player.skin;

import com.google.gson.Gson;
import java.lang.InterruptedException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;

import java.util.concurrent.*;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.destroystokyo.paper.profile.ProfileProperty;

import net.kyori.adventure.text.Component;

import org.jspecify.annotations.Nullable;
import pw.kaboom.extras.modules.player.skin.response.ProfileResponse;
import pw.kaboom.extras.modules.player.skin.response.SkinResponse;

import static pw.kaboom.extras.Main.PLUGIN;

public final class SkinManager extends Thread {
    private static final Pattern PREMIUM_USERNAME = Pattern.compile("^[a-zA-Z0-9_]{1,16}$");
    private static final Pattern UNDASHED_UUID =
            Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    private static final Gson GSON = new Gson();
    private static final URI SESSION_HOST =
            URI.create(
                    System.getProperty(
                            "minecraft.api.session.host",
                            "https://sessionserver.mojang.com"
                    )
            );
    private static final URI PROFILE_ENDPOINT = URI.create(
            // 1.21.9+
            System.getProperty(
                    "minecraft.api.profiles.host",
                    System.getProperty(
                            // 1.21.9-
                            "minecraft.api.session.host",
                            "https://api.mojang.com"
                    )
            )
    );
    private static final Component ERROR_MESSAGE = Component.text("Couldn't set your skin.");
    private static final BlockingQueue<SkinFillRequest> SKIN_REQUEST_QUEUE
            = new LinkedBlockingQueue<>();
    private static final long EXECUTION_INTERVAL = 1000;

    static {
        new SkinManager().start();
    }

    private SkinManager() {
        // avoid blocking jvm shutdown
        this.setDaemon(true);
    }

    @Override
    public void run() {
        final Map<String, UUID> nameToIdCache = new HashMap<>();
        long lastRequest = 0;

        try(final var client = HttpClient.newHttpClient()) {
            for(;;) {
                while (SKIN_REQUEST_QUEUE.size() > 32) SKIN_REQUEST_QUEUE.remove();
                final SkinFillRequest request = SKIN_REQUEST_QUEUE.take();

                final long diff = System.currentTimeMillis() - lastRequest;
                if (diff < EXECUTION_INTERVAL) {
                    //noinspection BusyWait
                    Thread.sleep(EXECUTION_INTERVAL - diff);
                }

                var ply = Bukkit.getPlayer(request.fromPlayer());
                if (ply == null) continue;

                final var toUser = request.toUser();
                UUID id = null;

                if (toUser.equalsIgnoreCase(ply.getName())
                        && Bukkit.getServerConfig().isProxyOnlineMode())
                    id = ply.getUniqueId();

                id = nameToIdCache.getOrDefault(toUser, id);

                final var resultConsumer = request.resultConsumer();

                try {
                    if (id == null) {
                        lastRequest = System.currentTimeMillis();
                        id = getUUID(client, toUser);
                        nameToIdCache.put(toUser, id);
                    }

                    lastRequest = System.currentTimeMillis();
                    // always refetch
                    final var skin = getSkinData(client, id);

                    // re-get player to ensure it hasn't changed while we were fetching the skin
                    ply = Bukkit.getPlayer(request.fromPlayer());
                    if (ply == null) continue;

                    resultConsumer.accept(ply, skin);
                } catch (final Exception ignored) {
                    resultConsumer.accept(ply, null);
                }
            }
        } catch (final InterruptedException ignored) {

        }
    }

    public static void resetSkin(final Player player, final boolean shouldSendMessage) {
        setSkin(player, null);

        if (shouldSendMessage)
            player.sendMessage(Component.text("Successfully removed your skin"));
    }

    private static void setSkin(final Player player, final @Nullable SkinData skinData) {
        final var profile = player.getPlayerProfile();
        if (skinData != null) {
            profile.setProperty(new ProfileProperty(
                    "textures",
                    skinData.texture(), skinData.signature()));
        } else {
            profile.removeProperty("texture");
        }

        Bukkit.getScheduler().runTask(PLUGIN, () -> player.setPlayerProfile(profile));
    }

    public static void requestSkin(final Player player, final String name,
                                   final boolean shouldSendMessage) {
        if (!PREMIUM_USERNAME.matcher(name).matches()) {
            if (shouldSendMessage) player.sendMessage(ERROR_MESSAGE);
            return;
        }

        final UUID uuid = player.getUniqueId();
        SKIN_REQUEST_QUEUE.removeIf(skinFillRequest -> skinFillRequest.fromPlayer().equals(uuid));
        SKIN_REQUEST_QUEUE.add(
                new SkinFillRequest(
                        uuid,
                        name,
                    (strongPlayer, skinData) -> {
                            if (skinData == null) {
                                if (shouldSendMessage) strongPlayer.sendMessage(ERROR_MESSAGE);
                                return;
                            }

                            setSkin(strongPlayer, skinData);
                            if (shouldSendMessage)
                                strongPlayer
                                    .sendMessage(Component.text("Successfully set your skin to ")
                                    .append(Component.text(name))
                                    .append(Component.text("'s")));
                        }
                )
        );
    }

    private static SkinData getSkinData(final HttpClient client, final UUID uuid) {
        final SkinResponse response = sendRequestForJSON(
            client,
            SESSION_HOST,
            "/session/minecraft/profile/" + uuid + "?unsigned=false",
            SkinResponse.class
        );

        final List<ProfileProperty> properties = response.properties();

        for (final ProfileProperty property : properties) {
            if (!property.getName().equals("textures"))
                continue;


            return new SkinData(property.getValue(), property.getSignature());
        }

        throw new RuntimeException("No textures property");
    }

    private static <T> T sendRequestForJSON(final HttpClient client, final URI uri,
                                            final String endpoint, final Class<T> clazz) {
        final HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(uri.resolve(endpoint))
            .build();

        final HttpResponse<String> response;

        try {
            response = client.send(request, BodyHandlers.ofString());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        return GSON.fromJson(response.body(), clazz);
    }

    private static UUID getUUID(final HttpClient client, final String playerName) {
        final ProfileResponse parsedResponse = sendRequestForJSON(
                client,
                PROFILE_ENDPOINT,
                "/users/profiles/minecraft/" + playerName,
                ProfileResponse.class
        );

        final String dashedUuid = UNDASHED_UUID
                .matcher(parsedResponse.id())
                .replaceAll("$1-$2-$3-$4-$5");

        return UUID.fromString(dashedUuid);
    }
}
