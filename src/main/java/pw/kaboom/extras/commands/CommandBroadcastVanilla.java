package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public final class CommandBroadcastVanilla implements CommandExecutor {
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component
                    .text("Usage: /" + label + " <message ..>",
                            NamedTextColor.RED));
            return true;
        }

        Command.broadcastCommandMessage(sender, ChatColor.translateAlternateColorCodes(
                '&', String.join(" ", args)));
        return true;
    }
}
