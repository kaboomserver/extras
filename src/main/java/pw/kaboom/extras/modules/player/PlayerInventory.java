/*package pw.kaboom.extras;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

class PlayerInventory extends PacketAdapter {
	private Main main;
	public PlayerInventory(Main main) {
		/*super(main, ListenerPriority.HIGH, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS);
		super(main, ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_METADATA);
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		try {
			LivingEntity entity = (LivingEntity) event.getPacket().getEntityModifier(event).read(0);
			entity.getEquipment().getArmorContents();
		} catch (Exception exception) {
			LivingEntity entity = (LivingEntity) event.getPacket().getEntityModifier(event).read(0);
			entity.getEquipment().setArmorContents(
				new ItemStack[] {null, null, null, null}
			);
			System.out.println("slot");
		}
	}

	/*@Override
	public void onPacketSending(PacketEvent event) {
		if (event.getPacketType().equals(PacketType.Play.Server.SET_SLOT)) {
			try {
				event.getPacket().getItemModifier().read(0);
			} catch (Exception exception) {
				PacketContainer packet = event.getPacket();
				packet.getItemModifier().write(0, new ItemStack(Material.AIR));
				event.setPacket(packet);
				System.out.println("slot");
			}
		} else {
			try {
				for (int i = 0; i < 50; i++) {
					event.getPacket().getItemListModifier().read(i);
				}
			} catch (Exception exception) {
				System.out.println("window1");
				PacketContainer packet = event.getPacket();
				for (int i = 0; i < 50; i++) {
					packet.getItemListModifier().write(i, null);
				}
				
				event.setPacket(packet);
				System.out.println("window");
			}
		}
	}
}*/
