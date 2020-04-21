package pw.kaboom.extras.modules.player;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class PlayerInteract implements Listener {
	//static HashMap<UUID, Long> interactMillisList = new HashMap<UUID, Long>();

	@EventHandler
	void onInventoryClick(final InventoryClickEvent event) {
		try {
			event.getSlot();
		} catch (Exception exception) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onPlayerInteract(final PlayerInteractEvent event) {
		/*final UUID playerUuid = event.getPlayer().getUniqueId();

		if (interactMillisList.get(playerUuid) != null) {
			final long millisDifference = System.currentTimeMillis() - interactMillisList.get(playerUuid);

			if (millisDifference < 150) {
				event.setCancelled(true);
			}
		}

		interactMillisList.put(playerUuid, System.currentTimeMillis());*/

    	final ItemStack item = event.getItem();

		if (item != null
				&& Material.TRIDENT.equals(item.getType())) {
			final int riptideLimit = 20;

	    	if (item.getEnchantmentLevel(Enchantment.RIPTIDE) > riptideLimit) {
				item.addUnsafeEnchantment(Enchantment.RIPTIDE, riptideLimit);
			}
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final BlockState clickedBlock = event.getClickedBlock().getState();

			if (clickedBlock instanceof Sign) {
				clickedBlock.update();
			}
		}
	}
}
