package pw.kaboom.extras.platform;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public interface IScheduler {
    void runRepeating(final Plugin plugin, final Runnable runnable, final long delay,
                      final long period, final TimeUnit unit);
    void runLater(final Plugin plugin, final Runnable runnable,
                  final long delay, final TimeUnit unit);
    void runSync(final Plugin plugin, final Runnable runnable);
    void runAsync(final Plugin plugin, final Runnable runnable);
}
