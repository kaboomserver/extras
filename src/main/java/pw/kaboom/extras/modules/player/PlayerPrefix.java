package pw.kaboom.extras.modules.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import pw.kaboom.extras.Main;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerPrefix implements Listener {
	private static final Main plugin = JavaPlugin.getPlugin(Main.class);
	private static final File PREFIX_CONFIG_FILE = plugin.getPrefixConfigFile();
	private static final FileConfiguration PREFIX_CONFIG = plugin.getPrefixConfig();
	private static final FileConfiguration PLUGIN_CONFIGURATION = plugin.getConfig();
	private static final Component opTag;
	private static final Component deOpTag;
	private static final Map<Player, Boolean> opMap = new HashMap<>();
	private static final Map<Player, Component> displayNameMap = new HashMap<>();

	static {
		final String legacyOpTag = PLUGIN_CONFIGURATION.getString("opTag");
		final String legacyDeOpTag = PLUGIN_CONFIGURATION.getString("deOpTag");

		if (legacyOpTag == null || legacyDeOpTag == null) {
			throw new RuntimeException("Invalid plugin configuration!");
		}

		opTag = LegacyComponentSerializer.legacySection().deserialize(legacyOpTag);
		deOpTag = LegacyComponentSerializer.legacySection().deserialize(legacyDeOpTag);

		final BukkitScheduler scheduler = Bukkit.getScheduler();

		scheduler.runTaskTimerAsynchronously(plugin, PlayerPrefix::checkOpStatus,
			0L, 1L);
		scheduler.runTaskTimerAsynchronously(plugin, PlayerPrefix::checkDisplayNames,
			0L, 1L);
	}

	public static void removePrefix(Player player) throws IOException {
		final UUID playerUUID = player.getUniqueId();
		final String stringifiedUUID = playerUUID.toString();

		PREFIX_CONFIG.set(stringifiedUUID, null);
		PREFIX_CONFIG.save(PREFIX_CONFIG_FILE);

		onUpdate(player);
	}

	public static Component setPrefix(Player player, String legacyPrefix) throws IOException {
		final Component prefix = LegacyComponentSerializer.legacySection()
			.deserialize(legacyPrefix);
		final UUID playerUUID = player.getUniqueId();
		final String stringifiedUUID = playerUUID.toString();

		PREFIX_CONFIG.set(stringifiedUUID, legacyPrefix);
		PREFIX_CONFIG.save(PREFIX_CONFIG_FILE);

		onUpdate(player);
		return prefix;
	}

	public static Component getPrefix(Player player) throws IOException {
		final UUID playerUUID = player.getUniqueId();
		final String stringifiedUUID = playerUUID.toString();
		final String legacyPrefix = PREFIX_CONFIG.getString(stringifiedUUID);

		if (legacyPrefix == null) {
			return player.isOp() ? opTag : deOpTag;
		}

		return LegacyComponentSerializer.legacyAmpersand()
			.deserialize(legacyPrefix)
			.append(Component.space());
	}

	public static Component getDefaultPrefix(Player player) {
		return player.isOp() ? opTag : deOpTag;
	}

	private static void onUpdate(Player player) throws IOException {
		final Component component = Component.empty()
			.append(getPrefix(player))
			.append(player.displayName());

		player.playerListName(component);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLoginEvent(PlayerLoginEvent event) throws IOException {
		final Player player = event.getPlayer();
		final boolean isOp = player.isOp();

		opMap.put(player, isOp);
		displayNameMap.put(player, player.displayName());
		onUpdate(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		final Player player = event.getPlayer();

		opMap.remove(player);
		displayNameMap.remove(player);
	}

	private static void checkOpStatus() {
		final Server server = Bukkit.getServer();
		final Collection<? extends Player> players = server.getOnlinePlayers();

		for (Player player : players) {
			final boolean isOp = player.isOp();

			if (!opMap.containsKey(player)) {
				return;
			}

			final boolean storedOp = opMap.get(player);

			if (isOp == storedOp) {
				continue;
			}

			opMap.put(player, isOp);

			try {
				onUpdate(player);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void checkDisplayNames() {
		final Server server = Bukkit.getServer();
		final Collection<? extends Player> players = server.getOnlinePlayers();

		for (Player player : players) {
			final Component displayName = player.displayName();

			if (!displayNameMap.containsKey(player)) {
				return;
			}

			final Component storedDisplayName = displayNameMap.get(player);

			if (displayName.equals(storedDisplayName)) {
				continue;
			}

			displayNameMap.put(player, displayName);

			try {
				onUpdate(player);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
