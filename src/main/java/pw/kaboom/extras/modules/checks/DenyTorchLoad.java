package pw.kaboom.extras.modules.checks;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;

public class DenyTorchLoad implements Listener {
    private static final int limit = JavaPlugin.getPlugin(Main.class).getConfig().getInt("torchLimit");

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.TORCH) {
            int placedTorchCount = getPlacedTorchCount(event.getPlayer());
            if (placedTorchCount >= limit) {
                event.setCancelled(true);
            }
        }
    }

    private int getPlacedTorchCount(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.TORCH) {
                count += item.getAmount();
            }
        }
        return count;
    }
}