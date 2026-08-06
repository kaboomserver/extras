package pw.kaboom.extras.commands;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class CommandEnchantAll implements CommandExecutor {
    public boolean onCommand(final @NonNull CommandSender sender,
                             final @NonNull Command command,
                             final @NonNull String label,
                             final String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Component
                    .text("Command has to be run by a player"));
            return true;
        }

        final ItemStack item = player.getInventory().getItemInMainHand();

        if (Material.AIR.equals(item.getType())) {
            player.sendMessage(Component
                    .text("Please hold an item in your hand to enchant it"));
            return true;
        }

        final Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT);
        for (Enchantment enchantment : registry) {
            item.addUnsafeEnchantment(enchantment, Short.MAX_VALUE);
        }
        player.sendMessage(Component
                .text("I killed Martin."));
        return true;
    }
}
