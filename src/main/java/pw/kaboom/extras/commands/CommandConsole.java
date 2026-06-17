package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import pw.kaboom.extras.util.Utility;

public final class CommandConsole implements CommandExecutor {
    public boolean onCommand(final @NonNull CommandSender sender,
                             final @NonNull Command command,
                             final @NonNull String label,
                             final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component
                    .text("Usage: /" + label + " <message ..>",
                            NamedTextColor.RED));
            return true;
        }

        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "minecraft:say " + Utility.translateLegacyColors(String.join(" ", args))
        );
        return true;
    }
}
