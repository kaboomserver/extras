package pw.kaboom.extras.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import pw.kaboom.extras.helpers.SkinDownloader;

public final class CommandUsername implements CommandExecutor {
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;

			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /" + label + " <username>");
			} else if (!SkinDownloader.skinInProgress.contains(player.getUniqueId())) {
				final String nameColor = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
				final String name = nameColor.substring(0, Math.min(16, nameColor.length()));

				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if (onlinePlayer.getName().equals(name)) {
						player.sendMessage("A player with that username is already logged in");
						return true;
					}
				}

				final boolean shouldChangeUsername = true;
				final boolean shouldSendMessage = true;

				SkinDownloader skinDownloader = new SkinDownloader();
				skinDownloader.applySkin(player, name, shouldChangeUsername, shouldSendMessage);
			} else {
				player.sendMessage("Your username is already being changed. Please wait a few seconds.");
			}
		}
		return true;
	}
}
