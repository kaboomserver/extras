package pw.kaboom.extras.modules.player;

import com.destroystokyo.paper.event.profile.PreLookupProfileEvent;
import com.google.common.base.Charsets;
import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent;
import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import io.papermc.paper.event.player.PlayerServerFullCheckEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.modules.server.ServerTabComplete;
import pw.kaboom.extras.modules.player.skin.SkinManager;
import pw.kaboom.extras.util.Utility;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class PlayerConnection implements Listener {
    private static final FileConfiguration CONFIG = JavaPlugin.getPlugin(Main.class).getConfig();
    private static final Component TITLE =
            LegacyComponentSerializer.legacySection()
                .deserialize(
                        CONFIG.getString(
                                "playerJoinTitle",
                                ""
                        )
                );
    private static final Component SUBTITLE =
            LegacyComponentSerializer.legacySection()
                .deserialize(
                        CONFIG.getString(
                                "playerJoinSubtitle",
                                ""
                        )
                );
    private static final Duration FADE_IN = Duration.ofMillis(50);
    private static final Duration STAY = Duration.ofMillis(8000);
    private static final Duration FADE_OUT = Duration.ofMillis(250);

    private static final boolean ENABLE_KICK = CONFIG.getBoolean("enableKick");
    private static final boolean ENABLE_JOIN_RESTRICTIONS = CONFIG.getBoolean(
                                                            "enableJoinRestrictions");
    private static final boolean ALLOW_JOIN_ON_FULL_SERVER = CONFIG.getBoolean(
                                                             "allowJoinOnFullServer");
    private static final boolean OP_ON_JOIN = CONFIG.getBoolean("opOnJoin");
    private static final boolean RANDOMIZE_SPAWN = CONFIG.getBoolean("randomizeSpawn");
    private final Set<UUID> disallowedLogins = new HashSet<>(5);

    @EventHandler
    void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
        final Player player = Utility.getPlayerExactIgnoreCase(event.getName());

        if (player != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                Component.text("A player with that username is already logged in"));
        }

        /*try {
            final PlayerProfile profile = event.getPlayerProfile();

            UUID offlineUUID = UUID.nameUUIDFromBytes(
                ("OfflinePlayer:" + event.getName()).getBytes(Charsets.UTF_8));

            profile.setId(offlineUUID);

            SkinDownloader skinDownloader = new SkinDownloader();
            skinDownloader.fillJoinProfile(profile, event.getName(), event.getUniqueId());
        } catch (Exception ignored) {
        }*/
    }

    @EventHandler
    void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (OP_ON_JOIN && !player.isOp()) {
            player.setOp(true);
        }

        player.showTitle(Title.title(
            TITLE,
            SUBTITLE,
            Title.Times.times(FADE_IN, STAY, FADE_OUT)
        ));

        ServerTabComplete.getLoginNameList().put(player.getUniqueId(), player.getName());

        if (!player.getPlayerProfile().hasTextures()) {
            SkinManager.applySkin(player, player.getName(), false);
        }
    }

    @EventHandler
    void onPlayerKick(final PlayerKickEvent event) {
        if (!ENABLE_KICK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPlayerServerFullCheck(final PlayerServerFullCheckEvent event) {
        if (ALLOW_JOIN_ON_FULL_SERVER) {
            event.allow(true);
        } else if (!event.isAllowed()) {
            this.disallowedLogins.add(event.getPlayerProfile().getId());
        }
    }

    // Note that this event gets fired even if FullCheckEvent returns disallowed, meaning we need
    // to keep track of the player's allowed state across events. Yuck.
    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    void onPlayerConnectionValidate(final PlayerConnectionValidateLoginEvent event) {
        // #312 - If allow join on full server is off, but join restrictions are disabled, player
        // can still join on full server

        // Full server kicks should be handled differently from other join restrictions since we
        // have a separate configuration value for it
        final UUID uuid = Utility.getConnectionUuid(event.getConnection());
        final boolean disallowed = this.disallowedLogins.remove(uuid);

        // If uuid is null, disallowedLogins will never contain it. So we always let connections
        // without a UUID through if join restrictions are disabled.
        if (!ENABLE_JOIN_RESTRICTIONS && !disallowed) {
            event.allow();
        }
    }

    @EventHandler
    void onPlayerSpawn(final AsyncPlayerSpawnLocationEvent event) {
        if (!RANDOMIZE_SPAWN || !event.isNewPlayer()) return;

        final World world = event.getSpawnLocation().getWorld();
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        final double teleportAmount = 500000D;
        final Location location = new Location(
            world,
            random.nextDouble(-teleportAmount, teleportAmount),
            100,
            random.nextDouble(-teleportAmount, teleportAmount)
        );

        event.setSpawnLocation(location);
    }

    @EventHandler
    void onPlayerQuit(final PlayerQuitEvent event) {
        PlayerCommand.getCommandMillisList().remove(event.getPlayer().getUniqueId());
        //PlayerInteract.interactMillisList.remove(event.getPlayer().getUniqueId());
        ServerTabComplete.getLoginNameList().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    void onPreLookupProfile(final PreLookupProfileEvent event) {
        // Disable Mojang API calls, we don't need them
        UUID offlineUUID = UUID.nameUUIDFromBytes(
            ("OfflinePlayer:" + event.getName()).getBytes(Charsets.UTF_8));
        event.setUUID(offlineUUID);
    }
}
