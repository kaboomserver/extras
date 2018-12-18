package pw.kaboom.extras;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

import org.bukkit.enchantments.Enchantment;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;

import org.bukkit.scheduler.BukkitRunnable;

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
		Player player = (Player) sender;

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
		Player player = (Player) sender;

		for (World world : Bukkit.getServer().getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity.getType() != EntityType.PLAYER) {
					entity.remove();
				}
			}
		}
		player.sendMessage("Successfully destroyed all entities in every world");
		return true;
	}
}

class CommandEnchantAll implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
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
			item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 32767);
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

class CommandJumpscare implements CommandExecutor {
	private void createJumpscare(Player player) {
		player.spawnParticle(Particle.MOB_APPEARANCE, player.getLocation(), 4);
		for (int i = 0; i < 10; ++i) {
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_SCREAM, 1, 0);
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
		} else {
			if (args[0].equals("*") || args[0].equals("**")) {
				for (Player p : Bukkit.getOnlinePlayers()) {
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

class CommandPrefix implements CommandExecutor {
	Main main;
	CommandPrefix(Main main) {
		this.main = main;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;

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

class CommandSkin implements CommandExecutor {
	Main main;
	CommandSkin(Main main) {
		this.main = main;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		final Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <username>");
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
            			public void run() {
					try {
						final String name = args[0];
						URL nameurl = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
						HttpURLConnection nameconnection = (HttpURLConnection) nameurl.openConnection();

						if (nameconnection.getResponseCode() == 200) {
							InputStreamReader namestream = new InputStreamReader(nameconnection.getInputStream());
							String uuid = new JsonParser().parse(namestream).getAsJsonObject().get("id").getAsString();
							URL uuidurl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
							HttpURLConnection uuidconnection = (HttpURLConnection) uuidurl.openConnection();

							if (uuidconnection.getResponseCode() == 200) {
								InputStreamReader uuidstream = new InputStreamReader(uuidconnection.getInputStream());
								JsonObject response = new JsonParser().parse(uuidstream).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
								final String texture = response.get("value").getAsString();
								final String signature = response.get("signature").getAsString();

								Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
            								public void run() {
										PlayerProfile textureprofile = player.getPlayerProfile();
										textureprofile.setProperty(new ProfileProperty("textures", texture, signature));
										player.setPlayerProfile(textureprofile);
										player.sendMessage("Successfully set your skin to " + name + "'s");
									}
								});
							} else {
								Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
            								public void run() {
										player.sendMessage("Failed to change skin. Try again later");
									}
								});
							}
							uuidconnection.disconnect();
						} else {
							Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
								public void run() {
									player.sendMessage("A player with that username doesn't exist");
								}
							});
						}
						nameconnection.disconnect();
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			});
		}
		return true;
	}
}

class CommandSpawn implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;

		World world = Bukkit.getWorld("world");
		player.teleport(world.getSpawnLocation());
		player.sendMessage("Successfully moved to the spawn");
		return true;
	}
}

class CommandTellraw implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;

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
		Player player = (Player) sender;

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
	Main main;
	CommandUsername(Main main) {
		this.main = main;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <username>");
		} else {
			String namelong = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));
			String name = namelong.substring(0, Math.min(16, namelong.length()));

			PlayerProfile profile = player.getPlayerProfile();
			profile.setName(name);
			player.setPlayerProfile(profile);
			player.sendMessage("Successfully set your username to \"" + name + "\"");

			main.getSkin(name, player);
		}
		return true;
	}
}
