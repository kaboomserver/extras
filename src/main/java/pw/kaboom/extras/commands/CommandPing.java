package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class CommandPing implements CommandExecutor {

    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        Player target;

        if (args.length == 0) {
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
        }

        if (target == null) {
            sender.sendMessage(Component
                    .text("Player \"" + args[0] + "\" not found"));
            return true;
        }

        final int ping = target.spigot().getPing();
        final int d = (int) Math.floor((float) ping / 100);
        NamedTextColor highlighting = NamedTextColor.WHITE;

        switch (d) {
            case 0:
                highlighting = NamedTextColor.GREEN;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                highlighting = NamedTextColor.YELLOW;
                break;
            case 5:
                highlighting = NamedTextColor.RED;
                break;
            default:
                if (d > 5) {
                    highlighting = NamedTextColor.DARK_RED;
                }
                break;
        }

        sender.sendMessage(Component.text((args.length == 0 ?
                        "Your" : target.getName() + "'s") + " ping is ")
                .append(Component.text(ping, highlighting))
                .append(Component.text("ms.", highlighting)));
        return true;
    }
}
