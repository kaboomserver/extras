package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.ShulkerBox;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Vehicle;

import org.bukkit.event.block.BlockDispenseEvent;

import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import org.bukkit.event.player.PlayerDropItemEvent;

import org.bukkit.event.vehicle.VehicleCreateEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.destroystokyo.paper.event.block.TNTPrimeEvent.PrimeReason;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;

import org.bukkit.block.banner.Pattern;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitRunnable;

class EntitySpawn implements Listener {
	private void applyEntityChanges(Entity entity) {
		final World world = entity.getWorld();
		
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
		}
	}

	private boolean checkEntityLimits(EntityType entityType, Chunk chunk, World world, boolean isAddToWorldEvent) {
		final int chunkEntityCount = chunk.getEntities().length;
		final int chunkEntityCountLimit = 50;

		final int worldDragonCount = world.getEntitiesByClass(EnderDragon.class).size();
		final int worldDragonCountLimit = 24;

		if ((entityType != EntityType.PLAYER &&
			isEntityLimitReached(chunkEntityCount, chunkEntityCountLimit, isAddToWorldEvent)) ||
			
			(entityType == EntityType.ENDER_DRAGON &&
			isEntityLimitReached(worldDragonCount, worldDragonCountLimit, isAddToWorldEvent))) {
			return true;
		}
		return false;
	}

	private boolean checkShouldRemoveEntities(World world) {
		final int worldEntityCount = world.getEntities().size();

		if (worldEntityCount > 1024) {
			for (Entity entity : world.getEntities()) {
				if (entity.getType() != EntityType.PLAYER) {
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
		
		System.out.println(X);
		System.out.println(Y);
		System.out.println(Z);
		
		return new Location(location.getWorld(), X, Y, Z);
	}
	
	private void limitSlimeSize(Slime slime) {
		if (slime.getSize() > 50) {
			slime.setSize(50);
		}
	}

	private void limitSpawner(CreatureSpawner spawner) {
		if (spawner.getSpawnedType() == EntityType.FALLING_BLOCK) {
			if (spawner.getDelay() > 100) {
				spawner.setMaxSpawnDelay(100);
				spawner.setDelay(100);
				spawner.update();
			}
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
			
			if (checkEntityLimits(entityType, chunk, world, isAddToWorldEvent)) {
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
			final CreatureSpawner spawner = (CreatureSpawner) event.getSpawnerLocation().getBlock().getState();

			if (spawner.getSpawnedType() == EntityType.MINECART_MOB_SPAWNER) {
				event.setCancelled(true);
				return;
			}
			
			limitSpawner(spawner);
		} catch (Exception exception) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onSpawnerSpawn(SpawnerSpawnEvent event) {
		if (event.getEntity().getType() == EntityType.FALLING_BLOCK) {
			final FallingBlock block = (FallingBlock) event.getEntity();

			if (block.getBlockData().getMaterial() == Material.SPAWNER) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onTNTPrime(TNTPrimeEvent event) {
		if (event.getBlock().getWorld().getEntitiesByClass(TNTPrimed.class).size() > 120) {
			if (event.getReason() == PrimeReason.EXPLOSION) {
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
