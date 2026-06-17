package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public final class CommandTellraw implements CommandExecutor {
    public boolean onCommand(final @NonNull CommandSender sender,
                             final @NonNull Command command,
                             final @NonNull String label,
                             final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component
                    .text("Usage: /" + label + " <message ..>", NamedTextColor.RED));
            return true;
        }

        final Component message = LegacyComponentSerializer.legacyAmpersand()
                .deserialize(String.join(" ", args));

        for (Player onlinePlayer: Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(message);
        }
        return true;
    }
}
