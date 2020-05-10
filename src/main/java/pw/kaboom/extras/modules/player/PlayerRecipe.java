package pw.kaboom.extras.modules.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;

public final class PlayerRecipe implements Listener {
	private long recipeMillis;

	@EventHandler
	void onPlayerRecipeDiscover(final PlayerRecipeDiscoverEvent event) {
		final long millisDifference = System.currentTimeMillis() - recipeMillis;

		if (millisDifference < 75) {
			event.setCancelled(true);
		}

		recipeMillis = System.currentTimeMillis();
	}
}
