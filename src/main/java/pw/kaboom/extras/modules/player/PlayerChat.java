package pw.kaboom.extras.modules.player;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class PlayerChat implements Listener {
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
        public @Nonnull Component render(@Nonnull Player player,
                                         @Nonnull Component displayName,
                                         @Nonnull Component component,
                                         @Nonnull Audience audience) {
            Component newComponent = Component.empty();
            final Component prefix;
            Component prefix1;

            try {
                prefix1 = PlayerPrefix.getPrefix(player);
            } catch (IOException e) {
                e.printStackTrace();
                prefix1 = PlayerPrefix.getDefaultPrefix(player);
            }

            prefix = prefix1;
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
