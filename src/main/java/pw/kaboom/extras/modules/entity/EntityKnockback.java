package pw.kaboom.extras;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

class EntityKnockback implements Listener {
	@EventHandler
	void onEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
		final int knockbackLimit = 60;

		if (event.getKnockbackStrength() > knockbackLimit) {
			event.getAcceleration().multiply(
				knockbackLimit / event.getKnockbackStrength()
			);
		}
	}
}
