package pw.kaboom.extras.modules.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.papermc.paper.event.player.PlayerSignCommandPreprocessEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.modules.server.ServerCommand;

public final class PlayerCommand implements Listener {
    private static final Map<UUID, Long> LAST_COMMAND_EXEC = new HashMap<>();
    private static final long MIN_COMMAND_DELAY_MILLIS = JavaPlugin.getPlugin(Main.class)
            .getConfig()
            .getLong("minCommandDelayMillis");

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        final UUID playerUuid = event.getPlayer().getUniqueId();

        if (getLastCommandExec().get(playerUuid) != null) {
            final long lastCommandTime = getLastCommandExec().get(playerUuid);
            final long millisDifference = System.currentTimeMillis() - lastCommandTime;

            if (millisDifference < MIN_COMMAND_DELAY_MILLIS) {
                event.setCancelled(true);
                return;
            }
        }

        getLastCommandExec().put(playerUuid, System.currentTimeMillis());

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

    public static Map<UUID, Long> getLastCommandExec() {
        return LAST_COMMAND_EXEC;
    }
}
