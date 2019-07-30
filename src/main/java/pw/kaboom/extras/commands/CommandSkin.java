package pw.kaboom.extras;

import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class CommandSkin implements CommandExecutor {
	private Main main;
	public CommandSkin(Main main) {
		this.main = main;
	}

	private void changeSkin(final Player player, final String name) {
		new BukkitRunnable() {
			public void run() {
				try {
					final URL nameUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
					final HttpsURLConnection nameConnection = (HttpsURLConnection) nameUrl.openConnection();

					if (nameConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
						final InputStreamReader nameStream = new InputStreamReader(nameConnection.getInputStream());
						final String uuid = new JsonParser().parse(nameStream).getAsJsonObject().get("id").getAsString();
						nameStream.close();
						nameConnection.disconnect();

						final URL uuidUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
						final HttpsURLConnection uuidConnection = (HttpsURLConnection) uuidUrl.openConnection();

						if (uuidConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
							final InputStreamReader uuidStream = new InputStreamReader(uuidConnection.getInputStream());
							final JsonObject response = new JsonParser().parse(uuidStream).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
							final String texture = response.get("value").getAsString();
							final String signature = response.get("signature").getAsString();
							uuidStream.close();
							uuidConnection.disconnect();

							final PlayerProfile textureProfile = player.getPlayerProfile();
							textureProfile.clearProperties();
							textureProfile.setProperty(new ProfileProperty("textures", texture, signature));

							new BukkitRunnable() {
								public void run() {
									player.setPlayerProfile(textureProfile);
									player.sendMessage("Successfully set your skin to " + name + "'s");
								}
							}.runTask(main);
						} else {
							uuidConnection.disconnect();
							new BukkitRunnable() {
								public void run() {
									player.sendMessage("Failed to change skin. Try again later");
								}
							}.runTask(main);
						}
					} else {
						nameConnection.disconnect();
						new BukkitRunnable() {
							public void run() {
								player.sendMessage("A player with that username doesn't exist");
							}
						}.runTask(main);
					}
				} catch (Exception exception) {
				}
			}
		}.runTaskAsynchronously(main);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		final Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <username>");
		} else {
			final String name = args[0];
			changeSkin(player, name);
		}
		return true;
	}
}
