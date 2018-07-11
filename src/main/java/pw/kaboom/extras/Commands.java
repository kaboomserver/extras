package pw.kaboom.extras;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.destroystokyo.paper.profile.PlayerProfile;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_12_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_12_R1.PacketPlayInClientCommand.EnumClientCommand;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;

import org.bukkit.enchantments.Enchantment;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;

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

class CommandDestroyEntities implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		for (World world : Bukkit.getServer().getWorlds()) {
			for (Chunk chunk : world.getLoadedChunks()) {
				for (Entity entity : chunk.getEntities()) {
					if (!entity.getType().equals(EntityType.PLAYER)) {
						entity.remove();
					}
				}
			}
		}
		player.sendMessage("Successfully destroyed all entities in every world");
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

class CommandEnchantAll implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		ItemStack item = player.getItemInHand();
		if (item.getType() == Material.AIR) {
			player.sendMessage("Please hold an item in your hand to enchant it");
		} else {
			item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 32767);
			item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 32767);
			item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 32767);
			item.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 32767);
			item.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 32767);
			item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 32767);
			item.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 32767);
			item.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 32767);
			item.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 32767);
			item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 32767);
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 32767);
			item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 32767);
			item.addUnsafeEnchantment(Enchantment.FROST_WALKER, 32767);
			/*item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 32767);*/
			item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 32767);
			item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 32767);
			item.addUnsafeEnchantment(Enchantment.LUCK, 32767);
			item.addUnsafeEnchantment(Enchantment.LURE, 32767);
			item.addUnsafeEnchantment(Enchantment.MENDING, 32767);
			item.addUnsafeEnchantment(Enchantment.OXYGEN, 32767);
			item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 32767);
			item.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 32767);
			item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 32767);
			item.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 32767);
			item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 32767);
			item.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 32767);
			item.addUnsafeEnchantment(Enchantment.SWEEPING_EDGE, 32767);
			item.addUnsafeEnchantment(Enchantment.THORNS, 32767);
			item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 32767);
			item.addUnsafeEnchantment(Enchantment.WATER_WORKER, 32767);
			player.sendMessage("I killed Tim.");
		}
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

class CommandHub implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.teleport(new Location(Bukkit.getWorld("world"), 0, 85, 0));
		player.sendMessage("Successfully moved to the Hub");
		return true;
	}
}

class CommandJumpscare implements CommandExecutor {
	private void createJumpscare(Player player) {
		player.spawnParticle(Particle.MOB_APPEARANCE, player.getLocation(), 4);
		for (int i = 0; i < 10; ++i) {
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_SCREAM, 1, 0);
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
		} else {
			if (args[0].equalsIgnoreCase("*") || args[0].equalsIgnoreCase("**")) {
				for (Player p: Bukkit.getOnlinePlayers()) {
					createJumpscare(p);
				}
				player.sendMessage("Successfully created jumpscare for every player");
			} else {
				Player target = Bukkit.getPlayer(args[0]);
				if (target != null) {
					createJumpscare(target);
					player.sendMessage("Successfully created jumpscare for player \"" + target.getName() + "\"");
				} else {
					player.sendMessage("Player \"" + target.getName() + "\" not found");
				}
			}	
		}
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
		player.teleport(Bukkit.getWorld("world").getSpawnLocation());
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
			main.getConfig().set(player.getUniqueId().toString(), null);
			main.saveConfig();
			player.sendMessage("You no longer have a tag");
		} else {
			main.getConfig().set(player.getUniqueId().toString(), String.join(" ", args));
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
			player.teleport(new Location(world, 0, 85, 0));
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

class CommandUnloadChunks implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		for (World world : Bukkit.getServer().getWorlds()) {
			for (Chunk chunk : world.getLoadedChunks()) {
				chunk.unload(true);
			}
		}
		player.sendMessage("Successfully unloaded unused chunks");
		return true;
	}
}

class CommandUsername implements CommandExecutor {
	private void changeName(Player player, String name){
		for (Player otherPlayer: Bukkit.getOnlinePlayers()){
			((CraftPlayer)otherPlayer).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer)player).getHandle()));

			try {
				Field nameField = GameProfile.class.getDeclaredField("name");
				nameField.setAccessible(true);
				nameField.set(((CraftPlayer)player).getProfile(), name);
			} catch (IllegalAccessException | NoSuchFieldException exception) {
				exception.printStackTrace();
			}

			((CraftPlayer)otherPlayer).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer)player).getHandle()));

			if (otherPlayer.getUniqueId() != player.getUniqueId()) {
				((CraftPlayer)otherPlayer).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
				((CraftPlayer)otherPlayer).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle()));
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <username>");
		} else {
			String namelong = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
			String name = namelong.substring(0, Math.min(16, namelong.length()));

			PlayerProfile profile = player.getPlayerProfile();
			profile.setName(name);
			profile.complete();
			profile.clearProperties();
			player.setPlayerProfile(profile);
			/*Location location = player.getLocation();
			/*changeName(player, name);
			/*player.setHealth(0);
			player.spigot().respawn();
			player.teleport(location);
			/*((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));*/
			player.sendMessage("Successfully set your username to \"" + name + "\"");
		}
		return true;
	}
}

class CommandVote implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.sendMessage("Feel free to vote for the server to help it grow");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " [\"\",{\"text\":\"[1] \",\"color\":\"dark_green\"},{\"text\":\"TopG.org\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://topg.org/Minecraft/in-414108\"}}]");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " [\"\",{\"text\":\"[2] \",\"color\":\"dark_green\"},{\"text\":\"MinecraftServers.biz\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://minecraftservers.biz/servers/140916/\"}}]");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " [\"\",{\"text\":\"[3] \",\"color\":\"dark_green\"},{\"text\":\"MinecraftServers.org\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://minecraftservers.org/vote/153833\"}}]");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " [\"\",{\"text\":\"[4] \",\"color\":\"dark_green\"},{\"text\":\"Minecraft Multiplayer\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://minecraft-mp.com/server/155223/vote/\"}}]");
		return true;
	}
}
