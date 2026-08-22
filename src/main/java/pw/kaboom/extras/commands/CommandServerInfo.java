package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import pw.kaboom.extras.util.Utility;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public final class CommandServerInfo implements BrigadierCommand {
    private static final OperatingSystemMXBean OS =
            ManagementFactory.getOperatingSystemMXBean();
    private static final RuntimeMXBean RUNTIME = ManagementFactory.getRuntimeMXBean();
    private static final MemoryMXBean MEMORY = ManagementFactory.getMemoryMXBean();
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

        @Nullable final Pair<String[], String> hardwareInfo = Utility.composeCallable(
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

    private static final List<InfoEntry> ENTRIES = List.of(
            InfoEntry.of("Hostname", () -> InetAddress.getLocalHost().getHostName()),
            InfoEntry.of("IP address", () ->
                    InetAddress.getLocalHost().getHostAddress()),
            InfoEntry.of("OS name", OS::getName),
            InfoEntry.of("OS architecture", OS::getArch),
            InfoEntry.of("OS version", OS::getVersion),
            InfoEntry.of("Java VM", RUNTIME::getVmName),
            InfoEntry.of("Java version",
                    () -> RUNTIME.getSpecVersion() + " " + RUNTIME.getVmVersion()),
            InfoEntry.ofNullable("CPU model", () -> PROCESSOR_NAME),
            InfoEntry.of("CPU cores",
                    () -> String.valueOf(Runtime.getRuntime().availableProcessors())),
            InfoEntry.of("CPU load", () -> String.valueOf(OS.getSystemLoadAverage())),
            InfoEntry.ofMulti("GPU device", () -> GPU_DEVICES),
            InfoEntry.of("Available memory", () -> mebibytes(
                    MEMORY.getHeapMemoryUsage().getMax()
                            + MEMORY.getNonHeapMemoryUsage().getMax())),
            InfoEntry.of("Heap memory usage",
                    () -> mebibytes(MEMORY.getHeapMemoryUsage().getUsed())),
            InfoEntry.of("Non-heap memory usage",
                    () -> mebibytes(MEMORY.getNonHeapMemoryUsage().getUsed())),
            InfoEntry.of("Total memory usage", () -> mebibytes(
                    MEMORY.getHeapMemoryUsage().getUsed()
                            + MEMORY.getNonHeapMemoryUsage().getUsed())),
            InfoEntry.of("Server uptime", CommandServerInfo::uptime)
    );


    @Override
    public String getLabel() {
        return "serverinfo";
    }

    @Override
    public String getDescription() {
        return "Shows detailed server information";
    }

    @Override
    public List<String> getAliases() {
        return List.of("specs");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src -> src.getSender().hasPermission("extras.serverinfo"))
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage(serverInfo());
                    return Command.SINGLE_SUCCESS;
                });
    }

    private record InfoEntry(String label, Callable<Stream<String>> values) {
        private static InfoEntry of(final String label, final Callable<String> value) {
            return new InfoEntry(label, () -> Stream.of(value.call()));
        }

        private static InfoEntry ofNullable(final String label,
                                            final Callable<@Nullable String> value) {
            return new InfoEntry(label, () -> Stream.ofNullable(value.call()));
        }

        private static InfoEntry ofMulti(final String label, final Callable<String[]> values) {
            return new InfoEntry(label, () -> Arrays.stream(values.call()));
        }
    }

    private static String mebibytes(final long bytes) {
        return (bytes / 1024 / 1024) + " MiB";
    }

    private static String uptime() {
        final long totalSeconds = RUNTIME.getUptime() / 1000;
        return (totalSeconds / 60) + " minute(s) "
                + (totalSeconds % 60) + " second(s)";
    }

    private static Component infoLine(final String label, final String value) {
        return Component.text(label, NamedTextColor.GRAY)
                .append(Component.text(": " + value, NamedTextColor.WHITE));
    }

    private static Component serverInfo() {
        final List<Component> lines = new ArrayList<>();

        for (final InfoEntry entry : ENTRIES) {
            final List<String> values;

            try {
                values = entry.values().call().toList();
            } catch (Exception _) {
                continue;
            }

            if (values.size() == 1) {
                lines.add(infoLine(entry.label(), values.getFirst()));
            } else {
                for (int i = 0; i < values.size(); i++) {
                    lines.add(infoLine(entry.label() + " (" + i + ")", values.get(i)));
                }
            }
        }

        return Component.join(JoinConfiguration.newlines(), lines);
    }
}
