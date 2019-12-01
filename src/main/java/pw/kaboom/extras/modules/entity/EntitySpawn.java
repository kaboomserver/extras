package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;
import org.bukkit.entity.TNTPrimed;

import org.bukkit.event.block.BlockDispenseEvent;

import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;

import  org.bukkit.block.banner.Pattern;

class EntitySpawn implements Listener {
	private boolean checkDragonWorldLimit(World world) {
		final int dragonCount = world.getEntitiesByClass(EnderDragon.class).size();

		if (dragonCount > 25) {
			return true;
		}
		return false;
	}
	
	private boolean checkEntityChunkLimit(Location location) {
		if (location.isChunkLoaded()) {
			final int count = location.getChunk().getEntities().length;

			if (count > 50) {
				return true;
			}
		}
		return false;
	}

	private boolean checkEntityWorldLimitRemove(World world) {
		if (world.getEntities().size() > 1024) {
			for (Entity entity : world.getEntities()) {
				if (entity.getType() != EntityType.PLAYER) {
					entity.remove();
				}
			}
			return true;
		}
		return false;
	}

	private void checkIllegalEquipment(LivingEntity mob) {
		try {
			for (ItemStack item : mob.getEquipment().getArmorContents()) {
				if (checkIllegalBannerItem(item)) {
					mob.getEquipment().setArmorContents(
						new ItemStack[] {null, null, null, null}
					);
				}
			}
		} catch (Exception exception) {
			mob.getEquipment().setArmorContents(
				new ItemStack[] {null, null, null, null}
			);
		}

		try {
			ItemStack item = mob.getEquipment().getItemInMainHand();

			if (checkIllegalBannerItem(item)) {
				mob.getEquipment().setItemInMainHand(null);
			}
		} catch (Exception exception) {
			mob.getEquipment().setItemInMainHand(null);
		}

		try {
			ItemStack item = mob.getEquipment().getItemInOffHand();

			if (checkIllegalBannerItem(item)) {
				mob.getEquipment().setItemInOffHand(null);
			}
		} catch (Exception exception) {
			mob.getEquipment().setItemInOffHand(null);
		}
	}
	
	private boolean checkIllegalBannerItem(ItemStack item) {
		if (item != null &&
			item.hasItemMeta()) {
			if (item.getItemMeta() instanceof BannerMeta) {
				final BannerMeta banner = (BannerMeta) item.getItemMeta();

				for (Pattern pattern : banner.getPatterns()) {
					if (pattern.getColor() == null) {
						return true;
					}
				}
			}
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
	void onBlockDispense(BlockDispenseEvent event) {
		try {
			event.getBlock().getState();
			event.getItem().getItemMeta();
		} catch (Exception exception) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onCreatureSpawn(CreatureSpawnEvent event) {
		final LivingEntity mob = event.getEntity();

		if (Main.spawnReasonList.contains(event.getSpawnReason())) {
			limitFollowAttribute(mob);
		}

		switch (event.getEntityType()) {
			case ARMOR_STAND:
			case DROWNED:
			case GIANT:
			case HUSK:
			case PIG_ZOMBIE:
			case PLAYER:
			case SKELETON:
			case STRAY:
			case WITHER_SKELETON:
			case ZOMBIE:
			case ZOMBIE_VILLAGER:
				checkIllegalEquipment(mob);
				break;
			case ENDER_DRAGON:
				final World world = event.getLocation().getWorld();

				if (checkDragonWorldLimit(world)) {
					event.setCancelled(true);
				}
				break;
			case MAGMA_CUBE:
			case SLIME: 
				final Slime slime = (Slime) mob;

				limitSlimeSize(slime);
		}
	}

	@EventHandler
	void onEntityAddToWorld(EntityAddToWorldEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) {
			final World world = event.getEntity().getWorld();
			final Entity entity = event.getEntity();

			if (!checkEntityWorldLimitRemove(world)) {
				if (checkEntityChunkLimit(entity.getLocation())) {
					entity.remove();
					return;
				}
			}
			
			switch (event.getEntityType()) {
				case ARMOR_STAND:
				case DROWNED:
				case GIANT:
				case HUSK:
				case PIG_ZOMBIE:
				case PLAYER:
				case SKELETON:
				case STRAY:
				case WITHER_SKELETON:
				case ZOMBIE:
				case ZOMBIE_VILLAGER:
					final LivingEntity mob = (LivingEntity) entity;
					
					checkIllegalEquipment(mob);
					break;
				case AREA_EFFECT_CLOUD:
					final AreaEffectCloud cloud = (AreaEffectCloud) entity;
					
					limitAreaEffectCloudRadius(cloud);
					break;
				case PRIMED_TNT: 
					if (world.getEntitiesByClass(TNTPrimed.class).size() > 180) {
						for (Entity tnt : world.getEntitiesByClass(TNTPrimed.class)) {
							tnt.remove();
						}
					}
			}
		}
	}

	@EventHandler
	void onEntitySpawn(EntitySpawnEvent event) {
		if (event.getEntityType() != EntityType.PLAYER &&
			checkEntityChunkLimit(event.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onItemSpawn(ItemSpawnEvent event) {
		try {
			event.getEntity().getItemStack().getItemMeta();
		} catch (Exception | StackOverflowError exception) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onPreCreatureSpawn(PreCreatureSpawnEvent event) {
		if ((event.getType() != EntityType.PLAYER &&
			checkEntityChunkLimit(event.getSpawnLocation())) ||
			
			(event.getType() == EntityType.ENDER_DRAGON &&
			checkDragonWorldLimit(event.getSpawnLocation().getWorld()))) {
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
		final double tps = Bukkit.getTPS()[0];
		final int tntCount = event.getBlock().getWorld().getEntitiesByClass(TNTPrimed.class).size();

		if (tps < 10 ||
			tntCount > 140) {
			event.setCancelled(true);
		}
	}
}
