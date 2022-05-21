package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public final class CommandClearChat implements CommandExecutor {
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        final int maxMessages = 100;
        Component clearChatComponent = Component.empty();

        for (int i = 0; i < maxMessages; i++) {
            clearChatComponent = clearChatComponent
                    .append(Component.newline());
        }

        Bukkit.getServer().broadcast(clearChatComponent
                .append(Component
                        .text("The chat has been cleared",
                                NamedTextColor.DARK_GREEN)));

        return true;
    }
}
