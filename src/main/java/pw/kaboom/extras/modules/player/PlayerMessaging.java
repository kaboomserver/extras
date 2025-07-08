package pw.kaboom.extras.modules.player;

import com.google.common.primitives.Longs;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import pw.kaboom.extras.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class PlayerMessaging implements PluginMessageListener, Listener {
    public static final String REGISTER = "extras:register";
    public static final String UNREGISTER = "extras:unregister";
    public static final String MESSAGE = "extras:message";

    private static final Component ERROR =
            Component.text("Could not send plugin channel message.", NamedTextColor.RED);
    private static final byte END_CHAR_MASK = (byte) 0x80;

    private final Main plugin;

    public PlayerMessaging(final Main plugin) {
        this.plugin = plugin;
    }

    private final Map<String, Set<Player>> listening = Collections.synchronizedMap(new HashMap<>());

    private static String readString(final DataInput dataInput) throws IOException {
        final byte[] buf = new byte[255];
        int idx = 0;

        for(;;) {
            final byte input = dataInput.readByte();
            if (idx == buf.length) throw new IOException("Index overflow");
            final boolean isLast = (input & END_CHAR_MASK) == END_CHAR_MASK;
            buf[idx++] = (byte) (input & ~END_CHAR_MASK);

            if (isLast) break;
        }

        return new String(buf, 0, idx, StandardCharsets.US_ASCII);
    }

    private void handleRegister(final Player player, final DataInput input) throws IOException {
        this.listening.compute(readString(input), (k, v) -> {
            v = v == null ?
                            Collections.synchronizedSet(
                                    Collections.newSetFromMap(
                                            new WeakHashMap<>()
                                    )
                            )
                            :
                            v;
            v.add(player);
            return v;
        });
    }

    private void handleUnregister(final Player player,
                                  final DataInput input) throws IOException {
        this.listening.computeIfPresent(readString(input), (k, v) -> {
            v.remove(player);
            return v;
        });
    }

    private void handleMessage(final Player player,
                               final DataInputStream input)
            throws IOException {
        final String channelName = readString(input);
        final Set<Player> players = this.listening.get(channelName);
        if (players == null) return;

        synchronized (players) {
            // we initialize as null so that we do not read the incoming
            // data and serialize the payload if the only recipient
            // would be the sender, who we do not send to
            byte[] msg = null;

            for (final Player playerInSet : players) {
                if (playerInSet == player) continue;
                if (msg == null) {
                    final int remaining = input.available();

                    // remaining count + channel name + uuid
                    // note: calls to channelName.length() are safe because we only read ASCII
                    final int realLength = remaining + channelName.length() + 16;
                    if (realLength > Messenger.MAX_MESSAGE_SIZE) {
                        player.sendMessage(ERROR);
                        return;
                    }

                    msg = new byte[realLength];
                    int offset = 0;

                    final byte[] nameBytes = channelName.getBytes(StandardCharsets.US_ASCII);
                    nameBytes[nameBytes.length - 1] |= END_CHAR_MASK;
                    System.arraycopy(
                            nameBytes,
                            0,
                            msg,
                            offset,
                            channelName.length()
                    );
                    offset += channelName.length();

                    final UUID uuid = player.getUniqueId();
                    System.arraycopy(
                            Longs.toByteArray(uuid.getMostSignificantBits()),
                            0,
                            msg,
                            offset,
                            8)
                    ;
                    offset += 8;

                    System.arraycopy(
                            Longs.toByteArray(uuid.getLeastSignificantBits()),
                            0,
                            msg,
                            offset,
                            8
                    );
                    offset += 8;

                    input.readFully(msg, offset, remaining);
                }

                playerInSet.sendPluginMessage(this.plugin, MESSAGE, msg);
            }
        }
    }

    @Override
    public void onPluginMessageReceived(final @NotNull String channelName,
                                        final @NotNull Player player,
                                        final byte[] bytes) {
        try {
            switch (channelName) {
                case REGISTER -> handleRegister(
                        player,
                        new DataInputStream(new FastByteArrayInputStream(bytes))
                );
                case UNREGISTER -> handleUnregister(
                        player,
                        new DataInputStream(new FastByteArrayInputStream(bytes))
                );
                case MESSAGE -> handleMessage(
                        player,
                        new DataInputStream(new FastByteArrayInputStream(bytes))
                );
            }
        } catch (final Exception ignored) {
            player.sendMessage(ERROR);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player removedPlayer = event.getPlayer();

        synchronized (this.listening) {
            final Iterator<Map.Entry<String, Set<Player>>> listeningIterator =
                    this.listening.entrySet().iterator();

            while (listeningIterator.hasNext()) {
                final Map.Entry<String, Set<Player>> entry = listeningIterator.next();
                final Set<Player> players = entry.getValue();
                synchronized (players) {
                    players.remove(removedPlayer);

                    if (players.isEmpty()) listeningIterator.remove();
                }
            }
        }
    }
}
