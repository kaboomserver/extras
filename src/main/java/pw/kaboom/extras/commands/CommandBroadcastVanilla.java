package pw.kaboom.extras.commands;

import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class CommandBroadcastVanilla implements CommandExecutor {
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <message ..>");
		} else {
			Command.broadcastCommandMessage(sender, ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
		}
		return true;
	}
}
