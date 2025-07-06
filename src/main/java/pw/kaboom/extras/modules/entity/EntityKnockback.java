package pw.kaboom.extras.modules.entity;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import org.bukkit.inventory.ItemStack;

public final class EntityKnockback implements Listener {
    @EventHandler
    void onEntityKnockbackByEntity(final EntityKnockbackByEntityEvent event) {
        final int knockbackLimit = 60;

        if (event.getKnockbackStrength() > knockbackLimit) {
            event.getKnockback().multiply(
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

            final ItemStack weapon = arrow.getWeapon();
            if (weapon == null) return;

            if (weapon.getEnchantmentLevel(Enchantment.KNOCKBACK) > knockbackLimit) {
                // replaces if already present
                weapon.addUnsafeEnchantment(Enchantment.KNOCKBACK, knockbackLimit);
                arrow.setWeapon(weapon);
            }
        }
    }
}
