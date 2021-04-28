package pw.kaboom.extras.helpers;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pw.kaboom.extras.Main;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.URL;

public final class SkinDownloader {
	//private static HashMap<UUID, PlayerProfile> skinProfiles = new HashMap<UUID, PlayerProfile>();

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
					exception.printStackTrace();
					try {
						skinStream.close();
						skinConnection.disconnect();
					} catch (Exception ignored) {
					}

					if (shouldSendMessage) {
						player.sendMessage("A player with that username doesn't exist, or the skin server is down.");
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

	/*public void fillJoinProfile(final PlayerProfile profile, final String name, final UUID uuid) {
		try {
			fetchSkinData(name);
			profile.setProperty(new ProfileProperty("textures", texture, signature));
		} catch (Exception exception) {
			try {
				skinStream.close();
				skinConnection.disconnect();
			} catch (Exception ignored) {
			}
		}
	}*/

	private String usernameToUUID(final String playerName) throws Exception {
		final URL usernameURL = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
		final HttpsURLConnection usernameConnection = (HttpsURLConnection) usernameURL.openConnection();
		usernameConnection.setConnectTimeout(0);

		InputStreamReader usernameStream = new InputStreamReader(usernameConnection.getInputStream());
		final JsonObject responseJson = new JsonParser().parse(usernameStream).getAsJsonObject();
		final String id = responseJson.get("id").getAsString();

		usernameStream.close();
		usernameConnection.disconnect();

		return id;
	}

	private void fetchSkinData(final String playerName) throws Exception {
		final String playerUUID = usernameToUUID(playerName);
		final URL skinUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + playerUUID + "?unsigned=false");
		skinConnection = (HttpsURLConnection) skinUrl.openConnection();
		skinConnection.setConnectTimeout(0);

		skinStream = new InputStreamReader(skinConnection.getInputStream());
		final JsonObject responseJson = new JsonParser().parse(skinStream).getAsJsonObject();
		texture = responseJson.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
		signature =  responseJson.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("signature").getAsString();

		skinStream.close();
		skinConnection.disconnect();
	}

	/*public static PlayerProfile getProfile(final UUID uuid) {
		return skinProfiles.get(uuid);
	}

	public static void removeProfile(final UUID uuid) {
		skinProfiles.remove(uuid);
	}*/

}
