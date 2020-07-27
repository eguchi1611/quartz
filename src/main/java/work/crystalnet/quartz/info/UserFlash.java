package work.crystalnet.quartz.info;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import work.crystalnet.quartz.display.SmartObjective;

public class UserFlash {

	private static Set<UserFlash> their = new HashSet<>();
	// Target Player
	private final Player me;
	// User Contents
	private String header = "";
	private String footer = "";
	private String prefix = "";
	private String suffix = "";
	private String title = "";
	private List<String> line = Collections.emptyList();

	public static void reset() {
		their.clear();
		their = new HashSet<>();
	}

	public void delete() {
		their.remove(this);
	}

	public static UserFlash of(Player me) {
		for (UserFlash flash : their) {
			if (flash.me.equals(me))
				return flash;
		}
		final UserFlash flash = new UserFlash(me);
		their.add(flash);
		return flash;
	}

	private UserFlash(Player me) {
		this.me = me;
	}

	public void setObjective(SmartObjective objective) {
		title = objective.title();
		line = objective.lines();
		sendObjectStylePacket();
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
		sendListStylePacket();
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
		sendListStylePacket();
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		if (prefix.length() > 16)
			throw new IllegalArgumentException("suffix.length > 16 is not allowed");
		this.prefix = prefix;
		sendTeamStylePacket();
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		if (suffix.length() > 16)
			throw new IllegalArgumentException("suffix.length > 16 is not allowed");
		this.suffix = suffix;
		sendTeamStylePacket();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		sendObjectStylePacket();
	}

	public List<String> getLine() {
		return line;
	}

	public void setLine(List<String> line) {
		this.line = line;
		sendObjectStylePacket();
	}

	public void sendObjectStylePacket() {
		final String name = "sidebar";
		final Scoreboard scoreboard = me.getScoreboard();
		Objective objective = scoreboard.getObjective(name);
		if (objective != null) {
			objective.unregister();
		}
		objective = scoreboard.registerNewObjective(name, "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(title);
		for (int i = 0; i < line.size(); i++) {
			objective.getScore(line.get(i)).setScore(i);
		}
	}

	public void sendTeamStylePacket() {
		for (Player user : me.getServer().getOnlinePlayers()) {
			final String teamName = me.getName().toLowerCase();
			final Scoreboard scoreboard = user.getScoreboard();
			Team team = scoreboard.getTeam(teamName);
			if (team == null) {
				team = scoreboard.registerNewTeam(teamName);
				team.addEntry(me.getName());
			}
			team.setPrefix(prefix);
			team.setSuffix(suffix);
		}
	}

	public void sendListStylePacket() {
		IChatBaseComponent headerText = ChatSerializer.a("{\"text\":\"" + (header != null ? header : "") + "\"}");
		IChatBaseComponent footerText = ChatSerializer.a("{\"text\":\"" + (footer != null ? footer : "") + "\"}");
		final PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(headerText);
		try {
			Field field = packet.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(packet, footerText);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
		}
		((CraftPlayer) me).getHandle().playerConnection.sendPacket(packet);
	}
}
