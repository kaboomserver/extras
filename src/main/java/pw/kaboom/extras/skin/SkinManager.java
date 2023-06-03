package pw.kaboom.extras.skin;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.platform.PlatformScheduler;
import pw.kaboom.extras.skin.response.ProfileResponse;
import pw.kaboom.extras.skin.response.SkinResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SkinManager {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();
    private static final ExecutorService executorService = Executors
        .newCachedThreadPool();

    public static void resetSkin(final Player player, final boolean shouldSendMessage) {
        executorService.submit(() -> {
            final PlayerProfile playerProfile = player.getPlayerProfile();
            playerProfile.removeProperty("textures");

            final Main plugin = JavaPlugin.getPlugin(Main.class);
            PlatformScheduler.executeOnEntity(plugin, player,
                    () -> player.setPlayerProfile(playerProfile));

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
                skinData = getSkinData(name).get();
            } catch (Exception e) {
                if(!shouldSendMessage) {
                    return;
                }

                player.sendMessage(Component.text("A player with that username doesn't exist"));
                return;
            }

            final String texture = skinData.texture();
            final String signature = skinData.signature();
            profile.setProperty(new ProfileProperty("textures", texture, signature));

            final Main plugin = JavaPlugin.getPlugin(Main.class);
            PlatformScheduler.executeOnEntity(plugin, player,
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
                "https://sessionserver.mojang.com/session/minecraft/profile/"
                + uuid + "?unsigned=false", SkinResponse.class);

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

    private static <T> T sendRequestForJSON(String url, Class<T> clazz) {
        final HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(url))
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
            final ProfileResponse parsedResponse = sendRequestForJSON
                ("https://api.mojang.com/users/profiles/minecraft/" + playerName,
                ProfileResponse.class);

            final String dashedUuid = parsedResponse
                .id()
                .replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");

            return UUID.fromString(dashedUuid);
        }, executorService);
    }
}
