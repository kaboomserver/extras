package pw.kaboom.extras.modules.entity;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AbstractCubeMob;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.weather.LightningStrikeEvent;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.util.Utility;

public final class EntitySpawn implements Listener {
    private static final FileConfiguration CONFIG = Main.PLUGIN.getConfig();

    private static final int MAX_ENTITIES_PER_CHUNK = CONFIG.getInt("maxEntitiesPerChunk");
    public static final int MAX_ENTITIES_PER_WORLD = CONFIG.getInt("maxEntitiesPerWorld");
    private static final int MAX_TNTS_PER_WORLD = CONFIG.getInt("maxTntsPerWorld");

    private void applyEntityChanges(final Entity entity) {
        switch (entity) {
            case final AreaEffectCloud cloud -> limitAreaEffectCloudRadius(cloud);
            case final AbstractCubeMob cube -> limitCubeSize(cube);
            default -> {}
        }
    }

    private boolean checkShouldRemoveEntities(final World world) {
        if (world.getEntityCount() <= MAX_ENTITIES_PER_CHUNK) return false;

        for (final Entity entity : world.getEntities()) {
            if (EntityType.PLAYER.equals(entity.getType())) continue;

            try {
                entity.remove();
            } catch (final Exception ignored) {
                // Broken entity
            }
        }

        return true;
    }

    private boolean isEntityLimitReached(final EntityType entityType, final Chunk chunk,
                                         final World world) {
        switch (entityType) {
        case PLAYER:
            break;
        case ENDER_DRAGON:
            final int worldDragonCount = world.getEntitiesByClass(EnderDragon.class).size();
            final int worldDragonCountLimit = 24;

            if (worldDragonCount >= worldDragonCountLimit) {
                return true;
            }
            break;
        case TNT:
            final int worldTntCount = world.getEntitiesByClass(TNTPrimed.class).size();

            if (worldTntCount >= MAX_TNTS_PER_WORLD) {
                return true;
            }
            break;
        default:
            final int chunkEntityCount = chunk.getEntities().length;

            if (chunkEntityCount >= MAX_ENTITIES_PER_CHUNK) {
                return true;
            }
            break;
        }
        return false;
    }

    private void limitAreaEffectCloudRadius(final AreaEffectCloud cloud) {
        if (cloud.getRadius() > 40) {
            cloud.setRadius(40);
        }

        if (cloud.getRadiusOnUse() > 0.01f) {
            cloud.setRadiusOnUse(0.1f);
        }

        if (cloud.getRadiusPerTick() > 0) {
            cloud.setRadiusPerTick(0);
        }
    }

    private void limitCubeSize(final AbstractCubeMob cube) {
        final AttributeInstance scaleInstance = cube.getAttribute(Attribute.SCALE);
        final double scale = scaleInstance != null ? scaleInstance.getValue() : 1.0f;

        if ((cube.getSize() * scale) > 20) {
            cube.setSize(20);
            Utility.resetAttribute(cube, Attribute.SCALE);
        }
    }

    private void limitSpawner(final CreatureSpawner spawner) {
        if (EntityType.SPAWNER_MINECART.equals(spawner.getSpawnedType())) {
            spawner.setSpawnedType(EntityType.MINECART);
        } else if (spawner.getSpawnedEntity() instanceof final FallingBlock block
            && block.getBlockData().getMaterial().equals(Material.SPAWNER)) {
            spawner.setSpawnedType(EntityType.FALLING_BLOCK);
        }

        if (spawner.getMinSpawnDelay() < 1000) {
            spawner.setMinSpawnDelay(1000);
        }

        if (spawner.getMaxSpawnDelay() < 1000) {
            spawner.setMaxSpawnDelay(1000);
        }

        if (spawner.getSpawnCount() > 200) {
            spawner.setSpawnCount(200);
        }

        if (spawner.getSpawnRange() > 50) {
            spawner.setSpawnRange(50);
        }
    }

    @EventHandler
    void onAreaEffectCloudApply(final AreaEffectCloudApplyEvent event) {
        limitAreaEffectCloudRadius(event.getEntity());
    }

    @EventHandler
    void onExplosionPrime(final ExplosionPrimeEvent event) {
        if (EntityType.TNT_MINECART.equals(event.getEntityType())
                && event.getEntity().getWorld()
                .getEntitiesByClass(ExplosiveMinecart.class).size() > 80) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onEntitySpawn(final EntitySpawnEvent event) {
        final EntityType entityType = event.getEntityType();
        final Chunk chunk = event.getLocation().getChunk();
        final World world = event.getLocation().getWorld();

        if (isEntityLimitReached(entityType, chunk, world)) {
            event.setCancelled(true);
            return;
        }

        if (checkShouldRemoveEntities(world)) {
            return;
        }

        final Entity entity = event.getEntity();
        applyEntityChanges(entity);
    }

    @EventHandler
    void onItemSpawn(final ItemSpawnEvent event) {
        try {
            event.getEntity().getItemStack().getItemMeta();
        } catch (Exception exception) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onLightningStrike(final LightningStrikeEvent event) {
        final LightningStrike lightning = event.getLightning();
        final EntityType entityType = EntityType.LIGHTNING_BOLT;
        final Chunk chunk = lightning.getChunk();
        final World world = event.getWorld();

        if (isEntityLimitReached(entityType, chunk, world)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPreCreatureSpawn(final PreCreatureSpawnEvent event) {
        final EntityType mobType = event.getType();
        final Chunk chunk = event.getSpawnLocation().getChunk();
        final World world = event.getSpawnLocation().getWorld();

        if (isEntityLimitReached(mobType, chunk, world)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPreSpawnerSpawn(final PreSpawnerSpawnEvent event) {
        try {
            limitSpawner((CreatureSpawner) event.getSpawnerLocation().getBlock().getState(false));
        } catch (Exception exception) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onTNTPrime(final TNTPrimeEvent event) {
        if (event.getBlock()
                .getWorld().getEntitiesByClass(TNTPrimed.class).size() >= MAX_TNTS_PER_WORLD
                && ThreadLocalRandom.current().nextBoolean()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onVehicleCreate(final VehicleCreateEvent event) {
        final Vehicle vehicle = event.getVehicle();
        final EntityType entityType = vehicle.getType();
        final Chunk chunk = vehicle.getChunk();
        final World world = vehicle.getWorld();

        if (isEntityLimitReached(entityType, chunk, world)) {
            event.setCancelled(true);
            return;
        }

        checkShouldRemoveEntities(world);
    }
}
