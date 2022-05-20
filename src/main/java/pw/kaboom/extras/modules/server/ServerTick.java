package pw.kaboom.extras.modules.server;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

public final class ServerTick implements Listener {
    @EventHandler
    void onPlayerStatisticIncrement(final PlayerStatisticIncrementEvent event) {
        final World world = event.getPlayer().getWorld();

        if (!world.isAutoSave()) {
            world.setAutoSave(true);
        }

        event.setCancelled(true);
    }
}
