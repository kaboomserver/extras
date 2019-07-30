package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

class CommandClearChat implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			for (int i = 0; i < 100; ++i) {
				onlinePlayer.sendMessage("");
			}
			onlinePlayer.sendMessage(ChatColor.DARK_GREEN + "The chat has been cleared");
		}
		return true;
	}
}
