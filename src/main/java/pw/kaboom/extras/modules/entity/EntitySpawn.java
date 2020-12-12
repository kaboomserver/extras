package pw.kaboom.extras.modules.entity;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Slime;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.weather.LightningStrikeEvent;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;

public final class EntitySpawn implements Listener {
	private void applyEntityChanges(final Entity entity) {
		switch (entity.getType()) {
			case AREA_EFFECT_CLOUD:
				final AreaEffectCloud cloud = (AreaEffectCloud) entity;

				limitAreaEffectCloudRadius(cloud);
				return;
			case MAGMA_CUBE:
			case SLIME:
				final Slime slime = (Slime) entity;

				limitSlimeSize(slime);
		default:
			break;
		}
	}

	private boolean checkShouldRemoveEntities(final World world) {
		final int worldEntityCount = world.getEntities().size();

		if (worldEntityCount > 1024) {
			for (Entity entity : world.getEntities()) {
				if (!EntityType.PLAYER.equals(entity.getType())) {
					entity.remove();
				}
			}
			return true;
		}
		return false;
	}

	private boolean isEntityLimitReached(final EntityType entityType, final Chunk chunk, final World world, final boolean isAddToWorldEvent) {
		/*
		Add 1 if EntitySpawnEvent triggered the method, due to the entity count being
		one larger in EntityAddToWorldEvent compared to EntitySpawnEvent
		This prevents EntityAddToWorldEvent from triggering an entity removal before
		EntitySpawnEvent's event cancel
		*/

		switch (entityType) {
		case ENDER_DRAGON:
			final int worldDragonCount =
			!isAddToWorldEvent ? world.getEntitiesByClass(EnderDragon.class).size() + 1
			: world.getEntitiesByClass(EnderDragon.class).size();
			final int worldDragonCountLimit = 24;

			if (worldDragonCount >= worldDragonCountLimit) {
				return true;
			}

			break;
		case PRIMED_TNT:
			final int worldTntCount =
			!isAddToWorldEvent ? world.getEntitiesByClass(TNTPrimed.class).size() + 1
			: world.getEntitiesByClass(TNTPrimed.class).size();
			final int worldTntCountLimit = 200;

			if (worldTntCount >= worldTntCountLimit) {
				return true;
			}

			break;
		default:
			if (!EntityType.PLAYER.equals(entityType)) {
				final int chunkEntityCount =
						!isAddToWorldEvent ? chunk.getEntities().length + 1
						: chunk.getEntities().length;
				final int chunkEntityCountLimit = 30;
				if (chunkEntityCount >= chunkEntityCountLimit) {
					return true;
				}
			}
			break;
		}
		return false;
	}

	private boolean isOutsideBoundaries(final double x, final double y, final double z) {
		final int maxValue = 30000000;
		final int minValue = -30000000;

		if (x > maxValue
				|| x < minValue
				|| y > maxValue
				|| y < minValue
				|| z > maxValue
				|| z < minValue) {
			return true;
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

	private void limitSlimeSize(final Slime slime) {
		if (slime.getSize() > 20) {
			slime.setSize(20);

		} else if (slime.getSize() < -20) {
			slime.setSize(-20);
		}
	}

	private void limitSpawner(final CreatureSpawner spawner) {
		if (EntityType.MINECART_MOB_SPAWNER.equals(spawner.getSpawnedType())) {
			spawner.setSpawnedType(EntityType.MINECART);
		}

		if (spawner.getDelay() > 100) {
			spawner.setMaxSpawnDelay(100);
			spawner.setDelay(100);
			spawner.update();
		}

		if (spawner.getSpawnCount() > 200) {
			spawner.setSpawnCount(200);
			spawner.update();
		}

		if (spawner.getSpawnRange() > 50) {
			spawner.setSpawnRange(50);
			spawner.update();
		}
	}

	@EventHandler
	void onAreaEffectCloudApply(final AreaEffectCloudApplyEvent event) {
		limitAreaEffectCloudRadius(event.getEntity());
	}

	@EventHandler
	void onEntityAddToWorld(final EntityAddToWorldEvent event) {
		final Entity entity = event.getEntity();
		final Chunk chunk = entity.getChunk();

		if (chunk.isLoaded()) {
			final double x = entity.getLocation().getX();
			final double y = entity.getLocation().getY();
			final double z = entity.getLocation().getZ();

			if (isOutsideBoundaries(x, y, z)) {
				entity.remove();
				return;
			}

			final World world = entity.getWorld();
			final EntityType entityType = entity.getType();
			final boolean isAddToWorldEvent = true;

			if (isEntityLimitReached(entityType, chunk, world, isAddToWorldEvent)
					&& !EntityType.PLAYER.equals(entity.getType())) {
				entity.remove();
				return;
			}

			if (checkShouldRemoveEntities(world)) {
				return;
			}
		}

		applyEntityChanges(entity);
	}

	@EventHandler
	void onExplosionPrime(final ExplosionPrimeEvent event) {
		if (EntityType.MINECART_TNT.equals(event.getEntityType())
				&& event.getEntity().getWorld().getEntitiesByClass(ExplosiveMinecart.class).size() > 80) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onEntitySpawn(final EntitySpawnEvent event) {
		final double x = event.getLocation().getX();
		final double y = event.getLocation().getY();
		final double z = event.getLocation().getZ();

		if (isOutsideBoundaries(x, y, z)) {
			event.setCancelled(true);
			return;
		}

		final EntityType entityType = event.getEntityType();
		final Chunk chunk = event.getLocation().getChunk();
		final World world = event.getLocation().getWorld();
		final boolean isAddToWorldEvent = false;

		if (isEntityLimitReached(entityType, chunk, world, isAddToWorldEvent)) {
			event.setCancelled(true);
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
		final double x = lightning.getLocation().getX();
		final double y = lightning.getLocation().getY();
		final double z = lightning.getLocation().getZ();

		if (isOutsideBoundaries(x, y, z)) {
			event.setCancelled(true);
			return;
		}

		final EntityType entityType = EntityType.LIGHTNING;
		final Chunk chunk = lightning.getChunk();
		final World world = event.getWorld();
		final boolean isAddToWorldEvent = false;

		if (isEntityLimitReached(entityType, chunk, world, isAddToWorldEvent)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onPreCreatureSpawn(final PreCreatureSpawnEvent event) {
		final EntityType mobType = event.getType();
		final Chunk chunk = event.getSpawnLocation().getChunk();
		final World world = event.getSpawnLocation().getWorld();
		final boolean isAddToWorldEvent = false;

		if (isEntityLimitReached(mobType, chunk, world, isAddToWorldEvent)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onPreSpawnerSpawn(final PreSpawnerSpawnEvent event) {
		try {
			limitSpawner((CreatureSpawner) event.getSpawnerLocation().getBlock().getState());
		} catch (Exception exception) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onSpawnerSpawn(final SpawnerSpawnEvent event) {
		if (EntityType.FALLING_BLOCK.equals(event.getEntityType())) {
			if (((FallingBlock) event.getEntity()).getBlockData().getMaterial().equals(Material.SPAWNER)) {
				event.setCancelled(true);
				event.getSpawner().setSpawnedType(EntityType.FALLING_BLOCK);
			}
		}
	}

	@EventHandler
	void onTNTPrime(final TNTPrimeEvent event) {
		switch (event.getReason()) {
		case EXPLOSION:
		case FIRE:
		case REDSTONE:
			if (new Random().nextBoolean()) {
				event.setCancelled(true);
			}
			return;
		default:
			break;
		}
	}

	@EventHandler
	void onVehicleCreate(final VehicleCreateEvent event) {
		final Vehicle vehicle = event.getVehicle();
		final double x = vehicle.getLocation().getX();
		final double y = vehicle.getLocation().getY();
		final double z = vehicle.getLocation().getZ();

		if (isOutsideBoundaries(x, y, z)) {
			event.setCancelled(true);
			return;
		}

		final EntityType entityType = vehicle.getType();
		final Chunk chunk = vehicle.getChunk();
		final World world = vehicle.getWorld();
		final boolean isAddToWorldEvent = false;

		if (isEntityLimitReached(entityType, chunk, world, isAddToWorldEvent)) {
			event.setCancelled(true);
			return;
		}
	}
}
