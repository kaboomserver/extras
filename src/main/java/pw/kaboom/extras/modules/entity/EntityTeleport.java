package pw.kaboom.extras.modules.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityTeleportEvent;

public final class EntityTeleport implements Listener {
	@EventHandler
	void onEntityTeleport(final EntityTeleportEvent event) {
		event.setTo(EntitySpawn.limitLocation(event.getTo()));
	}
}
