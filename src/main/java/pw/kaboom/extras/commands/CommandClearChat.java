package pw.kaboom.extras.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public final class CommandClearChat implements CommandExecutor {
    public boolean onCommand(final CommandSender sender, final Command command, final String label,
                             final String[] args) {
        final int maxMessages = 100;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < maxMessages; ++i) {
                onlinePlayer.sendMessage("");
            }
            onlinePlayer.sendMessage(ChatColor.DARK_GREEN + "The chat has been cleared");
        }
        return true;
    }
}
