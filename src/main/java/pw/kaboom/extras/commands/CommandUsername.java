package pw.kaboom.extras;

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

				new BukkitRunnable() {
					public void run() {
						final PlayerProfile profile = player.getPlayerProfile();
						profile.setName(nameShort);

						SkinDownloader skinDownloader = new SkinDownloader();

						if (skinDownloader.fetchSkinData(args[0])) {
							final String texture = skinDownloader.getTexture();
							final String signature = skinDownloader.getSignature();

							profile.setProperty(new ProfileProperty("textures", texture, signature));
						}
			
						player.sendMessage("Successfully set your username to \"" + nameShort + "\"");

						new BukkitRunnable() {
							public void run() {
								player.setPlayerProfile(profile);
								Main.usernameInProgress.remove(player.getUniqueId());
							}
						}.runTask(JavaPlugin.getPlugin(Main.class));
					}
				}.runTaskAsynchronously(JavaPlugin.getPlugin(Main.class));
			} else {
				player.sendMessage("Your username is already being changed. Please wait a few seconds.");
			}
		}
		return true;
	}
}
