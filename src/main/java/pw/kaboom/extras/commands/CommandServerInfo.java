package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;

public final class CommandServerInfo implements CommandExecutor {
    private void sendInfoMessage(final CommandSender target, final String description,
                                 final String value) {
        target.sendMessage(
                Component.text(description, NamedTextColor.GRAY)
                        .append(Component.text(": " + value, NamedTextColor.WHITE)));
    }

    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        try {
            sendInfoMessage(sender, "Hostname",
                    InetAddress.getLocalHost().getHostName()
            );
            sendInfoMessage(sender, "IP address",
                    InetAddress.getLocalHost().getHostAddress()
            );
        } catch (Exception ignored) {
        }

        sendInfoMessage(sender, "OS name",
            ManagementFactory.getOperatingSystemMXBean().getName()
        );
        sendInfoMessage(sender, "OS architecture",
            ManagementFactory.getOperatingSystemMXBean().getArch()
        );
        sendInfoMessage(sender, "OS version",
            ManagementFactory.getOperatingSystemMXBean().getVersion()
        );
        sendInfoMessage(sender, "Java VM",
            ManagementFactory.getRuntimeMXBean().getVmName()
        );
        sendInfoMessage(sender, "Java version",
            ManagementFactory.getRuntimeMXBean().getSpecVersion()
                    + " "
                    + ManagementFactory.getRuntimeMXBean().getVmVersion()
        );

        try {
            final String[] shCommand = {
                "/bin/sh",
                "-c",
                "cat /proc/cpuinfo | grep 'model name' | cut -f 2 -d ':' | awk '{$1=$1}1' | head -1"
            };

             final Process process = Runtime.getRuntime().exec(shCommand);
            final InputStreamReader isr = new InputStreamReader(process.getInputStream());
            final BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                sendInfoMessage(sender, "CPU model",
                    line
                );
            }

            br.close();
        } catch (Exception ignored) {
        }

        sendInfoMessage(sender, "CPU cores",
            String.valueOf(Runtime.getRuntime().availableProcessors())
        );
        sendInfoMessage(sender, "CPU load",
            String.valueOf(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage())
        );

        final long heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        final long nonHeapUsage = ManagementFactory.getMemoryMXBean()
            .getNonHeapMemoryUsage().getUsed();
        final long memoryMax = (
            ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()
                    + ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax()
        );
        final long memoryUsage = (heapUsage + nonHeapUsage);

        sendInfoMessage(sender, "Available memory",
            (memoryMax / 1024 / 1024) + " MB"
        );
        sendInfoMessage(sender, "Heap memory usage",
            (heapUsage / 1024 / 1024) + " MB"
        );
        sendInfoMessage(sender, "Non-heap memory usage",
            (nonHeapUsage / 1024 / 1024) + " MB"
        );
        sendInfoMessage(sender, "Total memory usage",
            (memoryUsage / 1024 / 1024) + " MB"
        );

        final long minutes = (ManagementFactory.getRuntimeMXBean().getUptime() / 1000) / 60;
        final long seconds = (ManagementFactory.getRuntimeMXBean().getUptime() / 1000) % 60;

        sendInfoMessage(sender, "Server uptime",
            minutes + " minute(s) "
                    + seconds + " second(s)"
        );
        return true;
    }
}
