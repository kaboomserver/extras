package pw.kaboom.extras.modules.player.skin;

import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

public record SkinFillRequest(WeakReference<Player> fromPlayer,
                              String toUser, Consumer<@Nullable SkinData> resultConsumer)  {

}
