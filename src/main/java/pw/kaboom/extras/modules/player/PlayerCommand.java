package pw.kaboom.extras;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import org.bukkit.scheduler.BukkitRunnable;


import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import org.bukkit.permissions.PermissionAttachment;

import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.util.Set;

class WrappedSender implements CommandSender {
    private final CommandSender sender;

    public WrappedSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);/*.substring(0, Math.min(256, message.length())));*/
    }

    @Override
    public void sendMessage(String[] messages) {
        sender.sendMessage(messages);
    }

    @Override
    public Server getServer() {
        return sender.getServer();
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return sender.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return sender.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return sender.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return sender.hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return sender.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return sender.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return sender.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return sender.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        sender.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        sender.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return sender.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return sender.isOp();
    }

    @Override
    public void setOp(boolean value) {
        sender.setOp(value);
    }
	
    @Override
    public Spigot spigot() {
		return null;
	}
}

class PlayerCommand implements Listener {
	private Main main;
	public PlayerCommand(Main main) {
		this.main = main;
	}

	@EventHandler
	void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		final String[] arr = event.getMessage().split(" ");
		final String command = event.getMessage();
		final UUID playerUuid = event.getPlayer().getUniqueId();
		
		if (main.commandMillisList.get(playerUuid) != null) {
			final long millisDifference = System.currentTimeMillis() - main.commandMillisList.get(playerUuid);
	
			if (millisDifference < 200) {
				event.setCancelled(true);
			}
		}
		
		main.commandMillisList.put(playerUuid, System.currentTimeMillis());
		
		if (event.isCancelled()) {
			return;
		}

		if (("/minecraft:execute".equals(arr[0].toLowerCase()) ||
			"/execute".equals(arr[0].toLowerCase())) &&
			arr.length >= 2) {
			for (int i = 1; i < arr.length; i++) {
				if ("as".equalsIgnoreCase(arr[i]) ||
					"at".equalsIgnoreCase(arr[i])) {
					for (int i2 = i+1; i2 < arr.length; i2++) {
						if ("at".equalsIgnoreCase(arr[i2]) ||
							"as".equalsIgnoreCase(arr[i2])) {
							Command.broadcastCommandMessage(event.getPlayer(), "Forbidden execute pattern detected");
							event.setCancelled(true);
							break;
						}
					}
				} else if (i+1 < arr.length &&
					"run".equalsIgnoreCase(arr[i])) {
					if ("execute".equalsIgnoreCase(arr[i+1]) ||
						"particle".equalsIgnoreCase(arr[i+1]) ||
						"save-off".equalsIgnoreCase(arr[i+1]) ||
						"stop".equalsIgnoreCase(arr[i+1])) {
						Command.broadcastCommandMessage(event.getPlayer(), "Forbidden execute command detected");
						event.setCancelled(true);
						break;
					} else if (i+3 < arr.length &&
						"gamerule".equalsIgnoreCase(arr[i+1])) {
						if ("randomTickSpeed".equalsIgnoreCase(arr[i+2]) &&
							Double.parseDouble(arr[i+3]) > 6) {
							event.setMessage(command.replaceFirst("(?i)" + "randomTickSpeed " + arr[i+3], "randomTickSpeed 6"));
							break;
						}
					}
				}
			}
		} else if (("/minecraft:gamerule".equalsIgnoreCase(arr[0]) ||
			"/gamerule".equalsIgnoreCase(arr[0])) &&
			arr.length >= 3) {
			if ("randomTickSpeed".equalsIgnoreCase(arr[1]) &&
				Double.parseDouble(arr[2]) > 6) {
				event.setMessage(command.replaceFirst(arr[2], "6"));
			}
		} else if (("/minecraft:particle".equalsIgnoreCase(arr[0]) ||
			"/particle".equalsIgnoreCase(arr[0])) &&
			arr.length >= 10) {
			if (Double.parseDouble(arr[9]) > 10) {
				final StringBuilder stringBuilder = new StringBuilder();

				for (int i = 0; i < 9; i++) {
					stringBuilder.append(arr[i]).append(" ");
				}
				stringBuilder.append("10 ");
				for (int i = 10; i < arr.length; i++) {
					stringBuilder.append(arr[i]).append(" ");
				}

				event.setMessage(stringBuilder.toString());
			}
		} else if (("/bukkit:reload".equalsIgnoreCase(arr[0]) ||
			"/bukkit:rl".equalsIgnoreCase(arr[0]) ||
			"/reload".equalsIgnoreCase(arr[0]) || 
			"/rl".equalsIgnoreCase(arr[0])) &&
			event.getPlayer().hasPermission("bukkit.command.reload")) {
			if (arr.length >= 2 &&
				"confirm".equalsIgnoreCase(arr[1])) {
				event.setCancelled(true);
				Command.broadcastCommandMessage(event.getPlayer(), ChatColor.RED + "Please note that this command is not supported and may cause issues when using some plugins.");
				Command.broadcastCommandMessage(event.getPlayer(), ChatColor.RED + "If you encounter any issues please use the /stop command to restart your server.");
				Command.broadcastCommandMessage(event.getPlayer(), ChatColor.GREEN + "Reload complete.");
			}
		} else if (("/restart".equalsIgnoreCase(arr[0]) ||
			"/spigot:restart".equalsIgnoreCase(arr[0])) &&
			event.getPlayer().hasPermission("bukkit.command.restart")) {
			event.setCancelled(true);
		} else if (("/minecraft:save-off".equalsIgnoreCase(arr[0]) ||
			"/save-off".equalsIgnoreCase(arr[0])) &&
			event.getPlayer().hasPermission("minecraft.command.save.disable")) {
			event.setCancelled(true);
			Command.broadcastCommandMessage(event.getPlayer(), "Automatic saving is now disabled");
		} else if (("/minecraft:stop".equalsIgnoreCase(arr[0]) ||
			"/stop".equalsIgnoreCase(arr[0])) &&
			event.getPlayer().hasPermission("minecraft.command.stop")) {
			event.setCancelled(true);
			Command.broadcastCommandMessage(event.getPlayer(), "Stopping the server");
		}
		
		/*if (!event.isCancelled()) {
			WrappedSender wrapped = new WrappedSender(event.getPlayer());
			Bukkit.getServer().dispatchCommand(wrapped, event.getMessage().substring(1));
			event.setCancelled(true);
		}*/
	}
}
