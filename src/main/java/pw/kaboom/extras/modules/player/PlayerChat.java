package pw.kaboom.extras.modules.player;

import java.io.File;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import pw.kaboom.extras.Main;

public final class PlayerChat implements Listener {
    private static final FileConfiguration CONFIG = JavaPlugin.getPlugin(Main.class).getConfig();
    private static final FileConfiguration PREFIX_CONFIG = JavaPlugin
        .getPlugin(Main.class).getPrefixConfig();

    private static final String OP_TAG = CONFIG.getString("opTag");
    private static final String DEOP_TAG = CONFIG.getString("deOpTag");

    @EventHandler
    void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUuid = event.getPlayer().getUniqueId();

        if (PlayerCommand.getCommandMillisList().get(playerUuid) != null) {
            final long lastCommandTime = PlayerCommand.getCommandMillisList().get(playerUuid);
            final long millisDifference = System.currentTimeMillis() - lastCommandTime;

            if (millisDifference < 50) {
                event.setCancelled(true);
            }
        }

        PlayerCommand.getCommandMillisList().put(playerUuid, System.currentTimeMillis());

        if (event.isCancelled()) {
            return;
        }

        final String name = player.getDisplayName().toString();
        String prefix = PREFIX_CONFIG.getString(player.getUniqueId().toString());

        if (prefix != null) {
            prefix = ChatColor.translateAlternateColorCodes('&', prefix + " " + ChatColor.RESET);
        } else if (event.getPlayer().isOp()) {
            prefix = OP_TAG;
        } else {
            prefix = DEOP_TAG;
        }

        event.setFormat(prefix + name + ChatColor.RESET + ": " + ChatColor.RESET + "%2$s");
        event.setMessage(
            ChatColor.translateAlternateColorCodes('&', event.getMessage())
        );
    }
}
