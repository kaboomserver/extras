package pw.kaboom.extras;

import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

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

	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;
	
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /" + label + " <username>");
			} else {
				final String name = args[0];
				new BukkitRunnable() {
					public void run() {
						try {
							final URL skinUrl = new URL("https://api.ashcon.app/mojang/v2/user/" + name);
							final HttpsURLConnection skinConnection = (HttpsURLConnection) skinUrl.openConnection();
							skinConnection.setConnectTimeout(0);
							skinConnection.setDefaultUseCaches(false);
							skinConnection.setUseCaches(false);
	
							if (skinConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
								final InputStreamReader skinStream = new InputStreamReader(skinConnection.getInputStream());
								final JsonObject response = new JsonParser().parse(skinStream).getAsJsonObject();
								final JsonObject rawSkin = response.getAsJsonObject("textures").getAsJsonObject("raw");
								final String texture = rawSkin.get("value").getAsString();
								final String signature = rawSkin.get("signature").getAsString();
								skinStream.close();
	
								final PlayerProfile textureProfile = player.getPlayerProfile();
								textureProfile.setProperty(new ProfileProperty("textures", texture, signature));
	
								player.sendMessage("Successfully set your skin to " + name + "'s");
								new BukkitRunnable() {
									public void run() {
										player.setPlayerProfile(textureProfile);
									}
								}.runTask(main);
							} else {
								player.sendMessage("A player with that username doesn't exist");
							}
	
							skinConnection.disconnect();
						} catch (Exception exception) {
						}
					}
				}.runTaskAsynchronously(main);
			}
		}
		return true;
	}
}
