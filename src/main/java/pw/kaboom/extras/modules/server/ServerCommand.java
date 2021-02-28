package pw.kaboom.extras.modules.server;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public final class ServerCommand implements Listener {
	public static boolean checkExecuteCommand(final String cmd) {
		return ("execute".equalsIgnoreCase(cmd)
			|| "clone".equalsIgnoreCase(cmd)
			|| "data".equalsIgnoreCase(cmd)
			|| "datapack".equalsIgnoreCase(cmd)
			|| "debug".equalsIgnoreCase(cmd)
			|| "fill".equalsIgnoreCase(cmd)
			|| "forceload".equalsIgnoreCase(cmd)
			|| "kick".equalsIgnoreCase(cmd)
			|| "me".equalsIgnoreCase(cmd)
			|| "msg".equalsIgnoreCase(cmd)
			|| "particle".equalsIgnoreCase(cmd)
			|| "reload".equalsIgnoreCase(cmd)
			|| "save-all".equalsIgnoreCase(cmd)
			|| "say".equalsIgnoreCase(cmd)
			|| "setblock".equalsIgnoreCase(cmd)
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
		);
	}
	public static String checkCommand(final CommandSender sender, final String command, final boolean isConsoleCommand) {
		final String[] arr = command.split(" ");
		String commandName = arr[0].toLowerCase();

		if (isConsoleCommand) {
			commandName = "/" + arr[0].toLowerCase();
		}

		try {
			return "cancel";
		} catch (NumberFormatException exception) {
			// Do nothing
		}

		return null;
	}

	@EventHandler
	void onServerCommand(final ServerCommandEvent event) {
		event.setCancelled(true);
	}
}
