package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

class CommandClearChat implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		for (int i = 0; i < 100; ++i) {
			Bukkit.broadcastMessage("");
		}
		Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "The chat has been cleared");
		return true;
	}
}

class CommandConsole implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <message ..>");
		} else {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:say " + ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
		}
		return true;
	}
}

class CommandDiscord implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.sendMessage("Join the Kaboom.pw Discord server to chat with other users");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " [\"\",{\"text\":\"https://discord.gg/UMGbMsU\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://discord.gg/UMGbMsU\"}}]");
		return true;
	}
}

class CommandEnd implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.teleport(Bukkit.getWorld("world_the_end").getSpawnLocation());
		player.sendMessage("Successfully moved to the End");
		return true;
	}
}

class CommandFlatlands implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.teleport(Bukkit.getWorld("world_flatlands").getSpawnLocation());
		player.sendMessage("Successfully moved to the Flatlands");
		return true;
	}
}

class CommandHub implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.teleport(new Location(Bukkit.getWorld("world"), 0, 63, -22));
		player.sendMessage("Successfully moved to the Hub");
		return true;
	}
}

class CommandNether implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.teleport(Bukkit.getWorld("world_nether").getSpawnLocation());
		player.sendMessage("Successfully moved to the Nether");
		return true;
	}
}

class CommandOverworld implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.teleport(Bukkit.getWorld("world_overworld").getSpawnLocation());
		player.sendMessage("Successfully moved to the Overworld");
		return true;
	}
}

class CommandPrefix implements CommandExecutor {
	Main main;
	CommandPrefix(Main main) {
		this.main = main;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player)sender;
		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <prefix|off>");
		} else if (args[0].equalsIgnoreCase("off")) {
			main.getConfig().set(player.getName().toString(), null);
			main.saveConfig();
			player.sendMessage("You no longer have a tag");
		} else {
			main.getConfig().set(player.getName().toString(), String.join(" ", args));
			main.saveConfig();
			player.sendMessage("You now have the tag: " + ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
		}
		return true;
	}
}

class CommandSpawn implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		World world = player.getLocation().getWorld();
		if (world.getName().equals("world")) {
			player.teleport(new Location(world, 0, 63, -22));
		} else {
			player.teleport(player.getWorld().getSpawnLocation());
		}
		player.sendMessage("Successfully moved to the spawn");
		return true;
	}
}

class CommandTellraw implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <message ..>");
		} else {
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
		}
		return true;
	}
}

class CommandVote implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.sendMessage("Feel free to vote for the server to help it grow");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " [\"\",{\"text\":\"[1] \",\"color\":\"dark_green\"},{\"text\":\"TopG.org\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://topg.org/Minecraft/in-414108\"}}]");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " [\"\",{\"text\":\"[2] \",\"color\":\"dark_green\"},{\"text\":\"MinecraftServers.org\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://minecraftservers.org/vote/153833\"}}]");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " [\"\",{\"text\":\"[3] \",\"color\":\"dark_green\"},{\"text\":\"Minecraft Multiplayer\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://minecraft-mp.com/server/155223/vote/\"}}]");
		return true;
	}
}
