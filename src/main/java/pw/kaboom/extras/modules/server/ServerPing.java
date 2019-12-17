package pw.kaboom.extras.modules.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;

public class ServerPing implements Listener {
	@EventHandler
	void onServerListPing(PaperServerListPingEvent event) {
		if (event.getClient().getProtocolVersion() == -1) {
			event.setProtocolVersion(573);
		} else {
			event.setProtocolVersion(event.getClient().getProtocolVersion());
		}
		event.setVersion("1.15");
	}
}
