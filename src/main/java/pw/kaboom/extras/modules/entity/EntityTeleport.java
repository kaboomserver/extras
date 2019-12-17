package pw.kaboom.extras.modules.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityTeleportEvent;

public class EntityTeleport implements Listener {
	@EventHandler
	void onEntityTeleport(EntityTeleportEvent event) {
		event.setTo(EntitySpawn.limitLocation(event.getTo()));
	}
}
