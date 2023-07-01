package pw.kaboom.extras.modules.block;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import org.bukkit.scheduler.BukkitScheduler;
import pw.kaboom.extras.Main;

public final class BlockPhysics implements Listener {

    // This class contains code to prevent large areas of non-solid blocks
    // from crashing the server
    private static double tps = 20;

    @EventHandler
    void onBlockPhysics(final BlockPhysicsEvent event) {
        try {
            switch (event.getChangedType()) {
            case COMMAND_BLOCK:
            case CHAIN_COMMAND_BLOCK:
            case REPEATING_COMMAND_BLOCK:
                if (Material.STRUCTURE_BLOCK.equals(event.getSourceBlock().getType())) {
                    event.setCancelled(true);
                }
            default:
                break;
            }
        } catch (Exception | StackOverflowError e) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onBlockRedstone(final BlockRedstoneEvent event) {
        final int maxTps = 10;

        if (tps < maxTps) {
            event.setNewCurrent(0);
        }
    }

    private int fallingBlockCount;

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
