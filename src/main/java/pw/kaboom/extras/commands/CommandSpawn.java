package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jspecify.annotations.NonNull;
import pw.kaboom.extras.util.Utility;

public final class CommandSpawn implements CommandExecutor {
    public boolean onCommand(final @NonNull CommandSender sender,
                             final @NonNull Command command,
                             final @NonNull String label,
                             final String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Component
                    .text("Command has to be run by a player"));
            return true;
        }

        Utility.teleportToSpawn(player, PlayerTeleportEvent.TeleportCause.COMMAND);
        player.sendMessage(Component
                .text("Successfully moved to spawn"));
        return true;
    }
}
