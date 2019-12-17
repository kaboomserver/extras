package pw.kaboom.extras.commands;

import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.entity.Player;

import pw.kaboom.extras.Main;
import pw.kaboom.extras.helpers.SkinDownloader;

public class CommandSkin implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;
	
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /" + label + " <username>");
			} else if (!Main.skinInProgress.contains(player.getUniqueId())) {
				final String name = args[0];
				final boolean shouldChangeUsername = false;
				final boolean shouldSendMessage = true;
				
				SkinDownloader skinDownloader = new SkinDownloader();
				skinDownloader.applySkin(player, name, shouldChangeUsername, shouldSendMessage);
			} else {
				player.sendMessage("You are already applying a skin. Please wait a few seconds.");
			}
		}
		return true;
	}
}
