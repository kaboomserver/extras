package pw.kaboom.extras.modules.player;

import java.util.HashMap;
import java.util.UUID;

import io.papermc.paper.event.player.PlayerSignCommandPreprocessEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import pw.kaboom.extras.modules.server.ServerCommand;

public final class PlayerCommand implements Listener {
    private static HashMap<UUID, Long> commandMillisList = new HashMap<UUID, Long>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        final UUID playerUuid = event.getPlayer().getUniqueId();

        if (getCommandMillisList().get(playerUuid) != null) {
            final long lastCommandTime = getCommandMillisList().get(playerUuid);
            final long millisDifference = System.currentTimeMillis() - lastCommandTime;

            if (millisDifference < 75) {
                event.setCancelled(true);
                return;
            }
        }

        getCommandMillisList().put(playerUuid, System.currentTimeMillis());

        final CommandSender sender = event.getPlayer();
        final String command = event.getMessage();
        final boolean isConsoleCommand = false;
        final String checkedCommand = ServerCommand.checkCommand(sender, command, isConsoleCommand);

        if (checkedCommand != null) {
            if ("cancel".equals(checkedCommand)) {
                event.setCancelled(true);
            } else {
                event.setMessage(checkedCommand);
            }
        }
    }

    @EventHandler
    void onPlayerSignCommandPreprocess(final PlayerSignCommandPreprocessEvent event) {
        this.onPlayerCommandPreprocess(event);
    }

    public static HashMap<UUID, Long> getCommandMillisList() {
        return commandMillisList;
    }
}
