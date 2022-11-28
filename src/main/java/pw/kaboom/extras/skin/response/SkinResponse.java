package pw.kaboom.extras.skin.response;

import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.List;
import java.util.Objects;

public final class SkinResponse {

	private final String id;
	private final String name;
	private final List<ProfileProperty> properties;

	public SkinResponse(String id, String name, List<ProfileProperty> properties) {
		this.id = id;
		this.name = name;
		this.properties = properties;
	}

	public String id() {
		return id;
	}

	public String name() {
		return name;
	}

	public List<ProfileProperty> properties() {
		return properties;
	}


}
