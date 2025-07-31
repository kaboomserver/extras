package pw.kaboom.extras.modules.player;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

public final class PlayerChat implements Listener {
    private static final PlayerChatRenderer CHAT_RENDERER = new PlayerChatRenderer();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    void onAsyncChatEventProcess(final AsyncChatEvent event) {
        final UUID playerUuid = event.getPlayer().getUniqueId();

        if (PlayerCommand.getCommandMillisList().get(playerUuid) != null) {
            final long lastCommandTime = PlayerCommand.getCommandMillisList().get(playerUuid);
            final long millisDifference = System.currentTimeMillis() - lastCommandTime;

            if (millisDifference < 50) {
                event.setCancelled(true);
                return;
            }
        }

        PlayerCommand.getCommandMillisList().put(playerUuid, System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onAsyncChatEventRenderer(final AsyncChatEvent event) {
        event.renderer(ChatRenderer.viewerUnaware(CHAT_RENDERER));
    }

    public static class PlayerChatRenderer implements ChatRenderer.ViewerUnaware {
        private static final Pattern URL_PATTERN = Pattern
                .compile("((https?://(ww(w|\\d)\\.)?|ww(w|\\d))[-a-zA-Z0-9@:%._+~#=]{1,256}"
                                 + "\\.[a-zA-Z0-9]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*))");
        private static final Style URL_STYLE = Style.style(NamedTextColor.BLUE,
                                                           TextDecoration.UNDERLINED);

        // Match vanilla by only rendering section sign legacy colors in vanilla chat
        private static final LegacyComponentSerializer VANILLA_CHAT_SERIALIZER =
                LegacyComponentSerializer.builder()
                        .character(LegacyComponentSerializer.SECTION_CHAR)
                        .extractUrls(URL_PATTERN, URL_STYLE)
                        .hexColors()
                        .build();

        private static final LegacyComponentSerializer CHAT_SERIALIZER =
                LegacyComponentSerializer.builder()
                        .character(LegacyComponentSerializer.AMPERSAND_CHAR)
                        .extractUrls(URL_PATTERN, URL_STYLE)
                        .hexColors()
                        .build();

        private Component renderVanilla(final @Nonnull Component displayName,
                                        final @Nonnull Component component) {
            return Component.translatable("chat.type.text", displayName, component);
        }

        @Override
        public @Nonnull Component render(@Nonnull Player player,
                                         @Nonnull Component displayName,
                                         @Nonnull Component component) {
            final String message = ((TextComponent) component).content();
            final Component prefix;
            Component prefix1;

            try {
                prefix1 = PlayerPrefix.getPrefix(player);
            } catch (IOException e) {
                e.printStackTrace();
                prefix1 = PlayerPrefix.getDefaultPrefix(player);
            }

            prefix = prefix1;
            if (prefix == null) {
                return renderVanilla(displayName, VANILLA_CHAT_SERIALIZER.deserialize(message));
            }

            return Component.empty()
                    .append(prefix)
                    .append(displayName)
                    .append(Component.text(":"))
                    .append(Component.space())
                    .append(CHAT_SERIALIZER.deserialize(message));
        }
    }
}
