package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.ShulkerBox;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;

import org.bukkit.event.block.BlockDispenseEvent;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
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
	@EventHandler
	void onBlockDispense(BlockDispenseEvent event) {
		try {
			event.getItem().getItemMeta();
		} catch (Exception exception) {
			event.setCancelled(true);
		}

		/*try {
			BlockStateMeta state = (BlockStateMeta) event.getItem().getItemMeta();
			state.getBlockState();
		} catch (UnsupportedOperationException exception) {
			System.out.println("I caught: " + exception);
			event.setCancelled(true);
		}*/
	}

	@EventHandler
	void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.CUSTOM ||
			event.getSpawnReason() == SpawnReason.DEFAULT ||
			event.getSpawnReason() == SpawnReason.DISPENSE_EGG ||
			event.getSpawnReason() == SpawnReason.SPAWNER ||
			event.getSpawnReason() == SpawnReason.SPAWNER_EGG) {
			final LivingEntity mob = event.getEntity();
			final AttributeInstance followAttribute = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

			if (followAttribute != null &&
				followAttribute.getBaseValue() > 40) {
				followAttribute.setBaseValue(40);
			}
		}

		if (event.getEntityType() == EntityType.ARMOR_STAND ||
			event.getEntityType() == EntityType.DROWNED ||
			event.getEntityType() == EntityType.GIANT ||
			event.getEntityType() == EntityType.HUSK ||
			event.getEntityType() == EntityType.PIG_ZOMBIE ||
			event.getEntityType() == EntityType.PLAYER ||
			event.getEntityType() == EntityType.SKELETON ||
			event.getEntityType() == EntityType.STRAY ||
			event.getEntityType() == EntityType.WITHER_SKELETON ||
			event.getEntityType() == EntityType.ZOMBIE ||
			event.getEntityType() == EntityType.ZOMBIE_VILLAGER) {
			final LivingEntity mob = (LivingEntity) event.getEntity();

			try {
				for (ItemStack item : mob.getEquipment().getArmorContents()) {
					if (item.getItemMeta() instanceof BannerMeta) {
						final BannerMeta banner = (BannerMeta) item.getItemMeta();

						for (Pattern pattern : banner.getPatterns()) {
							if (pattern.getColor() == null) {
								mob.getEquipment().setArmorContents(
									new ItemStack[] {null, null, null, null}
								);
							}
						}
					}
				}
			} catch (Exception exception) {
				mob.getEquipment().setArmorContents(
					new ItemStack[] {null, null, null, null}
				);
			}

			try {
				if (mob.getEquipment().getItemInMainHand().getItemMeta() instanceof BannerMeta) {
					final BannerMeta banner = (BannerMeta) mob.getEquipment().getItemInMainHand().getItemMeta();

					for (Pattern pattern : banner.getPatterns()) {
						if (pattern.getColor() == null) {
							mob.getEquipment().setItemInMainHand(null);
						}
					}
				}
			} catch (Exception exception) {
				mob.getEquipment().setItemInMainHand(null);
			}

			try {
				if (mob.getEquipment().getItemInOffHand().getItemMeta() instanceof BannerMeta) {
					final BannerMeta banner = (BannerMeta) mob.getEquipment().getItemInOffHand().getItemMeta();

					for (Pattern pattern : banner.getPatterns()) {
						if (pattern.getColor() == null) {
							mob.getEquipment().setItemInOffHand(null);
						}
					}
				}
			} catch (Exception exception) {
				mob.getEquipment().setItemInOffHand(null);
			}
		} else if (event.getEntityType() == EntityType.ENDER_DRAGON) {
			final int dragonCount = event.getLocation().getWorld().getEntitiesByClass(EnderDragon.class).size();

			if (dragonCount > 25) {
				event.setCancelled(true);
				return;
			}
		} else if (event.getEntityType() == EntityType.MAGMA_CUBE) {
			final MagmaCube magmacube = (MagmaCube) event.getEntity();

			if (magmacube.getSize() > 100) {
				magmacube.setSize(100);
			}
		} else if (event.getEntityType() == EntityType.SLIME) {
			final Slime slime = (Slime) event.getEntity();

			if (slime.getSize() > 100) {
				slime.setSize(100);
			}
		}
	}

	@EventHandler
	void onEntityAddToWorld(EntityAddToWorldEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) {
			if (event.getEntity().getLocation().isGenerated() &&
				event.getEntity().getLocation().isChunkLoaded()) {
				final Entity entity = event.getEntity();
				final int count = entity.getLocation().getChunk().getEntities().length;

				if (count > 50) {
					entity.remove();
				}
			}

			if (event.getEntityType() == EntityType.ARMOR_STAND ||
				event.getEntityType() == EntityType.DROWNED ||
				event.getEntityType() == EntityType.GIANT ||
				event.getEntityType() == EntityType.HUSK ||
				event.getEntityType() == EntityType.PIG_ZOMBIE ||
				event.getEntityType() == EntityType.PLAYER ||
				event.getEntityType() == EntityType.SKELETON ||
				event.getEntityType() == EntityType.STRAY ||
				event.getEntityType() == EntityType.WITHER_SKELETON ||
				event.getEntityType() == EntityType.ZOMBIE ||
				event.getEntityType() == EntityType.ZOMBIE_VILLAGER) {
				final LivingEntity mob = (LivingEntity) event.getEntity();

				try {
					for (ItemStack item : mob.getEquipment().getArmorContents()) {
						if (item.getItemMeta() instanceof BannerMeta) {
							final BannerMeta banner = (BannerMeta) item.getItemMeta();

							for (Pattern pattern : banner.getPatterns()) {
								if (pattern.getColor() == null) {
									mob.getEquipment().setArmorContents(
										new ItemStack[] {null, null, null, null}
									);
								}
							}
						}
					}
				} catch (Exception exception) {
					mob.getEquipment().setArmorContents(
						new ItemStack[] {null, null, null, null}
					);
				}

				try {
					if (mob.getEquipment().getItemInMainHand().getItemMeta() instanceof BannerMeta) {
						final BannerMeta banner = (BannerMeta) mob.getEquipment().getItemInMainHand().getItemMeta();

						for (Pattern pattern : banner.getPatterns()) {
							if (pattern.getColor() == null) {
								mob.getEquipment().setItemInMainHand(null);
							}
						}
					}
				} catch (Exception exception) {
					mob.getEquipment().setItemInMainHand(null);
				}

				try {
					if (mob.getEquipment().getItemInOffHand().getItemMeta() instanceof BannerMeta) {
						final BannerMeta banner = (BannerMeta) mob.getEquipment().getItemInOffHand().getItemMeta();

						for (Pattern pattern : banner.getPatterns()) {
							if (pattern.getColor() == null) {
								mob.getEquipment().setItemInOffHand(null);
							}
						}
					}
				} catch (Exception exception) {
					mob.getEquipment().setItemInOffHand(null);
				}
			}
		}
	}

	@EventHandler
	void onEntitySpawn(EntitySpawnEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) {
			if (event.getLocation().isGenerated() &&
				event.getLocation().isChunkLoaded()) {
				final int entityCount = event.getLocation().getChunk().getEntities().length;

				if (entityCount > 50) {
					event.setCancelled(true);
				}
			}
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
		if (event.getType() != EntityType.PLAYER) {
			if (event.getSpawnLocation().isGenerated() &&
				event.getSpawnLocation().isChunkLoaded()) {
				final int entityCount = event.getSpawnLocation().getChunk().getEntities().length;

				if (entityCount > 50) {
					event.setCancelled(true);
					return;
				}
			}

			if (event.getType() == EntityType.ENDER_DRAGON) {
				final int dragonCount = event.getSpawnLocation().getWorld().getEntitiesByClass(EnderDragon.class).size();

				if (dragonCount > 25) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	void onPreSpawnerSpawn(PreSpawnerSpawnEvent event) {
		try {
			final CreatureSpawner spawner = (CreatureSpawner) event.getSpawnerLocation().getBlock().getState();

			if (event.getType() == EntityType.MINECART_MOB_SPAWNER) {
				event.setCancelled(true);
				return;
			} else if (event.getType() == EntityType.FALLING_BLOCK) {
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
		} catch (Exception exception) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	void onSpawnerSpawn(SpawnerSpawnEvent event) {
		if (event.getEntity().getType() == EntityType.FALLING_BLOCK) {
			final FallingBlock block = (FallingBlock) event.getEntity();

			if (block.getBlockData().getMaterial() == Material.SPAWNER) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	void onTNTPrime(TNTPrimeEvent event) {
		final double tps = Bukkit.getTPS()[0];

		if (tps < 10) {
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR, false);
		}
	}
}
