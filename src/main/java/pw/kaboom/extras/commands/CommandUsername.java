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

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class CommandUsername implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;
		
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /" + label + " <username>");
			} else {
				final String[] name = args;
		
				new BukkitRunnable() {
					public void run() {
						try {
							String texture = "";
							String signature = "";
		
							final String nameColor = ChatColor.translateAlternateColorCodes('&', String.join(" ", name));
							final String nameShort = nameColor.substring(0, Math.min(16, nameColor.length()));
		
							final URL skinUrl = new URL("https://api.ashcon.app/mojang/v2/user/" + nameShort);
							final HttpsURLConnection skinConnection = (HttpsURLConnection) skinUrl.openConnection();
							skinConnection.setConnectTimeout(0);
							skinConnection.setDefaultUseCaches(false);
							skinConnection.setUseCaches(false);
		
							if (skinConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
								final InputStreamReader skinStream = new InputStreamReader(skinConnection.getInputStream());
								final JsonObject response = new JsonParser().parse(skinStream).getAsJsonObject();
								final JsonObject rawSkin = response.getAsJsonObject("textures").getAsJsonObject("raw");
								texture = rawSkin.get("value").getAsString();
								signature = rawSkin.get("signature").getAsString();
								skinStream.close();
							}
		
							skinConnection.disconnect();
		
							final PlayerProfile profile = player.getPlayerProfile();
							profile.setName(nameShort);
		
							if (!("".equals(texture)) &&
								!("".equals(signature))) {
								profile.setProperty(new ProfileProperty("textures", texture, signature));
							}
		
							player.sendMessage("Successfully set your username to \"" + nameShort + "\"");
							new BukkitRunnable() {
								public void run() {
									player.setPlayerProfile(profile);
								}
							}.runTask(JavaPlugin.getPlugin(Main.class));
						} catch (Exception exception) {
						}
					}
				}.runTaskAsynchronously(JavaPlugin.getPlugin(Main.class));
			}
		}
		return true;
	}
}
