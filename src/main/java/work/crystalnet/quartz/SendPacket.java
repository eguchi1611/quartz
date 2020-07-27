package work.crystalnet.quartz;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;

public final class SendPacket {

	public static void sendTitle(Player user, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
		((CraftPlayer) user).getHandle().playerConnection.sendPacket(length);
		if (title != null) {
			final PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE,
					CraftChatMessage.fromString(title)[0]);
			((CraftPlayer) user).getHandle().playerConnection.sendPacket(packetTitle);
		}
		if (subtitle != null) {
			final PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(
					PacketPlayOutTitle.EnumTitleAction.SUBTITLE, CraftChatMessage.fromString(subtitle)[0]);
			((CraftPlayer) user).getHandle().playerConnection.sendPacket(packetSubtitle);
		}
	}

	public static void sendActionbar(Player user, String text) {
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
		((CraftPlayer) user).getHandle().playerConnection.sendPacket(packet);
	}
}
