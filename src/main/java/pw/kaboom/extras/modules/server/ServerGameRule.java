package pw.kaboom.extras.modules.server;

import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.papermc.paper.event.world.WorldGameRuleChangeEvent;

public final class ServerGameRule implements Listener {
    @EventHandler
    void onGameRuleChange(final WorldGameRuleChangeEvent event) {
        final GameRule<?> gameRule = event.getGameRule();

        if ((gameRule == GameRule.RANDOM_TICK_SPEED
                && Integer.parseInt(event.getValue()) > 6)
            || (event.getGameRule() == GameRule.SPAWN_RADIUS
                && Integer.parseInt(event.getValue()) > 100)) {
            event.setCancelled(true);
        }
    }
}