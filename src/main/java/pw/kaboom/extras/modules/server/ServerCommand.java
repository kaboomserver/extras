package pw.kaboom.extras.modules.server;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerCommand implements Listener {
    private static final Pattern AS_AT_PATTERN = Pattern.compile(
        "\\b(as|at|facing entity) @[ae]\\b");
    private static final Logger LOGGER = JavaPlugin.getPlugin(Main.class).getLogger();

    public static boolean checkExecuteCommand(final String cmd) {
        return ("execute".equalsIgnoreCase(cmd)
            || "clone".equalsIgnoreCase(cmd)
            || "fill".equalsIgnoreCase(cmd)
            || "forceload".equalsIgnoreCase(cmd)
            || "kick".equalsIgnoreCase(cmd)
            || "locate".equalsIgnoreCase(cmd)
            || "locatebiome".equalsIgnoreCase(cmd)
            || "me".equalsIgnoreCase(cmd)
            || "msg".equalsIgnoreCase(cmd)
            || "reload".equalsIgnoreCase(cmd)
            || "save-all".equalsIgnoreCase(cmd)
            || "say".equalsIgnoreCase(cmd)
            || "spreadplayers".equalsIgnoreCase(cmd)
            || "stop".equalsIgnoreCase(cmd)
            || "summon".equalsIgnoreCase(cmd)
            || "teammsg".equalsIgnoreCase(cmd)
            || "teleport".equalsIgnoreCase(cmd)
            || "tell".equalsIgnoreCase(cmd)
            || "tellraw".equalsIgnoreCase(cmd)
            || "tm".equalsIgnoreCase(cmd)
            || "tp".equalsIgnoreCase(cmd)
            || "w".equalsIgnoreCase(cmd)
            || "place".equalsIgnoreCase(cmd)
            || "fillbiome".equalsIgnoreCase(cmd)
            || "ride".equalsIgnoreCase(cmd)
        );
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
        String commandName = arr[0].toLowerCase();

        if (isConsoleCommand) {
            commandName = "/" + commandName;
        } else if (arr.length >= 2 && commandName.equals("/")) {
            // Command could contain spaces after the slash, e.g. "/   spawn"
            commandName = "/" + arr[1].toLowerCase();
        }

        for (int i = 1; i < arr.length; i++) {
            if (arr[i].matches("^[+-]?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)[\\r\\n]*$")) {
                try {
                    int integer = Integer.parseInt(arr[i]);
                    try {
                        if (integer >= Integer.MAX_VALUE) {
                            return "cancel";
                        }
                    } catch (Exception e) {
                        return "cancel";
                    }

                } catch (NumberFormatException e) {
                    // Ignore exception
                }
            }
        }

        try {
            switch (commandName) {
                case "/minecraft:execute":
                case "/execute":
                    if (arr.length >= 2) {
                        int asAtCount = 0;
                        Matcher asAtMatcher = AS_AT_PATTERN.matcher(command.toLowerCase());
                        while (asAtMatcher.find()) {
                            asAtCount++;
                        }
                        if (asAtCount >= 2) {
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
                            if (checkExecuteCommand(arr[i + 1])) {
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
                            if (finalResult == null) {
                                return pureExecute + " " + result;
                            }
                            return finalResult;
                        }
                    }
                    break;
                case "/minecraft:fill":
                case "/fill":
                    if (command.contains("auto")) {
                        return command.replace("auto", "[auto]");
                    }
                    break;
                case "/minecraft:give":
                case "/give":
                    if (Double.parseDouble(arr[arr.length - 1]) > 64) {
                        // Limit item count
                        arr[arr.length - 1] = "64";
                        return String.join(" ", arr);
                    }
                    break;
                case "/minecraft:particle":
                case "/particle":
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
                    break;
                case "/minecraft:spreadplayers":
                case "/spreadplayers":
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
                    break;
                case "/viaversion:viaver":
                case "/viaversion:viaversion":
                case "/viaversion:vvbukkit":
                case "/viaver":
                case "/viaversion":
                case "/vvbukkit":
                    if (arr.length >= 2
                            && "debug".equalsIgnoreCase(arr[1])) {
                        return "cancel";
                    }
                    break;
                case "/scissors:scissors":
                case "/scissors":
                    if(arr.length >= 2
                           && "reload".equalsIgnoreCase(arr[1])) {
                        return "cancel";
                    }
                    break;
                case "/geyser-spigot:geyser":
                case "/geyser":
                    if (arr.length >= 2
                            && "dump".equalsIgnoreCase(arr[1])) {
                        return "cancel";
                    }
                    break;
                default:
                    break;
            }
        } catch (NumberFormatException exception) {
            // Do nothing
        }

        return null;
    }

    @EventHandler
    void onServerCommand(final ServerCommandEvent event) {
        final CommandSender sender = event.getSender();

        if (sender instanceof BlockCommandSender) {
            final CommandBlock commandBlock = (CommandBlock) ((BlockCommandSender) sender)
                .getBlock().getState();

            commandBlock.setCommand("");
            commandBlock.update();
        } else if (sender instanceof CommandMinecart) {
            final CommandMinecart commandMinecart = (CommandMinecart) sender;

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
