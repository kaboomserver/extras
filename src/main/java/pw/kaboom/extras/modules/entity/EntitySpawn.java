package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import org.bukkit.block.CreatureSpawner;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import org.bukkit.inventory.ItemStack;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;

class EntitySpawn implements Listener {
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

		if (event.getEntityType() == EntityType.DROWNED ||
			event.getEntityType() == EntityType.GIANT ||
			event.getEntityType() == EntityType.HUSK ||
			event.getEntityType() == EntityType.PIG_ZOMBIE ||
			event.getEntityType() == EntityType.PLAYER ||
			event.getEntityType() == EntityType.SKELETON ||
			event.getEntityType() == EntityType.STRAY ||
			event.getEntityType() == EntityType.WITHER_SKELETON ||
			event.getEntityType() == EntityType.ZOMBIE ||
			event.getEntityType() == EntityType.ZOMBIE_VILLAGER) {
			final LivingEntity mob = event.getEntity();

			try {
				mob.getEquipment().getArmorContents();
			} catch (Exception exception) {
				mob.getEquipment().setArmorContents(
					new ItemStack[] {
						new ItemStack(Material.AIR),
						new ItemStack(Material.AIR),
						new ItemStack(Material.AIR),
						new ItemStack(Material.AIR),
					}
				);
			}

			try {
				mob.getEquipment().getItemInMainHand();
			} catch (Exception exception) {
				mob.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
			}

			try {
				mob.getEquipment().getItemInOffHand();
			} catch (Exception exception) {
				mob.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
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
			final Entity entity = event.getEntity();
			final int count = entity.getLocation().getChunk().getEntities().length;

			if (count > 50) {
				entity.remove();
			}
		}
	}

	@EventHandler
	void onEntitySpawn(EntitySpawnEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) {
			final int entityCount = event.getLocation().getChunk().getEntities().length;

			if (entityCount > 50) {
				event.setCancelled(true);
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
			final int entityCount = event.getSpawnLocation().getChunk().getEntities().length;

			if (entityCount > 50) {
				event.setCancelled(true);
				return;
			} else if (event.getType() == EntityType.ENDER_DRAGON) {
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
		}
	}
}
