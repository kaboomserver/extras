package pw.kaboom.extras;

import org.bukkit.entity.Fireball;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.ExplosionPrimeEvent;

class EntityExplosion implements Listener {
	@EventHandler
	void onExplosionPrime(ExplosionPrimeEvent event) {
		if (event.getRadius() > 20) {
			event.setRadius(20);
		}
		
		if (event.getEntity().getWorld().getEntitiesByClass(Fireball.class).size() > 30 &&
			event.getRadius() > 1) {
			event.setRadius(1);	
		}
	}
}
