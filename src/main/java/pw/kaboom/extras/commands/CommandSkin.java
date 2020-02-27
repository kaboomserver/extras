package pw.kaboom.extras.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import pw.kaboom.extras.helpers.SkinDownloader;

public final class CommandSkin implements CommandExecutor {
	private long millis;

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;

			final long millisDifference = System.currentTimeMillis() - millis;

			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /" + label + " <username>");
			} else if (millisDifference <= 2000) {
				player.sendMessage("Please wait a few seconds before changing your skin");
			} else {
				final String name = args[0];
				final boolean shouldSendMessage = true;

				SkinDownloader skinDownloader = new SkinDownloader();
				skinDownloader.applySkin(player, name, shouldSendMessage);

				millis = System.currentTimeMillis();
			}
		}
		return true;
	}
}
