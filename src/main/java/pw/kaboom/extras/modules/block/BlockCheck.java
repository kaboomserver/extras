package pw.kaboom.extras.modules.block;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.platform.PlatformScheduler;

public final class BlockCheck implements Listener {
    @EventHandler
    void onBlockPlace(final BlockPlaceEvent event) {
        try {
            final int maxItemStringLength = 3019;

            if (event.getItemInHand().toString().length() > maxItemStringLength) {
                event.setCancelled(true);
            }

            event.getBlockPlaced().getState();
        } catch (Exception exception) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onChunkUnload(final ChunkUnloadEvent event) {
        final Main plugin = JavaPlugin.getPlugin(Main.class);

        for (Chunk chunk : event.getChunk().getWorld().getForceLoadedChunks()) {
            PlatformScheduler.executeOnChunk(plugin, chunk, () -> chunk.setForceLoaded(false));
        }
    }

	@EventHandler
	void onSignChange(final SignChangeEvent event) {
		try {
			event.getLines();
		} catch (Exception exception) {
			event.setCancelled(true);
		}
	}
}
