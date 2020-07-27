package work.crystalnet.quartz.display;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import work.crystalnet.quartz.info.UserInfo;

public class DuelObjective implements SmartObjective {

	private final Player me;
	private final Player enemy;
	private final String status;

	public DuelObjective(Player me, Player enemy, String status) {
		this.me = me;
		this.enemy = enemy;
		this.status = status;
	}

	@Override
	public List<String> lines() {
		final UserInfo info = UserInfo.of(me);
		final List<String> line = new ArrayList<>();
		line.add("Åòbcrystalnet.work");
		line.add(StringUtils.repeat(" ", line.size()));
		line.add("Time: Åòe" + new SimpleDateFormat("mm:ss")
				.format(new Date(System.currentTimeMillis() - info.getQueue().getStartTime())));
		line.add(StringUtils.repeat(" ", line.size()));
		line.add(" - PING: Åò2" + ((CraftPlayer) enemy).getHandle().ping);
		line.add(" - CPS: Åò2" + UserInfo.of(enemy).getCps());
		line.add("Opponent: Åòc" + String.join(", ", info.getQueue().getEnemies().stream().filter(e -> !me.equals(e))
				.map(Player::getName).collect(Collectors.toList())));
		line.add(StringUtils.repeat(" ", line.size()));
		line.add("Status: " + status);
		line.add("Game: " + info.getQueue().getName());
		line.add(StringUtils.repeat(" ", line.size()));
		line.add("Åò7" + new SimpleDateFormat("MM/dd/yy").format(new Date()) + " Åò8" + me.getWorld().getName());
		return line;
	}

	@Override
	public String title() {
		return "ÅòeÅòlDuel";
	}
}
