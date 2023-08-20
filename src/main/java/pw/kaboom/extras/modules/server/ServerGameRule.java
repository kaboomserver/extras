package pw.kaboom.extras.modules.server;

import io.papermc.paper.event.world.WorldGameRuleChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import pw.kaboom.extras.Main;

public final class ServerGameRule implements Listener {
    @EventHandler
    void onGameRuleChange(final WorldGameRuleChangeEvent event) {
        final GameRule<?> gameRule = event.getGameRule();

        if ((gameRule == GameRule.RANDOM_TICK_SPEED
                && Integer.parseInt(event.getValue()) > 6)
            || (event.getGameRule() == GameRule.SPAWN_RADIUS
                && Integer.parseInt(event.getValue()) > 100)
            || (event.getGameRule() == GameRule.COMMAND_MODIFICATION_BLOCK_LIMIT
                && Integer.parseInt(event.getValue()) > 32768)) {
            event.setCancelled(true);
        }
    }

    private static void enableAutoSave() {
        for (final World world: Bukkit.getWorlds()) {
            world.setAutoSave(true);
        }
    }

    public static void init(final Main main) {
        final BukkitScheduler scheduler = Bukkit.getScheduler();

        scheduler.runTaskTimer(main, ServerGameRule::enableAutoSave, 0L, 600L); // 30 seconds
    }
}
