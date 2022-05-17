package pw.kaboom.extras.modules.entity;

import java.security.SecureRandom;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
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
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import pw.kaboom.extras.Main;

public final class EntitySpawn implements Listener {
	private static final Main PLUGIN = JavaPlugin.getPlugin(Main.class);
	private static final FileConfiguration CONFIG = PLUGIN.getConfig();
	private static final SecureRandom RANDOM = new SecureRandom();

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

	private boolean isEntityLimitReached(final EntityType entityType, final Chunk chunk, final World world) {
		switch (entityType) {
		case ENDER_DRAGON:
			final int worldDragonCount = world.getEntitiesByClass(EnderDragon.class).size();
			final int worldDragonCountLimit = 24;

			if (worldDragonCount >= worldDragonCountLimit) {
				return true;
			}

			break;
		case PRIMED_TNT:
			final int worldTntCount = world.getEntitiesByClass(TNTPrimed.class).size();
			final int worldTntCountLimit = 200;

			if (worldTntCount >= worldTntCountLimit) {
				return true;
			}

			break;
		default:
			if (!EntityType.PLAYER.equals(entityType)) {
				final int chunkEntityCount = chunk.getEntities().length;
				final int chunkEntityCountLimit = CONFIG.getInt("maxEntitiesPerChunk");

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
	void onExplosionPrime(final ExplosionPrimeEvent event) {
		if (EntityType.MINECART_TNT.equals(event.getEntityType())
				&& event.getEntity().getWorld().getEntitiesByClass(ExplosiveMinecart.class).size() > 80) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onPlayerSpawn(final PlayerSpawnLocationEvent event) {
		final World world = event.getPlayer().getWorld();
		final WorldBorder worldBorder = world.getWorldBorder();

		if (CONFIG.getBoolean("randomizeSpawn") && event.getPlayer().getBedSpawnLocation() != event.getSpawnLocation()) {
			event.setSpawnLocation(new Location(world, RANDOM.nextDouble(-300000000D, 30000000D) + .5, 100D, RANDOM.nextDouble(-300000000D, 30000000D) + .5));
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

		if (isEntityLimitReached(entityType, chunk, world)) {
			event.setCancelled(true);
			return;
		}

		checkShouldRemoveEntities(world);
	}
}
