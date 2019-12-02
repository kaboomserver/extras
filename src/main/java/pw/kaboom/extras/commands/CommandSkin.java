package pw.kaboom.extras;

import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.entity.Player;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

class CommandSkin implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage("Command has to be run by a player");
		} else {
			final Player player = (Player) sender;
	
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Usage: /" + label + " <username>");
			} else if (!Main.skinInProgress.contains(player.getUniqueId())) {
				Main.skinInProgress.add(player.getUniqueId());
				
				new BukkitRunnable() {
					public void run() {
						SkinDownloader skinDownloader = new SkinDownloader();
						if (skinDownloader.fetchSkinData(args[0])) {
							final PlayerProfile profile = player.getPlayerProfile();
							final String texture = skinDownloader.getTexture();
							final String signature = skinDownloader.getSignature();

							profile.setProperty(new ProfileProperty("textures", texture, signature));
			
							player.sendMessage("Successfully set your skin to " + args[0] + "'s");
		
							new BukkitRunnable() {
								public void run() {
									player.setPlayerProfile(profile);
									Main.skinInProgress.remove(player.getUniqueId());
								}
							}.runTask(JavaPlugin.getPlugin(Main.class));
						}
					}
				}.runTaskAsynchronously(JavaPlugin.getPlugin(Main.class));
			} else {
				player.sendMessage("You are already applying a skin. Please wait a few seconds.");
			}
		}
		return true;
	}
}
