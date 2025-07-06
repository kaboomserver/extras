package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class CommandBroadcastVanilla implements CommandExecutor {
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER =
            LegacyComponentSerializer
            .legacyAmpersand();

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

        final Component senderName = sender.name();
        final String input = String.join(" ", args);
        final Component component = LEGACY_COMPONENT_SERIALIZER.deserialize(input);
        final Component broadcastComponent =
                Component.translatable("chat.type.admin", senderName, component)
                .decorate(TextDecoration.ITALIC)
                .color(NamedTextColor.GRAY);

        sender.sendMessage(component);

        final Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        for (final Player onlinePlayer : onlinePlayers) {
            if (onlinePlayer.equals(sender)) {
                continue;
            }

            onlinePlayer.sendMessage(broadcastComponent);
        }

        final ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();
        consoleCommandSender.sendMessage(broadcastComponent);

        return true;
    }
}
