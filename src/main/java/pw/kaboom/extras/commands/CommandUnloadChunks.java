package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

class CommandUnloadChunks implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		final Player player = (Player) sender;
		int chunkCount = 0;

		for (World world : Bukkit.getServer().getWorlds()) {
			for (Chunk chunk : world.getLoadedChunks()) {
				if (chunk.unload() == true) {
					chunkCount++;
				}
			}
		}

		player.sendMessage("Successfully unloaded " + chunkCount + "unused chunks");
		return true;
	}
}
