package pw.kaboom.extras.modules.entity;

import io.papermc.paper.event.entity.EntityKnockbackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import pw.kaboom.extras.Main;

public final class EntityKnockback implements Listener {
    // default translates to enchantment level 40
    private static final double KNOCKBACK_LIMIT = JavaPlugin.getPlugin(Main.class)
            .getConfig()
            .getDouble("maxKnockbackVelocity");
    private static final double KNOCKBACK_LIMIT_SQUARED = KNOCKBACK_LIMIT * KNOCKBACK_LIMIT;

    @EventHandler
    void onEntityKnockbackByEntity(final EntityKnockbackEvent event) {
        final Vector knockback = event.getKnockback();
        final double length = knockback.lengthSquared();
        if (length > KNOCKBACK_LIMIT_SQUARED) {
            event.setKnockback(knockback.normalize()
                    .multiply(KNOCKBACK_LIMIT));
        }
    }
}
