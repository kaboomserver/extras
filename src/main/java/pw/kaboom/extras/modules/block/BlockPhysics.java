package pw.kaboom.extras.modules.block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitScheduler;
import pw.kaboom.extras.Main;

public final class BlockPhysics implements Listener {
    private static final double MINIMUM_TPS = 10;
    // This class contains code to prevent large areas of non-solid blocks
    // from crashing the server
    private static double tps = 20;

    @EventHandler
    void onBlockRedstone(final BlockRedstoneEvent event) {
        if (tps < MINIMUM_TPS) {
            event.setNewCurrent(0);
        }
    }

    private int fallingBlockCount;

    @EventHandler
    void onBlockForm(final BlockFormEvent event) {
        if (tps < MINIMUM_TPS) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK
                && event.getTo() == Material.AIR) {
            fallingBlockCount++;

            final int maxFallingBlockCount = 10;

            if (fallingBlockCount == maxFallingBlockCount) {
                event.setCancelled(true);
                fallingBlockCount = 0;
            }
        }
    }

    private static void updateTPS() {
        final double[] tpsValues = Bukkit.getTPS();

        tps = tpsValues[0];
    }

    public static void init(final Main main) {
        final BukkitScheduler scheduler = Bukkit.getScheduler();

        scheduler.runTaskTimer(main, BlockPhysics::updateTPS, 0L, 1200L); // 1 minute
    }
}
