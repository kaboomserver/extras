package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public final class CommandEnchantAll implements CommandExecutor {
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Component
                    .text("Command has to be run by a player"));
            return true;
        }

        final Player player = (Player) sender;
        final ItemStack item = player.getInventory().getItemInMainHand();

        if (Material.AIR.equals(item.getType())) {
            player.sendMessage(Component
                    .text("Please hold an item in your hand to enchant it"));
            return true;
        }

        for (Enchantment enchantment : Enchantment.values()) {
            item.addUnsafeEnchantment(enchantment, Short.MAX_VALUE);
        }
        player.sendMessage(Component
                .text("I killed Martin."));
        return true;
    }
}
