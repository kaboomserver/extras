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

class CommandUsername implements CommandExecutor {
	private Main main;
	public CommandUsername(Main main) {
		this.main = main;
	}

	private void changeUsernameSkin(final Player player, final String[] name) {
		new BukkitRunnable() {
			public void run() {
				try {
					String texture = "";
					String signature = "";

					final String nameColor = ChatColor.translateAlternateColorCodes('&', String.join(" ", name));
					final String nameShort = nameColor.substring(0, Math.min(16, nameColor.length()));

					final URL nameUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" + nameShort);
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
							texture = response.get("value").getAsString();
							signature = response.get("signature").getAsString();

							uuidStream.close();
							uuidConnection.disconnect();
						}
					}

					final PlayerProfile profile = player.getPlayerProfile();
					profile.clearProperties();
					profile.setName(nameShort);

					if (!("".equals(texture)) &&
						!("".equals(signature))) {
						profile.setProperty(new ProfileProperty("textures", texture, signature));
					}

					new BukkitRunnable() {
						public void run() {
							player.setPlayerProfile(profile);
							player.sendMessage("Successfully set your username to \"" + nameShort + "\"");
						}
					}.runTask(main);
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
			final String[] name = args;
			changeUsernameSkin(player, name);
		}
		return true;
	}
}
