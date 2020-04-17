package pw.kaboom.extras.modules.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import pw.kaboom.extras.modules.entity.EntityTeleport;

public final class PlayerTeleport implements Listener {
	@EventHandler
	void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();

		if (player.getMaxHealth() <= 0) {
			player.setMaxHealth(Double.POSITIVE_INFINITY);
			player.setHealth(20);
			player.setMaxHealth(20);
		}
	}

	@EventHandler
	void onPlayerTeleport(final PlayerTeleportEvent event) {
		event.setTo(EntityTeleport.limitLocation(event.getTo()));
	}
}
