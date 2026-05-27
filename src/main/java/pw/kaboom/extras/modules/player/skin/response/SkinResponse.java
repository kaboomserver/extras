package pw.kaboom.extras.modules.player.skin.response;

import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.List;

public record SkinResponse(String id, String name, List<ProfileProperty> properties) {}
