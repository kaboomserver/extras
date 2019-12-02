package pw.kaboom.extras;

import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class SkinDownloader {
	private String texture;
	private String signature;

	public boolean fetchSkinData(String playerName) {
		try {
			final URL skinUrl = new URL("https://api.ashcon.app/mojang/v2/user/" + playerName);
			final HttpsURLConnection skinConnection = (HttpsURLConnection) skinUrl.openConnection();
			skinConnection.setConnectTimeout(0);
			skinConnection.setDefaultUseCaches(false);
			skinConnection.setUseCaches(false);

			if (skinConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				final InputStreamReader skinStream = new InputStreamReader(skinConnection.getInputStream());
				final JsonObject responseJson = new JsonParser().parse(skinStream).getAsJsonObject();
				final JsonObject rawSkin = responseJson.getAsJsonObject("textures").getAsJsonObject("raw");
				texture = rawSkin.get("value").getAsString();
				signature = rawSkin.get("signature").getAsString();
				try {
					skinStream.close();
				} catch (Exception exception) {
					System.out.println(exception);
				}
				return true;
			}
		} catch (Exception exception) {
		}
		return false;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public String getTexture() {
		return texture;
	}
}