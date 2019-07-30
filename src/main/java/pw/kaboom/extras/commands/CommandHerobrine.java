package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/* TODO: working command */
class CommandHerobrine implements CommandExecutor {
	private Main main;
	public CommandHerobrine(Main main) {
		this.main = main;
	}

	private void spawnHerobrine(Player player) {
		final Location location = player.getLocation();

		final Player herobrine = (Player) location.getWorld().spawnEntity(
			location.add(location.getDirection().multiply(6)),
			EntityType.PLAYER
		);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		final Player player = (Player) sender;
		final Location location = player.getLocation();

		if (args.length == 0) {
			final Player herobrine = (Player) location.getWorld().spawnEntity(
				location,
				EntityType.PLAYER
			);
		} else {
			if (args[0].equals("*") || args[0].equals("**")) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					spawnHerobrine(p);
				}
				player.sendMessage("Successfully spawned Herobrine behind every player");
			} else {
				final Player target = Bukkit.getPlayer(args[0]);

				if (target != null) {
					spawnHerobrine(target);
					player.sendMessage("Successfully spawned Herobrine behind player \"" + target.getName() + "\"");
				} else {
					player.sendMessage("Player \"" + target.getName() + "\" not found");
				}
			}	
		}
		return true;
	}
}
