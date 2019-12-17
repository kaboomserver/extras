package pw.kaboom.extras.modules.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerTeleportEvent;

import pw.kaboom.extras.modules.entity.EntitySpawn;

public class PlayerTeleport implements Listener {
	@EventHandler
	void onPlayerTeleport(PlayerTeleportEvent event) {
		event.setTo(EntitySpawn.limitLocation(event.getTo()));
	}
}
