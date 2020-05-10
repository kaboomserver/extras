package pw.kaboom.extras.modules.server;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

public final class ServerTick implements Listener {
	@EventHandler
	void onPlayerStatisticIncrement(final PlayerStatisticIncrementEvent event) {
		final World world = event.getPlayer().getWorld();
		final Integer randomTickSpeed = world.getGameRuleValue(GameRule.RANDOM_TICK_SPEED);

		if (randomTickSpeed > 6) {
			world.setGameRule(GameRule.RANDOM_TICK_SPEED, 6);
		}

		final Integer spawnRadius = world.getGameRuleValue(GameRule.SPAWN_RADIUS);

		if (spawnRadius > 100) {
			world.setGameRule(GameRule.SPAWN_RADIUS, 100);
		}

		if (!world.isAutoSave()) {
			world.setAutoSave(true);
		}

		event.setCancelled(true);
	}
}
