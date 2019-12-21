package pw.kaboom.extras.modules.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

public final class EntityKnockback implements Listener {
	@EventHandler
	void onEntityKnockbackByEntity(final EntityKnockbackByEntityEvent event) {
		final int knockbackLimit = 60;

		if (event.getKnockbackStrength() > knockbackLimit) {
			event.getAcceleration().multiply(
				knockbackLimit / event.getKnockbackStrength()
			);
		}
	}
}
