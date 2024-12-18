package pw.kaboom.extras.modules.server;

import io.papermc.paper.event.world.WorldGameRuleChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.modules.entity.EntitySpawn;

import java.util.Map;

public final class ServerGameRule implements Listener {
    private static final Map<GameRule<Integer>, Integer> GAMERULE_LIMITS = Map.of(
            GameRule.RANDOM_TICK_SPEED, 6,
            GameRule.SPAWN_RADIUS, 100,
            GameRule.COMMAND_MODIFICATION_BLOCK_LIMIT, 32768,
            GameRule.MAX_COMMAND_FORK_COUNT, EntitySpawn.MAX_ENTITIES_PER_WORLD
    );

    @EventHandler
    void onGameRuleChange(final WorldGameRuleChangeEvent event) {
        final GameRule<?> gameRule = event.getGameRule();

        final Integer limit = GAMERULE_LIMITS.get(gameRule);
        if (limit == null) {
            return;
        }

        final int value = Integer.parseInt(event.getValue());
        if (value > limit) {
            event.setValue(limit.toString());
        }
    }

    private static void enableAutoSave() {
        for (final World world : Bukkit.getWorlds()) {
            world.setAutoSave(true);
        }
    }

    private static void fixGameRules() {
        for (final World world : Bukkit.getWorlds()) {
            for (final var entry : GAMERULE_LIMITS.entrySet()) {
                final GameRule<Integer> gameRule = entry.getKey();
                final int limit = entry.getValue();

                final Integer value = world.getGameRuleValue(gameRule) != null
                        ? world.getGameRuleValue(gameRule) : world.getGameRuleDefault(gameRule);

                if (value == null || value > limit) {
                    world.setGameRule(gameRule, limit);
                }
            }
        }
    }

    public static void init(final Main main) {
        final BukkitScheduler scheduler = Bukkit.getScheduler();

        scheduler.runTask(main, ServerGameRule::fixGameRules); // Right before server fully starts
        scheduler.runTaskTimer(main, ServerGameRule::enableAutoSave, 0L, 600L); // 30 seconds
    }
}
