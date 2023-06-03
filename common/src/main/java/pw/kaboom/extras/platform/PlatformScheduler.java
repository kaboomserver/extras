package pw.kaboom.extras.platform;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public final class PlatformScheduler {
    private static IScheduler currentScheduler = null;

    public static void setCurrentScheduler(final IScheduler implementation) {
        if (currentScheduler != null) {
            throw new IllegalStateException("Tried to set current scheduler, even though " +
                    " it was already set!");
        }

        currentScheduler = implementation;
    }

    public static void runRepeating(final Plugin plugin, final Runnable runnable,
                                    final long delay, final long period, final TimeUnit unit) {
        currentScheduler.runRepeating(plugin, runnable, delay, period, unit);
    }

    public static void runLater(final Plugin plugin, final Runnable runnable,
                                final long delay, final TimeUnit unit) {
        currentScheduler.runLater(plugin, runnable, delay, unit);
    }

    public static void runSync(final Plugin plugin, final Runnable runnable) {
        currentScheduler.runSync(plugin, runnable);
    }

    public static void runAsync(final Plugin plugin, final Runnable runnable) {
        currentScheduler.runAsync(plugin, runnable);
    }

    public static void executeOnChunk(final Plugin plugin, final Chunk chunk,
                                      final Runnable runnable) {
        currentScheduler.executeOnChunk(plugin, chunk, runnable);
    }

    public static void executeOnGlobalRegion(final Plugin plugin, final Runnable runnable) {
        currentScheduler.executeOnGlobalRegion(plugin, runnable);
    }

    public static void executeOnEntity(final Plugin plugin, final Entity entity,
                                       final Runnable runnable) {
        currentScheduler.executeOnEntity(plugin, entity, runnable);
    }
}
