package pw.kaboom.extras;

import org.bukkit.ChatColor;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;

class ServerCommand implements Listener {
	public static String checkCommand(CommandSender sender, String command, boolean isConsoleCommand) {
		final String[] arr = command.split(" ");
		String commandName = arr[0].toLowerCase();
		
		if (isConsoleCommand) {
			commandName = "/" + arr[0].toLowerCase();
		}
		
		switch (commandName) {
			case "/minecraft:execute":
			case "/execute":
				if (arr.length >= 2) {
					int asAtCount = 0;
		
					for (int i = 1; i < arr.length; i++) {
						if ("run".equalsIgnoreCase(arr[i])) {
							if (i+1 < arr.length) {
								if ("execute".equalsIgnoreCase(arr[i+1]) ||
									"particle".equalsIgnoreCase(arr[i+1]) ||
									"save-off".equalsIgnoreCase(arr[i+1]) ||
									"stop".equalsIgnoreCase(arr[i+1])) {
									Command.broadcastCommandMessage(sender, "Forbidden execute command detected");
									return "cancel";
								} else if (i+3 < arr.length &&
									"gamerule".equalsIgnoreCase(arr[i+1])) {
									if ("randomTickSpeed".equalsIgnoreCase(arr[i+2]) &&
										Double.parseDouble(arr[i+3]) > 6) {
										return command.replaceFirst("(?i)" + "randomTickSpeed " + arr[i+3], "randomTickSpeed 6");
									} else if ("spawnRadius".equalsIgnoreCase(arr[i+2]) &&
										Double.parseDouble(arr[i+3]) > 100) {
										return command.replaceFirst("(?i)" + "spawnRadius " + arr[i+3], "spawnRadius 100");
									}
								} else if (i+5 < arr.length &&
									"spreadplayers".equalsIgnoreCase(arr[i+1])) {
									if (Double.parseDouble(arr[i+5]) > 75) {
										final StringBuilder stringBuilder = new StringBuilder();
						
										for (int i2 = 0; i2 < i+5; i2++) {
											stringBuilder.append(arr[i2]).append(" ");
										}
										stringBuilder.append("75 ");
										for (int i2 = i+6; i2 < arr.length; i2++) {
											stringBuilder.append(arr[i2]).append(" ");
										}
						
										return stringBuilder.toString();
									}
								}
							}
							break;
						}
		
						if ("as".equalsIgnoreCase(arr[i]) ||
							"at".equalsIgnoreCase(arr[i])) {
							asAtCount++;
						}
					}
					
					if (asAtCount >= 2) {
						Command.broadcastCommandMessage(sender, "Forbidden execute pattern detected");
						return "cancel";
					}
				}
				break;
			case "/minecraft:gamerule":
			case "/gamerule":
				if (arr.length >= 3) {
					if ("randomTickSpeed".equalsIgnoreCase(arr[1]) &&
						Double.parseDouble(arr[2]) > 6) {
						return command.replaceFirst(arr[2], "6");
					} else if ("spawnRadius".equalsIgnoreCase(arr[1]) &&
						Double.parseDouble(arr[2]) > 100) {
						return command.replaceFirst(arr[2], "100");
					}
				}
				break;
			case "/minecraft:particle":
			case "/particle":
				if (arr.length >= 10) {
					if (Double.parseDouble(arr[9]) > 10) {
						final StringBuilder stringBuilder = new StringBuilder();
		
						for (int i = 0; i < 9; i++) {
							stringBuilder.append(arr[i]).append(" ");
						}
						stringBuilder.append("10 ");
						for (int i = 10; i < arr.length; i++) {
							stringBuilder.append(arr[i]).append(" ");
						}
		
						return stringBuilder.toString();
					}
				}
				break;
			case "/bukkit:reload":
			case "/bukkit:rl":
			case "/reload":
			case "/rl":
				if (sender.hasPermission("bukkit.command.reload") &&
					arr.length >= 2 &&
					"confirm".equalsIgnoreCase(arr[1])) {
					Command.broadcastCommandMessage(sender, ChatColor.RED + "Please note that this command is not supported and may cause issues when using some plugins.");
					Command.broadcastCommandMessage(sender, ChatColor.RED + "If you encounter any issues please use the /stop command to restart your server.");
					Command.broadcastCommandMessage(sender, ChatColor.GREEN + "Reload complete.");
					return "cancel";
				}
				break;
			case "/spigot:restart":
			case "/restart":
				if (sender.hasPermission("bukkit.command.restart")) {
					return "cancel";
				}
				break;
			case "/minecraft:save-off":
			case "/save-off":
				if (sender.hasPermission("minecraft.command.save.disable")) {
					Command.broadcastCommandMessage(sender, "Automatic saving is now disabled");
					return "cancel";
				}
				break;
			case "/minecraft:spreadplayers":
			case "/spreadplayers":
				if (arr.length >= 5 &&
					Double.parseDouble(arr[4]) > 75) {
					final StringBuilder stringBuilder = new StringBuilder();
	
					for (int i = 0; i < 4; i++) {
						stringBuilder.append(arr[i]).append(" ");
					}
					stringBuilder.append("75 ");
					for (int i = 5; i < arr.length; i++) {
						stringBuilder.append(arr[i]).append(" ");
					}
	
					return stringBuilder.toString();
				}
				break;
			case "/minecraft:stop":
			case "/stop":
				if (sender.hasPermission("minecraft.command.stop")) {
					Command.broadcastCommandMessage(sender, "Stopping the server");
					return "cancel";
				}
		}
		return null;
	}

	@EventHandler
	void onServerCommand(ServerCommandEvent event) {
		final CommandSender sender = event.getSender();
		final String[] arr = event.getCommand().split(" ");

		if (sender instanceof BlockCommandSender) {
			if (Main.consoleCommandBlacklist.contains(arr[0].toLowerCase())) {
				event.setCancelled(true);
			}
		}

		final String command = event.getCommand();
		final boolean isConsoleCommand = true;
		final String checkedCommand = checkCommand(sender, command, isConsoleCommand);
		
		if (checkedCommand.equals("cancel")) {
			event.setCancelled(true);
		} else if (checkedCommand != null) {
			event.setCommand(checkedCommand);
		}
	}
}
