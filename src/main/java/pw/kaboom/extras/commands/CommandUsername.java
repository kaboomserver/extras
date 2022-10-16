package pw.kaboom.extras.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class CommandUsername implements CommandExecutor {
    private long millis;

    @Override
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Component
                    .text("Command has to be run by a player"));
            return true;
        }

        final Player player = (Player) sender;
        final String nameColor = ChatColor.translateAlternateColorCodes(
                '&', String.join(" ", args));
        final String name = nameColor.substring(0, Math.min(16, nameColor.length()));
        final long millisDifference = System.currentTimeMillis() - millis;

        if (args.length == 0) {
            player.sendMessage(Component
                    .text("Usage: /" + label + " <username>",
                            NamedTextColor.RED));
            return true;
        }

        if (name.equals(player.getName())) {
            player.sendMessage(Component
                    .text("You already have the username \"" + name + "\""));
            return true;
        }

        if (millisDifference <= 2000) {
            player.sendMessage(Component
                    .text("Please wait a few seconds before changing your username."));
            return true;
        }

        if (Bukkit.getPlayer(name) != null) {
            player.sendMessage(Component
                    .text("A player with that username is already logged in."));
            return true;
        }

        final PlayerProfile profile = player.getPlayerProfile();

        profile.setName(name);  // FIXME: Marked for removal
        player.setPlayerProfile(profile);
        millis = System.currentTimeMillis();

        player.sendMessage(Component
                .text("Successfully set your username to \"" + name + "\""));
        return true;
    }
}
