package pw.kaboom.extras.platform.paper;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import pw.kaboom.extras.platform.IScheduler;

import java.util.concurrent.TimeUnit;

public final class PaperScheduler implements IScheduler {
    private static final BukkitScheduler BUKKIT_SCHEDULER = Bukkit.getScheduler();

    private static long convertIntoTicks(final long time, final TimeUnit unit) {
        final long millis = unit.toMillis(time);

        return millis / 20L;
    }

    @Override
    public void runRepeating(final Plugin plugin, final Runnable runnable, final long delay,
                             final long period, final TimeUnit unit) {
        BUKKIT_SCHEDULER.runTaskTimerAsynchronously(plugin, runnable, convertIntoTicks(delay, unit),
                convertIntoTicks(period, unit));
    }

    @Override
    public void runLater(final Plugin plugin, final Runnable runnable,
                         final long delay, TimeUnit unit) {
        BUKKIT_SCHEDULER.runTaskLaterAsynchronously(plugin, runnable,
                convertIntoTicks(delay, unit));
    }

    @Override
    public void runSync(final Plugin plugin, final Runnable runnable) {
        BUKKIT_SCHEDULER.runTask(plugin, runnable);
    }

    @Override
    public void runAsync(final Plugin plugin, final Runnable runnable) {
        BUKKIT_SCHEDULER.runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void executeOnChunk(final Plugin plugin, final Chunk chunk, final Runnable runnable) {
        BUKKIT_SCHEDULER.runTask(plugin, runnable);
    }

    @Override
    public void executeOnGlobalRegion(final Plugin plugin, final Runnable runnable) {
        BUKKIT_SCHEDULER.runTask(plugin, runnable);
    }

    @Override
    public void executeOnEntity(final Plugin plugin, final Entity entity, final Runnable runnable) {
        BUKKIT_SCHEDULER.runTask(plugin, runnable);
    }
}
