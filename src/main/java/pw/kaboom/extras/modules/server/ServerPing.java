package pw.kaboom.extras;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;

class ServerPing implements Listener {
	@EventHandler
	void onServerListPing(PaperServerListPingEvent event) {
		if (event.getClient().getProtocolVersion() != -1) {
			event.setProtocolVersion(event.getClient().getProtocolVersion());
		} else {
			event.setProtocolVersion(498);
		}
		event.setVersion("1.14.4");
	}
}
