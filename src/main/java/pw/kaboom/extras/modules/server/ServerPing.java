package pw.kaboom.extras.modules.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;

public final class ServerPing implements Listener {
	@EventHandler
	void onServerListPing(final PaperServerListPingEvent event) {
		if (event.getClient().getProtocolVersion() == -1) {
			final int protocol = 754;
			event.setProtocolVersion(protocol);
		} else {
			event.setProtocolVersion(event.getClient().getProtocolVersion());
		}
		event.setVersion("1.16.4");
	}
}
