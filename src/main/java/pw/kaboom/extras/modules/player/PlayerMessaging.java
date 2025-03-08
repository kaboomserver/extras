package pw.kaboom.extras.modules.player;

import com.google.common.primitives.Longs;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import pw.kaboom.extras.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class PlayerMessaging implements PluginMessageListener {
    public static final String REGISTER = "extras:register";
    public static final String UNREGISTER = "extras:unregister";
    public static final String MESSAGE = "extras:message";

    private static final Component ERROR =
            Component.text("Could not send plugin channel message.", NamedTextColor.RED);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
            Executors.newSingleThreadScheduledExecutor();

    private final Main plugin;

    public PlayerMessaging(Main plugin) {
        this.plugin = plugin;

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            synchronized (this.listening) {
                final Iterator<Map.Entry<String, Set<Player>>> iterator =
                        this.listening.entrySet().iterator();

                while (iterator.hasNext()) {
                    final Map.Entry<String, Set<Player>> entry = iterator.next();

                    final Set<Player> players = entry.getValue();
                    synchronized (players) {
                        // try and avoid issues with other plugins causing player obj leaks
                        int onlineCount = 0;

                        for (final Player player: players) {
                            if (!player.isOnline()) continue;
                            onlineCount++;
                        }

                        if (onlineCount != 0) continue;
                        iterator.remove();
                    }
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private final Map<String, Set<Player>> listening = Collections.synchronizedMap(new HashMap<>());

    private static String readString(DataInput dataInput) throws IOException {
        final int len = dataInput.readUnsignedByte();
        final byte[] buf = new byte[len];
        dataInput.readFully(buf);
        return new String(buf, StandardCharsets.US_ASCII);
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

    private void handleUnregister(final Player player, final DataInput input) throws IOException {
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
            // we start at null so that we do not read the bytes if the
            // only player in the channel is the sender of the message
            byte[] msg = null;

            for (final Player playerInSet : players) {
                if (playerInSet == player) continue;
                if (msg == null) {
                    final int remaining = input.available();

                    // remaining count + channel name + channel length header + uuid
                    // note: calls to channelName.length() are safe because we only read ASCII
                    final int realLength = remaining + channelName.length() + 17;
                    if (realLength > Messenger.MAX_MESSAGE_SIZE) {
                        player.sendMessage(ERROR);
                        return;
                    }

                    msg = new byte[realLength];
                    msg[0] = (byte) channelName.length();
                    int offset = 1;

                    System.arraycopy(
                            channelName.getBytes(StandardCharsets.US_ASCII),
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
        } catch (Exception ignored) {
            player.sendMessage(ERROR);
        }
    }
}
