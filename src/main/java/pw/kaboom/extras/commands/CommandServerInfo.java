package pw.kaboom.extras.commands;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import pw.kaboom.extras.util.Utility;

import javax.annotation.Nonnull;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;

public final class CommandServerInfo implements CommandExecutor {
    private static final String[] GPU_DEVICES;
    private static final @Nullable String PROCESSOR_NAME;

    static {
        // No need to store this in a static variable as it would
        // just waste memory & won't be accessed outside construction
        // anyway.

        final SystemInfo systemInfo = new SystemInfo();

        // Unfortunately, we need to do something like this
        // because calls to getHardware may fail if the
        // server is running on an unrecognized platform,
        // and we're unable to use guard clauses due to
        // returns not being supported in static blocks.

        final @Nullable Pair<String[], String> hardwareInfo = Utility.composeCallable(
                systemInfo::getHardware,
                hardware ->
                        new ObjectObjectImmutablePair<>(
                            hardware.getGraphicsCards()
                                .stream()
                                .map(GraphicsCard::getName)
                                .toArray(String[]::new),
                            hardware.getProcessor()
                                .getProcessorIdentifier()
                                .getName()
                )
        );

        if (hardwareInfo == null) {
            GPU_DEVICES = new String[0];
            PROCESSOR_NAME = null;
        } else {
            GPU_DEVICES = hardwareInfo.first();
            PROCESSOR_NAME = hardwareInfo.second();
        }
    }

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

        if (PROCESSOR_NAME != null) {
            sendInfoMessage(sender, "CPU model", PROCESSOR_NAME);
        }

        sendInfoMessage(sender, "CPU cores",
            String.valueOf(Runtime.getRuntime().availableProcessors())
        );
        sendInfoMessage(sender, "CPU load",
            String.valueOf(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage())
        );

        for (int i = 0; i < GPU_DEVICES.length; i++) {
            sendInfoMessage(
                    sender,
                    "GPU device (" + i + ")",
                    GPU_DEVICES[i]
            );
        }

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
