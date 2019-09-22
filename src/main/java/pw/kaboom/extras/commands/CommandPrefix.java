package pw.kaboom.extras;

import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.entity.Player;

class CommandPrefix implements CommandExecutor {
	private Main main;
	public CommandPrefix(Main main) {
		this.main = main;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;
	
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /" + label + " <prefix|off>");
			} else if (args[0].equalsIgnoreCase("off")) {
				main.getConfig().set(player.getUniqueId().toString(), null);
				main.saveConfig();
				player.sendMessage("You no longer have a tag");
			} else {
				main.getConfig().set(player.getUniqueId().toString(), String.join(" ", args));
				main.saveConfig();
				player.sendMessage("You now have the tag: " + ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
			}
		}
		return true;
	}
}
