package pw.kaboom.extras.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.destroystokyo.paper.profile.PlayerProfile;

public final class CommandUsername implements CommandExecutor {
	private long millis;

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;

			final String nameColor = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
			final String name = nameColor.substring(0, Math.min(16, nameColor.length()));

			final long millisDifference = System.currentTimeMillis() - millis;

			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /" + label + " <username>");
			} else if (name.equals(player.getName())) {
				player.sendMessage("You already have the username \"" + name + "\"");
			} else if (millisDifference <= 2000) {
				player.sendMessage("Please wait a few seconds before changing your username");
			} else {
				if (Bukkit.getPlayer(name) != null
						&& Bukkit.getPlayer(name).isOnline()) {
					player.sendMessage("A player with that username is already logged in");
					return true;
				}

				final PlayerProfile profile = player.getPlayerProfile();

				profile.setName(name);

				player.setPlayerProfile(profile);

				millis = System.currentTimeMillis();

				player.sendMessage("Successfully set your username to \"" + name + "\"");
			}
		}
		return true;
	}
}
