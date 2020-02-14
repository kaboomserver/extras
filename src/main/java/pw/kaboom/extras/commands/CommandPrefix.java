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
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;
			final File configFile = new File(JavaPlugin.getPlugin(Main.class).getDataFolder(), "prefixes.yml");
			final FileConfiguration prefixConfig = YamlConfiguration.loadConfiguration(configFile);

			try {
				if (args.length == 0) {
					player.sendMessage(ChatColor.RED + "Usage: /" + label + " <prefix|off>");
				} else if ("off".equalsIgnoreCase(args[0])) {
					prefixConfig.set(player.getUniqueId().toString(), null);
					prefixConfig.save(configFile);
					player.sendMessage("You no longer have a tag");
				} else {
					prefixConfig.set(player.getUniqueId().toString(), String.join(" ", args));
					prefixConfig.save(configFile);
					player.sendMessage("You now have the tag: " + ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
				}
			} catch (Exception exception) {
				player.sendMessage("Something went wrong while saving the prefix. Please check console.");
				exception.printStackTrace();
			}
		}
		return true;
	}
}
