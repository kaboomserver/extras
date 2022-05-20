package pw.kaboom.extras.modules.entity;

import org.bukkit.World;

import org.bukkit.entity.Fireball;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.ExplosionPrimeEvent;

public final class EntityExplosion implements Listener {
    @EventHandler
    void onExplosionPrime(final ExplosionPrimeEvent event) {
        final int maxRadius = 20;

        if (event.getRadius() > maxRadius) {
            event.setRadius(maxRadius);
        }

        final World world = event.getEntity().getWorld();
        final int maxFireballCount = 30;

        if (world.getEntitiesByClass(Fireball.class).size() > maxFireballCount
                && event.getRadius() > 1) {
            event.setRadius(1);
        }
    }
}
