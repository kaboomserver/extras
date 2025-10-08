package pw.kaboom.extras.modules.player.skin;

import com.google.gson.Gson;
import java.lang.InterruptedException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.UUID;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import net.kyori.adventure.text.Component;

import org.bukkit.scheduler.BukkitScheduler;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.modules.player.skin.response.ProfileResponse;
import pw.kaboom.extras.modules.player.skin.response.SkinResponse;

public final class SkinManager {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();
    private static final ExecutorService executorService = Executors
        .newCachedThreadPool();
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

    public static void resetSkin(final Player player, final boolean shouldSendMessage) {
        executorService.submit(() -> {
            final PlayerProfile playerProfile = player.getPlayerProfile();
            playerProfile.removeProperty("textures");

            final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
            final Main plugin = JavaPlugin.getPlugin(Main.class);

            bukkitScheduler.runTask(plugin, () -> player.setPlayerProfile(playerProfile));

            if(!shouldSendMessage) {
                return;
            }

            player.sendMessage(Component.text("Successfully removed your skin"));
        });
    }

    public static void applySkin(final Player player, final String name,
        final boolean shouldSendMessage) {
        executorService.submit(() -> {
            final PlayerProfile profile = player.getPlayerProfile();
            final SkinData skinData;

            try {
                skinData = getSkinData(name).get(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                if (!shouldSendMessage) {
                    return;
                }

                player.sendMessage(Component.text("Skin fetching was interrupted"));
                return;
            } catch (TimeoutException e) {
                if (!shouldSendMessage) {
                    return;
                }

                player.sendMessage(Component.text("Took too long to fetch skin"));
                return;
            } catch (ExecutionException | CompletionException e) {
                if(!shouldSendMessage) {
                    return;
                }

                player.sendMessage(Component.text("A player with that username doesn't exist"));
                return;
            }

            final String texture = skinData.texture();
            final String signature = skinData.signature();
            profile.setProperty(new ProfileProperty("textures", texture, signature));

            final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
            final Main plugin = JavaPlugin.getPlugin(Main.class);

            bukkitScheduler.runTask(plugin,
                () -> player.setPlayerProfile(profile));


            if(!shouldSendMessage) {
                return;
            }

            player.sendMessage(Component.text("Successfully set your skin to ")
                .append(Component.text(name))
                .append(Component.text("'s")));
        });
    }

    public static CompletableFuture<SkinData> getSkinData(final String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            final UUID uuid;
            try {
                uuid = getUUID(playerName).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
                return getSkinData(uuid).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    public static CompletableFuture<SkinData> getSkinData(final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            final SkinResponse response = sendRequestForJSON(
                SESSION_HOST,
               "/session/minecraft/profile/" + uuid + "?unsigned=false",
                SkinResponse.class
            );

            final List<ProfileProperty> properties = response.properties();

            for (ProfileProperty property : properties) {
                if(!property.getName().equals("textures")) {
                    continue;
                }

                return new SkinData(property.getValue(), property.getSignature());
            }

            throw new RuntimeException("No textures property");
        }, executorService);
    }

    private static <T> T sendRequestForJSON(URI uri, String endpoint, Class<T> clazz) {
        final HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(uri.resolve(endpoint))
            .build();

        final HttpResponse<String> response;

        try {
            response = httpClient.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return GSON.fromJson(response.body(), clazz);
    }

    private static CompletableFuture<UUID> getUUID(final String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            final ProfileResponse parsedResponse = sendRequestForJSON(
                    PROFILE_ENDPOINT,
                    "/users/profiles/minecraft/" + playerName,
                    ProfileResponse.class
            );

            final String dashedUuid = parsedResponse
                .id()
                .replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");

            return UUID.fromString(dashedUuid);
        }, executorService);
    }
}
