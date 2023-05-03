package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.kaboom.extras.modules.player.PlayerPrefix;

import javax.annotation.Nonnull;

public final class CommandPrefix implements CommandExecutor {


    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command cmd,
                             final @Nonnull String label,
                             final String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Component
                    .text("Command has to be run by a player"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component
                    .text("Usage: /" + label + " <prefix|off>",
                            NamedTextColor.RED));
            return true;
        }

        try {
            if ("off".equalsIgnoreCase(args[0])) {
                PlayerPrefix.removePrefix(player);
                player.sendMessage(Component
                        .text("You no longer have a tag"));
            } else {
                final Component prefix = PlayerPrefix.setPrefix(player, String.join(" ", args));

                player.sendMessage(Component.text("You now have the tag: ")
                        .append(prefix));
             }
        } catch (Exception exception) {
            player.sendMessage(Component
                    .text("Something went wrong while saving the prefix. " +
                            "Please check console."));
            exception.printStackTrace();
        }
        return true;
    }
}
