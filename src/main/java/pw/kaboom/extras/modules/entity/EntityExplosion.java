package pw.kaboom.extras.modules.entity;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;

public final class EntityExplosion implements Listener {
    private static final FileConfiguration CONFIG = JavaPlugin.getPlugin(Main.class).getConfig();

    private static final int MAX_EXPLOSION_RADIUS = CONFIG.getInt("maxExplosionRadius");
    private static final int MAX_FIREBALL_COUNT = CONFIG.getInt("maxFireballCount");

    @EventHandler
    void onExplosionPrime(final ExplosionPrimeEvent event) {
        if (event.getRadius() > MAX_EXPLOSION_RADIUS) {
            event.setRadius(MAX_EXPLOSION_RADIUS);
        }

        final World world = event.getEntity().getWorld();

        if (world.getEntitiesByClass(Fireball.class).size() > MAX_FIREBALL_COUNT
                && event.getRadius() > 1) {
            event.setRadius(1);
        }
    }
}
