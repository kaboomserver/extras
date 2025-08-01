package pw.kaboom.extras.modules.server;

import com.google.common.collect.ImmutableSet;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerCommand implements Listener {
    private static final Pattern SELECTOR_PATTERN = Pattern.compile("(?>\\s)*@[aenprs](?>\\s)*");
    private static final Logger LOGGER = JavaPlugin.getPlugin(Main.class).getLogger();

    private static final Set<String> BLOCKED_EXECUTE_COMMANDS = ImmutableSet.of(
            "clone", "fill", "give", "kick", "locate",  "me", "msg", "save-all", "say",
            "spawnpoint", "spreadplayers",  "summon", "teammsg",  "teleport", "tell", "tellraw",
            "tm", "tp", "w", "fillbiome", "ride");

    private static String checkSelectors(final String[] arr, final int offset) {
        final String[] args = Arrays.copyOfRange(arr, offset, arr.length);
        final String str = String.join(" ", args);
        final Matcher matcher = SELECTOR_PATTERN.matcher(str);
        final long count = matcher.results().count();

        if (count > 3) {
            return "cancel";
        }

        return null;
    }

    public static String checkCommand(final CommandSender sender, final String command,
                                      final boolean isConsoleCommand) {
        return checkCommand(sender, command, isConsoleCommand, 1);
    }

    public static String checkCommand(final CommandSender sender, final String command,
                                      final boolean isConsoleCommand, int depth) {
        if (depth > 50) {
            return "cancel";
        }

        final String[] arr = command.split(" ");

        if (arr.length == 0) {
            return command;
        }

        String commandName = arr[0].toLowerCase();

        if (isConsoleCommand) {
            commandName = "/" + commandName;
        } else if (arr.length >= 2 && commandName.equals("/")) {
            // Command could contain spaces after the slash, e.g. "/   spawn"
            commandName = "/" + arr[1].toLowerCase();
        }

        try {
            switch (commandName) {
                case "/minecraft:execute", "/execute" -> {
                    if (arr.length >= 2) {
                        if (arr.length >= 3 && checkSelectors(arr, 2) != null) {
                            return "cancel";
                        }

                        for (int i = 1; i < arr.length; i++) {
                            if ("summon".equalsIgnoreCase(arr[i])) {
                                return "cancel";
                            }
                            if (!"run".equalsIgnoreCase(arr[i])) {
                                continue;
                            }
                            if (i + 1 == arr.length) {
                                break;
                            }
                            if (BLOCKED_EXECUTE_COMMANDS.contains(arr[i + 1])) {
                                return "cancel";
                            }
                            final String[] executeCommand = Arrays.copyOfRange(
                                    arr, i + 1, arr.length);
                            final String result = checkCommand(sender,
                                    String.join(" ", executeCommand), true, depth + 1);
                            if (result == null) {
                                continue;
                            } else if (result.equals("cancel")) {
                                return "cancel";
                            }
                            final String pureExecute = String.join(
                                    " ", Arrays.copyOfRange(arr, 0, i + 1));
                            final String finalResult = checkCommand(sender,
                                    pureExecute + " " + result, isConsoleCommand, depth + 1);
                            return Objects.requireNonNullElseGet(finalResult,
                                    () -> pureExecute + " " + result);
                        }
                    }
                }
                case "/minecraft:fill", "/fill" -> {
                    if (command.contains("auto")) {
                        return command.replace("auto", "[auto]");
                    }
                }
                case "/minecraft:give", "/give" -> {
                    if (Double.parseDouble(arr[arr.length - 1]) > 64) {
                        // Limit item count
                        arr[arr.length - 1] = "64";
                        return String.join(" ", arr);
                    }
                }
                case "/minecraft:particle", "/particle" -> {
                    int[] numArgs = {14, 10};
                    for (int i : numArgs) {
                        if (arr.length < i || arr.length > i + 2) {
                            continue;
                        }
                        if (Double.parseDouble(arr[i - 1]) > 10) {
                            // Limit particle count
                            arr[i - 1] = "10";
                            return String.join(" ", arr);
                        }
                    }
                }
                case "/minecraft:ban", "/ban", "/minecraft:kick", "/kick",
                        "/minecraft:tell", "/tell", "/minecraft:msg", "/msg",
                        "/minecraft:w", "/w", "/minecraft:say", "/say" -> {
                    return checkSelectors(arr, 1);
                }
                case "/minecraft:spreadplayers", "/spreadplayers" -> {
                    if (arr.length == 7 && (arr[6].contains("@e") || arr[6].contains("@a"))) {
                        return "cancel";
                    } else if (arr.length >= 5) {
                        if (Double.parseDouble(arr[3]) > 0) {
                            arr[3] = "0";
                        }
                        if (Double.parseDouble(arr[4]) < 8) {
                            arr[4] = "8";
                        }
                        if (Double.parseDouble(arr[4]) > 50) {
                            arr[4] = "50";
                        }
                        return String.join(" ", arr);
                    }
                }
                case "/viaversion:viaver", "/viaversion:viaversion", "/viaversion:vvbukkit",
                        "/viaver", "/viaversion", "/vvbukkit" -> {
                    if (arr.length >= 2
                            && "debug".equalsIgnoreCase(arr[1])) {
                        return "cancel";
                    }
                }
                case "/geyser-spigot:geyser", "/geyser" -> {
                    if (arr.length >= 2
                            && ("dump".equalsIgnoreCase(arr[1])
			     || "reload".equalsIgnoreCase(arr[1]))) {
                        return "cancel";
                    }
                }
                default -> {
                }
            }
        } catch (NumberFormatException exception) {
            // Do nothing
        }

        return null;
    }

    @EventHandler
    void onServerCommand(final ServerCommandEvent event) {
        final CommandSender sender = event.getSender();

        if (sender instanceof BlockCommandSender blockCommandSender) {
            final var commandBlock = (CommandBlock) blockCommandSender.getBlock().getState(false);

            commandBlock.setCommand("");
        } else if (sender instanceof CommandMinecart commandMinecart) {
            commandMinecart.setCommand("");
        }

        final String command = event.getCommand();
        final boolean isConsoleCommand = true;
        final String checkedCommand = checkCommand(sender, command, isConsoleCommand);

        if (checkedCommand != null) {
            if ("cancel".equals(checkedCommand)) {
                event.setCancelled(true);
            } else {
                event.setCommand(checkedCommand);
            }
        }

        LOGGER.log(Level.INFO, "Console command: " + command);
    }
}
