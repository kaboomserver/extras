package pw.kaboom.extras;

import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;

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
			} else if (!Main.usernameInProgress.contains(player.getUniqueId())) {
				Main.usernameInProgress.add(player.getUniqueId());

				final String nameColor = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
				final String nameShort = nameColor.substring(0, Math.min(16, nameColor.length()));
				
				HttpClient client = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://api.ashcon.app/mojang/v2/user/" + nameShort.replace(" ", "%20")))
					.build();
				client.sendAsync(request, BodyHandlers.ofInputStream())
					.thenAccept(response -> {
					String texture = "";
					String signature = "";

					if (response.statusCode() == 200) {
						final InputStreamReader skinStream = new InputStreamReader(response.body());
						final JsonObject responseJson = new JsonParser().parse(skinStream).getAsJsonObject();
						final JsonObject rawSkin = responseJson.getAsJsonObject("textures").getAsJsonObject("raw");
						texture = rawSkin.get("value").getAsString();
						signature = rawSkin.get("signature").getAsString();
						try {
							skinStream.close();
						} catch (Exception exception) {
							System.out.println(exception);
						}
					}
					
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
							Main.usernameInProgress.remove(player.getUniqueId());
						}
					}.runTask(JavaPlugin.getPlugin(Main.class));
				});
			} else {
				player.sendMessage("Your username is already being changed. Please wait a few seconds.");
			}
		}
		return true;
	}
}
