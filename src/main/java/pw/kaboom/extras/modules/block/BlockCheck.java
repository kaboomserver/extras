package pw.kaboom.extras;

import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import org.bukkit.event.world.ChunkLoadEvent;

class BlockCheck implements Listener {
	@EventHandler
	void onBlockPlace(BlockPlaceEvent event) {
		if (event.getItemInHand().toString().length() > 3019) {
			event.setCancelled(true);
		}

		try {
			event.getBlockPlaced().getState();
		} catch (Exception exception) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onChunkLoad(ChunkLoadEvent event) {
		if (!event.isNewChunk()) {
			for (BlockState block : event.getChunk().getTileEntities()) {
				if (block instanceof CommandBlock) {
					block.update();
				}
			}
		}
	}

	@EventHandler
	void onSignChange(SignChangeEvent event) {
		try {
			event.getLines();
		} catch (Exception exception) {
			event.setCancelled(true);
		}
	}
}

/*class TileEntityCheck extends BukkitRunnable {
	private Main main;
	public TileEntityCheck(Main main) {
		this.main = main;
	}

	public void run() {
		for (final World world : Bukkit.getServer().getWorlds()) {
			for (final Chunk chunk : world.getLoadedChunks()) {
				try {
					chunk.getTileEntities();
				} catch (Exception e) {
					new BukkitRunnable() {
						public void run() {
							world.regenerateChunk(chunk.getX(), chunk.getZ());
						}
					}.runTask(main);
				}
			}
		}
	}
}*/
