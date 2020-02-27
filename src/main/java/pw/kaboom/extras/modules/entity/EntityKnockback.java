package pw.kaboom.extras.modules.entity;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

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

	@EventHandler
	void onProjectileHit(final ProjectileHitEvent event) {
		if (event.getHitEntity() != null
				&& EntityType.ARROW.equals(event.getEntityType())) {
			final Arrow arrow = (Arrow) event.getEntity();
			final int knockbackLimit = 60;

			if (arrow.getKnockbackStrength() > knockbackLimit) {
				arrow.setKnockbackStrength(knockbackLimit);
			}
		}
	}
}
