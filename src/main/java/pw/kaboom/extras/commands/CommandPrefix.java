package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;

import javax.annotation.Nonnull;
import java.io.File;

public final class CommandPrefix implements CommandExecutor {
    private static final File PREFIX_CONFIG_FILE = JavaPlugin
            .getPlugin(Main.class).getPrefixConfigFile();
    private static final FileConfiguration PREFIX_CONFIG = JavaPlugin
            .getPlugin(Main.class).getPrefixConfig();

    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command cmd,
                             final @Nonnull String label,
                             final String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Component
                    .text("Command has to be run by a player"));
            return true;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Component
                    .text("Usage: /" + label + " <prefix|off>",
                            NamedTextColor.RED));
            return true;
        }

        try {
            if ("off".equalsIgnoreCase(args[0])) {
                PREFIX_CONFIG.set(player.getUniqueId().toString(), null);
                PREFIX_CONFIG.save(PREFIX_CONFIG_FILE);
                player.sendMessage(Component
                        .text("You no longer have a tag"));
            } else {
                PREFIX_CONFIG.set(player.getUniqueId().toString(), String.join(" ", args));
                PREFIX_CONFIG.save(PREFIX_CONFIG_FILE);
                player.sendMessage(Component.text("You now have the tag: ")
                        .append(LegacyComponentSerializer.legacyAmpersand()
                                .deserialize(String.join(" ", args))));
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
