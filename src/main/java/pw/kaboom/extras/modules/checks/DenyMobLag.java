package pw.kaboom.extras.modules.checks;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;

public class DenyMobLag implements Listener {
    private static final int limit = JavaPlugin.getPlugin(Main.class).getConfig().getInt("mobLimitAtOnce");
    private int mobSpawnCount = 0;

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        EntityType entityType = event.getEntityType();
        if (entityType.isAlive()) {
            mobSpawnCount++;
            if (mobSpawnCount > limit) {
                event.setCancelled(true);
            }
        }
    }
}