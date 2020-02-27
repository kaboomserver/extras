package pw.kaboom.extras.helpers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pw.kaboom.extras.Main;

public final class SkinDownloader {
	private static HashMap<UUID, PlayerProfile> skinProfiles = new HashMap<UUID, PlayerProfile>();

	private HttpsURLConnection skinConnection;
	private InputStreamReader skinStream;

	private String texture;
	private String signature;

	public void applySkin(final Player player, final String name, final boolean shouldSendMessage) {
		new BukkitRunnable() {
			@Override
			public void run() {
				final PlayerProfile profile = player.getPlayerProfile();

				try {
					fetchSkinData(name);
					profile.setProperty(new ProfileProperty("textures", texture, signature));

					if (shouldSendMessage) {
						player.sendMessage("Successfully set your skin to " + name + "'s");
					}
				} catch (Exception exception) {
					try {
						skinStream.close();
						skinConnection.disconnect();
					} catch (Exception ignored) {
					}

					if (shouldSendMessage) {
						player.sendMessage("A player with that username doesn't exist");
					}

					return;
				}

				new BukkitRunnable() {
					@Override
					public void run() {
						try {
							player.setPlayerProfile(profile);
						} catch (Exception ignored) {
						}
					}
				}.runTask(JavaPlugin.getPlugin(Main.class));
			}
		}.runTaskAsynchronously(JavaPlugin.getPlugin(Main.class));
	}

	public void fillJoinProfile(final PlayerProfile profile, final String name, final UUID uuid) {
		try {
			fetchSkinData(name);
			profile.setProperty(new ProfileProperty("textures", texture, signature));
			skinProfiles.put(uuid, profile);
		} catch (Exception exception) {
			try {
				skinStream.close();
				skinConnection.disconnect();
			} catch (Exception ignored) {
			}
		}
	}

	private void fetchSkinData(final String playerName) throws IOException {
		final URL skinUrl = new URL("https://api.ashcon.app/mojang/v2/user/" + playerName);
		skinConnection = (HttpsURLConnection) skinUrl.openConnection();
		skinConnection.setConnectTimeout(0);

		skinStream = new InputStreamReader(skinConnection.getInputStream());
		final JsonObject responseJson = new JsonParser().parse(skinStream).getAsJsonObject();
		final JsonObject rawSkin = responseJson.getAsJsonObject("textures").getAsJsonObject("raw");
		texture = rawSkin.get("value").getAsString();
		signature = rawSkin.get("signature").getAsString();

		skinStream.close();
		skinConnection.disconnect();
	}

	public static PlayerProfile getProfile(final UUID uuid) {
		return skinProfiles.get(uuid);
	}

	public static void removeProfile(final UUID uuid) {
		skinProfiles.remove(uuid);
	}
}