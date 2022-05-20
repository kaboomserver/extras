package pw.kaboom.extras.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pw.kaboom.extras.Main;

public final class CommandPrefix implements CommandExecutor {
    private static final File PREFIX_CONFIG_FILE = JavaPlugin
        .getPlugin(Main.class).getPrefixConfigFile();
    private static final FileConfiguration PREFIX_CONFIG = JavaPlugin
        .getPlugin(Main.class).getPrefixConfig();

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label,
                             final String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Command has to be run by a player");
        } else {
            final Player player = (Player) sender;

            try {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "Usage: /" + label + " <prefix|off>");
                } else if ("off".equalsIgnoreCase(args[0])) {
                    PREFIX_CONFIG.set(player.getUniqueId().toString(), null);
                    PREFIX_CONFIG.save(PREFIX_CONFIG_FILE);
                    player.sendMessage("You no longer have a tag");
                } else {
                    PREFIX_CONFIG.set(player.getUniqueId().toString(), String.join(" ", args));
                    PREFIX_CONFIG.save(PREFIX_CONFIG_FILE);
                    player.sendMessage("You now have the tag: "
                                       + ChatColor.translateAlternateColorCodes(
                                       '&', String.join(" ", args)));
                }
            } catch (Exception exception) {
                player.sendMessage("Something went wrong while saving the prefix. "
                                   + "Please check console.");
                exception.printStackTrace();
            }
        }
        return true;
    }
}
