package pw.kaboom.extras.commands;

import org.bukkit.Material;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.enchantments.Enchantment;

import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;

public class CommandEnchantAll implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;
			final ItemStack item = player.getInventory().getItemInMainHand();
	
			if (item.getType() == Material.AIR) {
				player.sendMessage("Please hold an item in your hand to enchant it");
			} else {
				item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 32767);
				item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 32767);
				item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 32767);
				item.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 32767);
				item.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 32767);
				item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 32767);
				item.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 32767);
				item.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 32767);
				item.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 32767);
				item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 32767);
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 32767);
				item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 32767);
				item.addUnsafeEnchantment(Enchantment.FROST_WALKER, 32767);
				item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 32767);
				item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 32767);
				item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 32767);
				item.addUnsafeEnchantment(Enchantment.LUCK, 32767);
				item.addUnsafeEnchantment(Enchantment.LURE, 32767);
				item.addUnsafeEnchantment(Enchantment.MENDING, 32767);
				item.addUnsafeEnchantment(Enchantment.OXYGEN, 32767);
				item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 32767);
				item.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 32767);
				item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 32767);
				item.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 32767);
				item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 32767);
				item.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 32767);
				item.addUnsafeEnchantment(Enchantment.SWEEPING_EDGE, 32767);
				item.addUnsafeEnchantment(Enchantment.THORNS, 32767);
				item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 32767);
				item.addUnsafeEnchantment(Enchantment.WATER_WORKER, 32767);
				player.sendMessage("I killed Tim.");
			}
		}
		return true;
	}
}
