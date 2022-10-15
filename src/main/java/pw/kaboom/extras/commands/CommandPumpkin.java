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
                    sender.sendMessage("Player \"" + args[0] + "\" not found");
                }
            }
        }
        return true;
    }
}
