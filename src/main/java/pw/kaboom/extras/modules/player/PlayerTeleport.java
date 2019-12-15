package pw.kaboom.extras;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerTeleportEvent;

class PlayerTeleport implements Listener {
	@EventHandler
	void onPlayerTeleport(PlayerTeleportEvent event) {
		event.setTo(EntitySpawn.limitLocation(event.getTo()));
	}
}
