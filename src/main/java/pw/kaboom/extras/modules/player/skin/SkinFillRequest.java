package pw.kaboom.extras.modules.player.skin;

import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.function.BiConsumer;

public record SkinFillRequest(UUID fromPlayer, String toUser,
                              BiConsumer<Player, @Nullable SkinData> resultConsumer)  {

}
