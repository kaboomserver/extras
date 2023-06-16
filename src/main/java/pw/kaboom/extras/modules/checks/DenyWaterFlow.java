package pw.kaboom.extras.modules.checks;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;

public class DenyWaterFlow implements Listener {
    private static final int max = JavaPlugin.getPlugin(Main.class).getConfig().getInt("maxWaterFlow");
    private int waterFlowCount = 0;

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("blockLotsOfWaterFromFlowing")) return;
        Material blockType = event.getBlock().getType();
        if (blockType == Material.WATER || blockType == Material.LAVA) {
            waterFlowCount++;
            if (waterFlowCount > max) {
                event.setCancelled(true);
            }
        }
    }
}