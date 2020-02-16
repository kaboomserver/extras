package pw.kaboom.extras.commands;

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

import pw.kaboom.extras.Main;

public final class CommandUsername implements CommandExecutor {
	public static boolean nameInProgress = false;
	
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
			} else if (nameInProgress) {
				player.sendMessage("Please wait a few seconds before changing your username");
			} else if (!name.equals(player.getName())) {
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if (name.equals(onlinePlayer.getName())) {
						player.sendMessage("A player with that username is already logged in");
						return true;
					}
				}
				
				nameInProgress = true;
				
				final PlayerProfile profile = player.getPlayerProfile();

				profile.setName(name);
				
				player.setPlayerProfile(profile);
				player.setOp(true);
				
				final int tickDelay = 40;

				new BukkitRunnable() {
					@Override
					public void run() {
						nameInProgress = false;
					}
				}.runTaskLaterAsynchronously(JavaPlugin.getPlugin(Main.class), tickDelay);
				
				player.sendMessage("Successfully set your username to \"" + name + "\"");
			} else {
				player.sendMessage("You already have the username \"" + name + "\"");
			}
		}
		return true;
	}
}
