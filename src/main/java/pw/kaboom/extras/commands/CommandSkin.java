package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pw.kaboom.extras.helpers.SkinDownloader;

import javax.annotation.Nonnull;

public final class CommandSkin implements CommandExecutor {
    private long millis;

    @Override
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Component
                    .text("Command has to be run by a player"));
        } else {
            final Player player = (Player) sender;

            final long millisDifference = System.currentTimeMillis() - millis;

            if (args.length == 0) {
                player.sendMessage(Component
                        .text("Usage: /" + label + " <username>",
                                NamedTextColor.RED));
            } else if (millisDifference <= 2000) {
                player.sendMessage(Component
                        .text("Please wait a few seconds before changing your skin"));
            } else {
                final String name = args[0];
                final boolean shouldSendMessage = true;

                SkinDownloader skinDownloader = new SkinDownloader();
                skinDownloader.applySkin(player, name, shouldSendMessage);

                millis = System.currentTimeMillis();
            }
        }
        return true;
    }
}
