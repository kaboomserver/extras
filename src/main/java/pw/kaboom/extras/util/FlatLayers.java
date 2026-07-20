package pw.kaboom.extras.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.papermc.paper.registry.TypedKey;
import org.bukkit.block.BlockType;

public final class FlatLayers {
    private final JsonArray layers = new JsonArray();

    public FlatLayers addLayer(final TypedKey<BlockType> blockType,
                               final int height) {
        final var layer = new JsonObject();

        layer.addProperty("block", blockType.asString());
        layer.addProperty("height", height);

        this.layers.add(layer);
        return this;
    }

    public String build() {
        final var layers = new JsonObject();
        layers.add("layers", this.layers);
        return layers.toString();
    }
}
