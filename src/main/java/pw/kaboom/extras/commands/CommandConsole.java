package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

class CommandConsole implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <message ..>");
		} else {
			Bukkit.dispatchCommand(
				Bukkit.getConsoleSender(),
				"minecraft:say " + ChatColor.translateAlternateColorCodes('&', String.join(" ", args))
			);
		}
		return true;
	}
}
