package pw.kaboom.extras;

import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import org.bukkit.entity.Player;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class SkinDownloader {
	private String texture;
	private String signature;

	public void applySkin(Player player, String name, boolean shouldChangeName, boolean shouldSendMessage) {
		Main.usernameInProgress.add(player.getUniqueId());

		new BukkitRunnable() {
			public void run() {
				final PlayerProfile profile = player.getPlayerProfile();
				
				if (shouldChangeName && shouldSendMessage) {
					profile.setName(name);
					player.sendMessage("Changing your username. Please wait...");
				}

				if (fetchSkinData(name)) {
					profile.setProperty(new ProfileProperty("textures", texture, signature));
					if (!shouldChangeName && shouldSendMessage) {
						player.sendMessage("Successfully set your skin to " + name + "'s");
					}
				} else if (!shouldChangeName && shouldSendMessage) {
					player.sendMessage("A player with that username doesn't exist");
					Main.usernameInProgress.remove(player.getUniqueId());
					return;
				}

				new BukkitRunnable() {
					public void run() {
						if (player.isOnline()) {
							player.setPlayerProfile(profile);

							if (shouldChangeName && shouldSendMessage) {
								player.sendMessage("Successfully set your username to \"" + name + "\"");
							}
						}
						Main.usernameInProgress.remove(player.getUniqueId());
					}
				}.runTask(JavaPlugin.getPlugin(Main.class));
			}
		}.runTaskAsynchronously(JavaPlugin.getPlugin(Main.class));
	}

	private boolean fetchSkinData(String playerName) {
		try {
			final URL skinUrl = new URL("https://api.ashcon.app/mojang/v2/user/" + playerName);
			final HttpsURLConnection skinConnection = (HttpsURLConnection) skinUrl.openConnection();
			skinConnection.setConnectTimeout(0);
			skinConnection.setDefaultUseCaches(false);
			skinConnection.setUseCaches(false);

			if (skinConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				final InputStreamReader skinStream = new InputStreamReader(skinConnection.getInputStream());
				final JsonObject responseJson = new JsonParser().parse(skinStream).getAsJsonObject();
				final JsonObject rawSkin = responseJson.getAsJsonObject("textures").getAsJsonObject("raw");
				texture = rawSkin.get("value").getAsString();
				signature = rawSkin.get("signature").getAsString();
				try {
					skinStream.close();
				} catch (Exception exception) {
					System.out.println(exception);
				}
				return true;
			}
		} catch (Exception exception) {
		}
		return false;
	}
}