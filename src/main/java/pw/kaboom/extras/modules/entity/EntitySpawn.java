package pw.kaboom.extras.modules.entity;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.destroystokyo.paper.event.block.TNTPrimeEvent.PrimeReason;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import org.bukkit.event.weather.LightningStrikeEvent;

public class EntitySpawn implements Listener {
	private void applyEntityChanges(Entity entity) {
		if (entity instanceof LivingEntity) {
			final LivingEntity mob = (LivingEntity) entity;

			limitFollowAttribute(mob);
		}

		switch (entity.getType()) {
			case AREA_EFFECT_CLOUD:
				final AreaEffectCloud cloud = (AreaEffectCloud) entity;

				limitAreaEffectCloudRadius(cloud);
				break;
			case MAGMA_CUBE:
			case SLIME:
				final Slime slime = (Slime) entity;

				limitSlimeSize(slime);
		default:
			break;
		}
	}

	private boolean checkEntityLimits(EntityType entityType, Chunk chunk, World world, boolean isAddToWorldEvent) {
		final int chunkEntityCount = chunk.getEntities().length;
		final int chunkEntityCountLimit = 30;

		final int worldDragonCount = world.getEntitiesByClass(EnderDragon.class).size();
		final int worldDragonCountLimit = 24;

		if ((!EntityType.PLAYER.equals(entityType) &&
			isEntityLimitReached(chunkEntityCount, chunkEntityCountLimit, isAddToWorldEvent)) ||

			(EntityType.ENDER_DRAGON.equals(entityType) &&
			isEntityLimitReached(worldDragonCount, worldDragonCountLimit, isAddToWorldEvent))) {
			return true;
		}
		return false;
	}

	private boolean checkShouldRemoveEntities(World world) {
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

	private boolean isEntityLimitReached(int count, int countLimit, boolean isAddToWorldEvent) {
		/*
			Add 1 if EntitySpawnEvent triggered the method, due to the entity count being
			one larger in EntityAddToWorldEvent compared to EntitySpawnEvent
			This prevents EntityAddToWorldEvent from triggering an entity removal before
			EntitySpawnEvent's event cancel
		*/
		if (!isAddToWorldEvent) {
			count += 1;
		}

		if (count >= countLimit) {
			return true;
		}
		return false;
	}

	private boolean isOutsideBoundaries(double X, double Y, double Z) {
		int maxValue = 30000000;
		int minValue = -30000000;

		if (X > maxValue ||
			X < minValue ||
			Y > maxValue ||
			Y < minValue ||
			Z > maxValue ||
			Z < minValue) {
			return true;
		}
		return false;
	}

	private void limitAreaEffectCloudRadius(AreaEffectCloud cloud) {
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

	private void limitFollowAttribute(LivingEntity mob) {
		final AttributeInstance followAttribute = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

		if (followAttribute != null &&
			followAttribute.getBaseValue() > 40) {
			followAttribute.setBaseValue(40);
		}
	}

	public static Location limitLocation(Location location) {
		double X = location.getX();
		double Y = location.getY();
		double Z = location.getZ();

		int maxValue = 30000000;
		int minValue = -30000000;

		if (X > maxValue)
			X = maxValue;
		if (X < minValue)
			X = minValue;
		if (Y > maxValue)
			Y = maxValue;
		if (Y < minValue)
			Y = minValue;
		if (Z > maxValue)
			Z = maxValue;
		if (Z < minValue)
			Z = minValue;

		return new Location(location.getWorld(), X, Y, Z);
	}

	private void limitSlimeSize(Slime slime) {
		if (slime.getSize() > 50) {
			slime.setSize(50);
		}
	}

	private void limitSpawner(CreatureSpawner spawner) {
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
	void onAreaEffectCloudApply(AreaEffectCloudApplyEvent event) {
		limitAreaEffectCloudRadius(event.getEntity());
	}

	@EventHandler
	void onEntityAddToWorld(EntityAddToWorldEvent event) {
		final Entity entity = event.getEntity();
		final double X = entity.getLocation().getX();
		final double Y = entity.getLocation().getY();
		final double Z = entity.getLocation().getZ();

		if (isOutsideBoundaries(X, Y, Z)) {
			entity.remove();
			return;
		}

		final World world = entity.getWorld();
		final Chunk chunk = entity.getChunk();

		if (chunk.isLoaded()) {
			final EntityType entityType = entity.getType();
			final boolean isAddToWorldEvent = true;

			if (checkEntityLimits(entityType, chunk, world, isAddToWorldEvent)
					&& !EntityType.PLAYER.equals(entity.getType())) {
				entity.remove();
				return;
			}
		}

		applyEntityChanges(entity);

		if (chunk.isLoaded()) {
			checkShouldRemoveEntities(world);
		}
	}

	@EventHandler
	void onEntitySpawn(EntitySpawnEvent event) {
		final double X = event.getLocation().getX();
		final double Y = event.getLocation().getY();
		final double Z = event.getLocation().getZ();

		if (isOutsideBoundaries(X, Y, Z)) {
			event.setCancelled(true);
			return;
		}

		final Entity entity = event.getEntity();
		final EntityType entityType = entity.getType();
		final Chunk chunk = entity.getChunk();
		final World world = entity.getWorld();
		final boolean isAddToWorldEvent = false;

		if (checkEntityLimits(entityType, chunk, world, isAddToWorldEvent)) {
			event.setCancelled(true);
			return;
		}

		applyEntityChanges(entity);
	}

	@EventHandler
	void onLightningStrike(LightningStrikeEvent event) {
		final LightningStrike lightning = event.getLightning();
		final double X = lightning.getLocation().getX();
		final double Y = lightning.getLocation().getY();
		final double Z = lightning.getLocation().getZ();

		if (isOutsideBoundaries(X, Y, Z)) {
			event.setCancelled(true);
			return;
		}

		final EntityType entityType = lightning.getType();
		final Chunk chunk = lightning.getChunk();
		final World world = lightning.getWorld();
		final boolean isAddToWorldEvent = false;

		if (checkEntityLimits(entityType, chunk, world, isAddToWorldEvent)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onPreCreatureSpawn(PreCreatureSpawnEvent event) {
		final EntityType mobType = event.getType();
		final Chunk chunk = event.getSpawnLocation().getChunk();
		final World world = event.getSpawnLocation().getWorld();
		final boolean isAddToWorldEvent = false;

		if (checkEntityLimits(mobType, chunk, world, isAddToWorldEvent)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onPreSpawnerSpawn(PreSpawnerSpawnEvent event) {
		try {
			limitSpawner((CreatureSpawner) event.getSpawnerLocation().getBlock().getState());
		} catch (Exception exception) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onSpawnerSpawn(SpawnerSpawnEvent event) {
		if (EntityType.FALLING_BLOCK.equals(event.getEntityType())) {
			if (((FallingBlock) event.getEntity()).getBlockData().getMaterial().equals(Material.SPAWNER)) {
				event.setCancelled(true);
				event.getSpawner().setSpawnedType(EntityType.FALLING_BLOCK);
			}
		}
	}

	@EventHandler
	void onTNTPrime(TNTPrimeEvent event) {
		if (event.getBlock().getWorld().getEntitiesByClass(TNTPrimed.class).size() > 120) {
			if (PrimeReason.EXPLOSION.equals(event.getReason())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onVehicleCreate(VehicleCreateEvent event) {
		final Vehicle vehicle = event.getVehicle();
		final double X = vehicle.getLocation().getX();
		final double Y = vehicle.getLocation().getY();
		final double Z = vehicle.getLocation().getZ();

		if (isOutsideBoundaries(X, Y, Z)) {
			event.setCancelled(true);
			return;
		}

		final EntityType entityType = vehicle.getType();
		final Chunk chunk = vehicle.getChunk();
		final World world = vehicle.getWorld();
		final boolean isAddToWorldEvent = false;

		if (checkEntityLimits(entityType, chunk, world, isAddToWorldEvent)) {
			event.setCancelled(true);
			return;
		}
	}
}
