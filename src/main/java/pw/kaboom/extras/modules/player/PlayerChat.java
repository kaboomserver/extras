package pw.kaboom.extras.modules.player;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pw.kaboom.extras.Main;

import java.util.UUID;

public final class PlayerChat implements Listener {
    private static final FileConfiguration CONFIG = JavaPlugin.getPlugin(Main.class).getConfig();
    private static final FileConfiguration PREFIX_CONFIG = JavaPlugin
            .getPlugin(Main.class).getPrefixConfig();

    private static final Component OP_TAG = LegacyComponentSerializer
            .legacySection().deserialize(CONFIG.getString("opTag", ""));
    private static final Component DEOP_TAG = LegacyComponentSerializer
            .legacySection().deserialize(CONFIG.getString("deOpTag", ""));
    private static final PlayerChatRenderer CHAT_RENDERER = new PlayerChatRenderer();

    @EventHandler(priority = EventPriority.HIGHEST)
    void onAsyncChatEventProcess(final AsyncChatEvent event) {
        final UUID playerUuid = event.getPlayer().getUniqueId();

        if (PlayerCommand.getCommandMillisList().get(playerUuid) != null) {
            final long lastCommandTime = PlayerCommand.getCommandMillisList().get(playerUuid);
            final long millisDifference = System.currentTimeMillis() - lastCommandTime;

            if (millisDifference < 50) {
                event.setCancelled(true);
            }
        }

        PlayerCommand.getCommandMillisList().put(playerUuid, System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onAsyncChatEventRenderer(final AsyncChatEvent event) {
        event.renderer(CHAT_RENDERER);
    }

    public static class PlayerChatRenderer implements ChatRenderer {

        @Override
        public @NotNull Component render(@NotNull Player player,
                                         @NotNull Component displayName,
                                         @NotNull Component component,
                                         @NotNull Audience audience) {
            Component newComponent = Component.empty();
            final String legacyPrefix = PREFIX_CONFIG.getString(player.getUniqueId().toString());
            final Component prefix = legacyPrefix == null ?
                    ((player.isOp()) ? OP_TAG : DEOP_TAG)
                    : LegacyComponentSerializer.legacyAmpersand().deserialize(legacyPrefix)
                    .append(Component.space());

            final String message = ((TextComponent) component).content();

            newComponent = newComponent
                    .append(prefix)
                    .append(displayName)
                    .append(Component.text(":"))
                    .append(Component.space());

            return newComponent.append(LegacyComponentSerializer
                .legacyAmpersand()
                .deserialize(message)
            );
        }
    }
}
