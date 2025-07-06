package pw.kaboom.extras.modules.entity;

import io.papermc.paper.event.entity.EntityKnockbackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.util.Vector;

public final class EntityKnockback implements Listener {
    private static final double KNOCKBACK_LIMIT = 20; // translates to enchantment level 40
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
