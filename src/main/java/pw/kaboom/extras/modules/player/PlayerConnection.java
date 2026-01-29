package pw.kaboom.extras.modules.player;

import com.destroystokyo.paper.event.profile.PreLookupProfileEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.base.Charsets;
import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent;
import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import io.papermc.paper.event.player.PlayerServerFullCheckEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.modules.server.ServerTabComplete;
import pw.kaboom.extras.modules.player.skin.SkinManager;
import pw.kaboom.extras.util.Utility;

import java.time.Duration;
import java.util.*;
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

    private static final EnumSet<PlayerKickEvent.Cause> ALLOWED_KICK_CAUSES = EnumSet.of(
        PlayerKickEvent.Cause.TIMEOUT,  PlayerKickEvent.Cause.INVALID_VEHICLE_MOVEMENT,
        PlayerKickEvent.Cause.INVALID_PLAYER_MOVEMENT,
        PlayerKickEvent.Cause.INVALID_ENTITY_ATTACKED, PlayerKickEvent.Cause.INVALID_PAYLOAD,
        PlayerKickEvent.Cause.INVALID_COOKIE, PlayerKickEvent.Cause.ILLEGAL_ACTION,
        PlayerKickEvent.Cause.SELF_INTERACTION, PlayerKickEvent.Cause.RESOURCE_PACK_REJECTION
    );

    // Kaboom does not use chat signatures, however some clones may wish to enable them.
    private static final boolean USE_SIGNED_CHAT = Bukkit.getServer().isEnforcingSecureProfiles();
    private static final EnumSet<PlayerKickEvent.Cause> SIGNED_CHAT_KICK_CAUSES = EnumSet.of(
        PlayerKickEvent.Cause.OUT_OF_ORDER_CHAT, PlayerKickEvent.Cause.UNSIGNED_CHAT,
        PlayerKickEvent.Cause.CHAT_VALIDATION_FAILED,
        PlayerKickEvent.Cause.EXPIRED_PROFILE_PUBLIC_KEY,
        PlayerKickEvent.Cause.INVALID_PUBLIC_KEY_SIGNATURE,
        PlayerKickEvent.Cause.TOO_MANY_PENDING_CHATS
    );

    private final Set<UUID> disallowedLogins = Collections.newSetFromMap(new WeakHashMap<>());

    @EventHandler
    void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
        final Player player = Utility.getPlayerExactIgnoreCase(event.getName());

        if (player != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                Component.text("A player with that username is already logged in"));
        }
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

    @EventHandler(ignoreCancelled = true)
    void onPlayerKick(final PlayerKickEvent event) {
        if (ENABLE_KICK) return;

        final PlayerKickEvent.Cause cause = event.getCause();
        if (ALLOWED_KICK_CAUSES.contains(cause)) return;
        if (USE_SIGNED_CHAT && SIGNED_CHAT_KICK_CAUSES.contains(cause)) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onPlayerServerFullCheck(final PlayerServerFullCheckEvent event) {
        if (event.isAllowed()) return;

        if (ALLOW_JOIN_ON_FULL_SERVER) {
            event.allow(true);
            return;
        }

        // #312 - If allow join on full server is off, but join restrictions are disabled, player
        // can still join on full server

        // Full server kicks should be handled differently from other join restrictions since we
        // have a separate configuration value for it
        if (!ENABLE_JOIN_RESTRICTIONS) {
            this.disallowedLogins.add(event.getPlayerProfile().getId());
        }
    }

    // Note that this event gets fired even if FullCheckEvent returns disallowed.
    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.HIGHEST)
    void onPlayerConnectionValidate(final PlayerConnectionValidateLoginEvent event) {
        final PlayerProfile profile = Utility.getConnectionUuid(event.getConnection());
        final Player player = Utility.getPlayerExactIgnoreCase(profile.getName());
        if (player != null) {
            event.kickMessage(Component.text("A player with that username is already logged in"));
            return;
        }

        if (ENABLE_JOIN_RESTRICTIONS) return;

        final boolean disallowed = this.disallowedLogins.remove(profile.getId());
        if (!disallowed) event.allow();
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
