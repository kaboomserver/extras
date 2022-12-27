package pw.kaboom.extras.skin.response;

public class ProfileResponse {

	public ProfileResponse(String name, String id) {
		this.name = name;
		this.id = id;
	}

	private final String name;
	private final String id;

	public String name() {
		return name;
	}

	public String id() {
		return id;
	}
}
