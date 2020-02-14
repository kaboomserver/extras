package pw.kaboom.extras.commands;

import org.bukkit.Material;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.enchantments.Enchantment;

import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;

public final class CommandEnchantAll implements CommandExecutor {
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;
			final ItemStack item = player.getInventory().getItemInMainHand();

			if (Material.AIR.equals(item.getType())) {
				player.sendMessage("Please hold an item in your hand to enchant it");
			} else {
				for (Enchantment enchantment : Enchantment.values()) {
					item.addUnsafeEnchantment(enchantment, Short.MAX_VALUE);
				}
				player.sendMessage("I killed Tim.");
			}
		}
		return true;
	}
}
