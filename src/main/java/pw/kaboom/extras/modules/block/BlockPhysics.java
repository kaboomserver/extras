package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;

import org.bukkit.entity.EntityType;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

import org.bukkit.event.entity.EntityChangeBlockEvent;

import org.bukkit.plugin.java.JavaPlugin;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;

class BlockPhysics implements Listener {
	@EventHandler
	void onBlockForm(BlockFormEvent event) {
		if (event.getBlock().getType() == Material.LAVA ||
			event.getBlock().getType() == Material.WATER) {
			for (BlockFace face : JavaPlugin.getPlugin(Main.class).faces) {
				if (event.getBlock().getRelative(face).getType() != Material.LAVA &&
					event.getBlock().getRelative(face).getType() != Material.WATER) {
					return;
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onBlockFromTo(BlockFromToEvent event) {
		if (event.getBlock().getType() == Material.LAVA ||
			event.getBlock().getType() == Material.WATER) {
			boolean lavaFound = false;
			boolean waterFound = false;

			for (BlockFace face : JavaPlugin.getPlugin(Main.class).faces) {
				if (event.getBlock().getRelative(face).getType() == Material.LAVA) {
					lavaFound = true;
				} else if (event.getBlock().getRelative(face).getType() == Material.WATER) {
					waterFound = true;
				}
				
				if (lavaFound && waterFound) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	void onBlockDestroy(BlockDestroyEvent event) {
		if (!event.getBlock().getType().isSolid()) {
			for (BlockFace face : JavaPlugin.getPlugin(Main.class).faces) {
				if (event.getBlock().getRelative(face).getType() != event.getBlock().getType()) {
					return;
				}
				event.getBlock().setType(Material.AIR, false);
				event.setCancelled(true);
			}
		}
	}

	/*@EventHandler
	void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.getSourceBlock().getState() instanceof CommandBlock) {
			event.getSourceBlock().getState().update();
		}
	}*/

	@EventHandler
	void onBlockRedstone(BlockRedstoneEvent event) {
		final double tps = Bukkit.getServer().getTPS()[0];

		if (tps < 10) {
			event.setNewCurrent(0);
		}
	}
	
	@EventHandler
	void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (event.getEntityType() == EntityType.FALLING_BLOCK &&
			event.getTo() == Material.AIR) {
			Main.fallingBlockCount++;

			if (Main.fallingBlockCount == 10) {
				event.setCancelled(true);
				Main.fallingBlockCount = 0;
			}
		}
	}
}
