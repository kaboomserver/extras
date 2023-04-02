package pw.kaboom.extras.modules.player;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
        private static final TextReplacementConfig URL_REPLACEMENT_CONFIG =
                TextReplacementConfig
                        .builder()
                .match(Pattern
                        .compile("((https?://(www\\.)?)?[-a-zA-Z0-9@:%._+~#=]" +
                                "{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*))"))
                .replacement((b, c) -> {
                    if (c == null) {
                        return null;
                    }

                    if (b.groupCount() < 1) {
                        return null;
                    }

                    final String content = b.group(1);
                    final String url;

                    /*
                    Minecraft doesn't accept "www.google.com" or "google.com" as URLs
                    in click events
                     */
                    if (content.contains("://")) {
                        url = content;
                    } else {
                        url = "https://" + content;
                    }

                    return Component.text(content, NamedTextColor.BLUE)
                            .decorate(TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.openUrl(url));
                })
                .build();

        private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER =
                LegacyComponentSerializer
                        .legacyAmpersand();

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

            final Component messageWithColorCodes = LEGACY_COMPONENT_SERIALIZER
                    .deserialize(message);
            final Component completedMessage = messageWithColorCodes
                    .replaceText(URL_REPLACEMENT_CONFIG);

            return newComponent.append(completedMessage);
        }
    }
}
