package pw.kaboom.extras.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class CommandPing implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final Player player = (Player) sender;
        final int ping = ((CraftPlayer) player).getHandle().playerConnection.player.ping;
        final int d = (int) Math.floor(ping / 100);
        ChatColor highlighting = ChatColor.WHITE;

        switch (d) {
            case 0:
                highlighting = ChatColor.GREEN;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                highlighting = ChatColor.YELLOW;
                break;
            case 5:
                highlighting = ChatColor.RED;
                break;
            default:
                if (d > 5) {
                    highlighting = ChatColor.DARK_RED;
                }
                break;
        }

        player.sendMessage("Your ping is " + highlighting + ping + "ms.");
        return true;
    }
}