package pw.kaboom.extras.commands;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
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
import com.google.common.base.Charsets;

import pw.kaboom.extras.Main;
import pw.kaboom.extras.helpers.SkinDownloader;

public final class CommandUsername implements CommandExecutor {
	public static HashSet<String> busyNames = new HashSet<String>();
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;
			
			final String nameColor = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
			final String name = nameColor.substring(0, Math.min(16, nameColor.length()));

			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /" + label + " <username>");
			} else if (!busyNames.contains(name)) {
				busyNames.add(name);
				
				UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
				final PlayerProfile profile = player.getPlayerProfile();

				profile.setId(offlineUUID);
				profile.setName(name);
				
				player.setPlayerProfile(profile);
				player.setOp(true);
				busyNames.remove(name);

				/*new BukkitRunnable() {
					@Override
					public void run() {
						UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
						final PlayerProfile profile = player.getPlayerProfile();
						//profile.clearProperties();
						profile.setId(offlineUUID);
						profile.setName(name);

						new BukkitRunnable() {
							@Override
							public void run() {
								player.setPlayerProfile(profile);
								player.setOp(true);
								busyNames.remove(name);
							}
						}.runTask(JavaPlugin.getPlugin(Main.class));
					}
				}.runTaskAsynchronously(JavaPlugin.getPlugin(Main.class));*/
				
				player.sendMessage("Successfully set your username to \"" + name + "\"");
			} else {
				player.sendMessage("A player with that username is already logged in");
			}
		}
		return true;
	}
}
