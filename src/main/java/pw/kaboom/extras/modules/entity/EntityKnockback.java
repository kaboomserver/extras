package pw.kaboom.extras;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

class EntityKnockback implements Listener {
	@EventHandler
	void onEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
		if (event.getKnockbackStrength() > 100) {
			event.setCancelled(true);
		}
	}
}
