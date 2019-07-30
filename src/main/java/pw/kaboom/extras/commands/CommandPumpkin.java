package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;

class CommandPumpkin implements CommandExecutor {
	private void placePumpkin(Player player) {
		player.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		final Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
		} else {
			if (args[0].equals("*") || args[0].equals("**")) {
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					placePumpkin(onlinePlayer);
				}
				player.sendMessage("Everyone is now a pumpkin");
			} else {
				final Player target = Bukkit.getPlayer(args[0]);
				if (target != null) {
					placePumpkin(target);
					player.sendMessage("Player \"" + target.getName() + "\" is now a pumpkin");
				} else {
					player.sendMessage("Player \"" + target.getName() + "\" not found");
				}
			}	
		}
		return true;
	}
}
