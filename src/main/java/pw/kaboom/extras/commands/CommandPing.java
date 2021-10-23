package pw.kaboom.extras.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandPing implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        Player target;

        if (args.length == 0) {
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
        }

        if (target == null) {
            sender.sendMessage("Player \"" + args[0] + "\" not found");
            return true;
        }

        final int ping = target.spigot().getPing();
        final int d = (int) Math.floor((float) ping / 100);
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

        if (args.length == 0) {
            sender.sendMessage("Your ping is " + highlighting + ping + "ms.");
        } else {
            sender.sendMessage(target.getName() + "'s ping is " + highlighting + ping + "ms.");
        }
        return true;
    }
}
