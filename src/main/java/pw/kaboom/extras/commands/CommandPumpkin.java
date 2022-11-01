package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public final class CommandPumpkin implements CommandExecutor {
    private void placePumpkin(final Player player) {
        player.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
    }

    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component
                    .text("Usage: /" + label + " <player>",
                            NamedTextColor.RED));
            return true;
        }

        if (args[0].equals("*") || args[0].equals("**")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                placePumpkin(onlinePlayer);
            }
            sender.sendMessage(Component.text("Everyone is now a pumpkin"));
            return true;
        }

        final Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(
                Component.text("Player \"")
                    .append(Component.text(args[0]))
                    .append(Component.text("\" not found"))
            );
            return true;
        }

        placePumpkin(target);
        sender.sendMessage(
            Component.text("Player \"")
                .append(Component.text(target.getName()))
                .append(Component.text("\" is now a pumpkin"))
        );
        return true;
    }
}
