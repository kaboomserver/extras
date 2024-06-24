package pw.kaboom.extras.modules.player;

import io.papermc.paper.event.world.WorldGameRuleChangeEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pw.kaboom.extras.util.Utility;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public final class PlayerDamage implements Listener {
    private final Map<World, Boolean> deathMessageToggles = new IdentityHashMap<>();

    @EventHandler
    void onEntityDamage(final EntityDamageEvent event) {
        if (EntityType.PLAYER.equals(event.getEntityType())) {
            if (DamageCause.VOID.equals(event.getCause())
                    && event.getDamage() == Float.MAX_VALUE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    void onEntityRegainHealth(final EntityRegainHealthEvent event) {
        if (event.getAmount() < 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onFoodLevelChange(final FoodLevelChangeEvent event) {
        final HumanEntity player = event.getEntity();
        final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) return;
        if (attribute.getValue() <= 0) {
            Utility.resetAttribute(attribute);
            player.setHealth(20);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onGameRuleChange(final WorldGameRuleChangeEvent event) {
        if (event.getGameRule() != GameRule.SHOW_DEATH_MESSAGES) {
            return;
        }

        this.deathMessageToggles.put(event.getWorld(), Boolean.parseBoolean(event.getValue()));
    }

    @EventHandler
    void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Component deathMessage = event.deathMessage();

        if (deathMessage != null && this.deathMessageToggles.computeIfAbsent(
                player.getWorld(),
                (key) -> Objects.requireNonNullElse(
                        key.getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES),
                        key.getGameRuleDefault(GameRule.SHOW_DEATH_MESSAGES)
                )
        )) {
            Bukkit.broadcast(deathMessage);
        }

        try {
            if (!event.getKeepInventory()) {
                player.getInventory().clear();

                for (ItemStack item : event.getDrops()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }

            if (event.getDroppedExp() > 0) {
                ExperienceOrb xp = player.getWorld().spawn(player.getLocation(),
                                                           ExperienceOrb.class);
                xp.setExperience(event.getDroppedExp());
            }

            final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                Utility.resetAttribute(attribute);
            }

            player.setHealth(20);

            if (player.getBedSpawnLocation() != null) {
                player.teleportAsync(player.getBedSpawnLocation());
            } else {
                final World world = Bukkit.getWorld("world");
                player.teleportAsync(world.getSpawnLocation());
            }
        } catch (Exception exception) {
            final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                Utility.resetAttribute(attribute);
            }
            player.setHealth(20);
        }

        player.setExp(event.getNewExp());
        player.setLevel(event.getNewLevel());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setRemainingAir(player.getMaximumAir());

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        event.setCancelled(true);
    }
}
