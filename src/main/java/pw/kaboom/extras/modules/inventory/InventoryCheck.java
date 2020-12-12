package pw.kaboom.extras.modules.inventory;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class InventoryCheck implements Listener {

	@EventHandler
	void onInventoryClick(final InventoryClickEvent event) {
		if (Material.STRUCTURE_BLOCK.equals(event.getCurrentItem().getType())) {
			event.setCurrentItem(new ItemStack(Material.AIR));
			event.setCancelled(true);
		}
	}

}
