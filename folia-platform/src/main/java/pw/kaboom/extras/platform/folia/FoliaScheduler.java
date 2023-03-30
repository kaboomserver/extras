package pw.kaboom.extras.platform.folia;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import pw.kaboom.extras.platform.IScheduler;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class FoliaScheduler implements IScheduler {
    private static final AsyncScheduler ASYNC_SCHEDULER = Bukkit.getAsyncScheduler();
    private static final RegionScheduler REGION_SCHEDULER = Bukkit.getRegionScheduler();
    private static final GlobalRegionScheduler GLOBAL_REGION_SCHEDULER =
            Bukkit.getGlobalRegionScheduler();

    @Override
    public void runRepeating(final Plugin plugin, final Runnable runnable,
                             final long delay, final long period,
                             final TimeUnit unit) {
        ASYNC_SCHEDULER.runAtFixedRate(plugin, FoliaTask.from(runnable), delay, period, unit);
    }

    @Override
    public void runLater(final Plugin plugin, final Runnable runnable, final long delay,
                         final TimeUnit unit) {
        ASYNC_SCHEDULER.runDelayed(plugin, FoliaTask.from(runnable), delay, unit);
    }

    @Override
    public void runSync(final Plugin plugin, final Runnable runnable) {
        ASYNC_SCHEDULER.runNow(plugin, FoliaTask.from(runnable));
    }

    @Override
    public void runAsync(final Plugin plugin, final Runnable runnable) {
        ASYNC_SCHEDULER.runNow(plugin, FoliaTask.from(runnable));
    }

    @Override
    public void executeOnChunk(final Plugin plugin, final Chunk chunk, final Runnable runnable) {
        final World world = chunk.getWorld();
        final int chunkX = chunk.getX();
        final int chunkZ = chunk.getZ();

        REGION_SCHEDULER.execute(plugin, world, chunkX, chunkZ, runnable);
    }

    @Override
    public void executeOnGlobalRegion(final Plugin plugin, final Runnable runnable) {
        GLOBAL_REGION_SCHEDULER.execute(plugin, runnable);
    }

    private static final class FoliaTask implements Consumer<ScheduledTask> {
        private final Runnable runnable;

        FoliaTask(final Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void accept(final ScheduledTask scheduledTask) {
            this.runnable.run();
        }

        public static FoliaTask from(final Runnable runnable) {
            return new FoliaTask(runnable);
        }
    }
}
