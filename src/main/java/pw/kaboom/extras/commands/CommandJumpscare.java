package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

class CommandJumpscare implements CommandExecutor {
	private void createJumpscare(Player player) {
		player.spawnParticle(Particle.MOB_APPEARANCE, player.getLocation(), 4);
		for (int i = 0; i < 10; i++) {
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1, 0);
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
		} else {
			if (args[0].equals("*") || args[0].equals("**")) {
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					createJumpscare(onlinePlayer);
				}
				sender.sendMessage("Successfully created jumpscare for every player");
			} else {
				final Player target = Bukkit.getPlayer(args[0]);
				if (target != null) {
					createJumpscare(target);
					sender.sendMessage("Successfully created jumpscare for player \"" + target.getName() + "\"");
				} else {
					sender.sendMessage("Player \"" + target.getName() + "\" not found");
				}
			}	
		}
		return true;
	}
}
