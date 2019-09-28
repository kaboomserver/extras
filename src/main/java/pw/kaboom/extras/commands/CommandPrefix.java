package pw.kaboom.extras;

import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.entity.Player;

import org.bukkit.plugin.java.JavaPlugin;

class CommandPrefix implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;
			final JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);
	
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /" + label + " <prefix|off>");
			} else if (args[0].equalsIgnoreCase("off")) {
				plugin.getConfig().set(player.getUniqueId().toString(), null);
				plugin.saveConfig();
				player.sendMessage("You no longer have a tag");
			} else {
				plugin.getConfig().set(player.getUniqueId().toString(), String.join(" ", args));
				plugin.saveConfig();
				player.sendMessage("You now have the tag: " + ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
			}
		}
		return true;
	}
}
