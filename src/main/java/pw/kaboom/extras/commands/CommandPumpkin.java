package pw.kaboom.extras.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;

public class CommandPumpkin implements CommandExecutor {
	private void placePumpkin(Player player) {
		player.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
		} else {
			if (args[0].equals("*") || args[0].equals("**")) {
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					placePumpkin(onlinePlayer);
				}
				sender.sendMessage("Everyone is now a pumpkin");
			} else {
				final Player target = Bukkit.getPlayer(args[0]);
				if (target != null) {
					placePumpkin(target);
					sender.sendMessage("Player \"" + target.getName() + "\" is now a pumpkin");
				} else {
					sender.sendMessage("Player \"" + target.getName() + "\" not found");
				}
			}	
		}
		return true;
	}
}
