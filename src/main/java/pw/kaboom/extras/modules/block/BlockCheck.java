package pw.kaboom.extras.modules.block;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public final class BlockCheck implements Listener {
    @EventHandler
    void onChunkUnload(final ChunkUnloadEvent event) {
        for (Chunk chunk : event.getChunk().getWorld().getForceLoadedChunks()) {
            chunk.setForceLoaded(false);
        }
    }
}
