package pw.kaboom.extras.modules.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

public final class WorldGenerator {
    public void generateWorlds() {
        if (Bukkit.getWorld("flatworld") == null) {
            final WorldCreator worldCreator = new WorldCreator("flatworld").environment(World.Environment.NORMAL);
            worldCreator.type(WorldType.FLAT);
            worldCreator.generateStructures(false);
            Bukkit.getServer().createWorld(worldCreator);
        }
    }
}
