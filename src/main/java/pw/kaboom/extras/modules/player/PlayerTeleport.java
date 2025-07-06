package pw.kaboom.extras.modules.player;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import pw.kaboom.extras.util.Utility;

public final class PlayerTeleport implements Listener {
    @EventHandler
    void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        final AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute == null) return;
        if (attribute.getValue() <= 0) {
            Utility.resetAttribute(attribute);
            player.setHealth(20);
        }
    }
}
