package work.crystalnet.quartz.display;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import work.crystalnet.quartz.info.UserInfo;
import work.crystalnet.quartz.info.RecordMemory;
import work.crystalnet.quartz.queue.Queue;

public class LobbyObjective implements SmartObjective {

	private Player me;

	public LobbyObjective(Player me) {
		this.me = me;
	}

	@Override
	public List<String> lines() {
		final RecordMemory record = RecordMemory.of(me);
		final UserInfo info = UserInfo.of(me);
		final List<String> row = new ArrayList<>();
		row.add("��bcrystalnet.work");
		row.add(StringUtils.repeat(" ", row.size()));
		row.add("Token: ��2" + NumberFormat.getNumberInstance().format(record.getToken()));
		row.add("Coins: ��6" + NumberFormat.getNumberInstance().format(record.getCoin()));
		row.add(StringUtils.repeat(" ", row.size()));
		row.add("��e��lPlay points: ��l" + record.getPoint());
		row.add(StringUtils.repeat(" ", row.size()));
		final Queue queue = info.getQueue();
		if (queue != null) {
			row.add("Queue: " + queue.getName());
			row.add(StringUtils.repeat(" ", row.size()));
		}
		row.add("��7" + new SimpleDateFormat("MM/dd/yy").format(new Date()) + " ��8" + me.getWorld().getName());
		return row;
	}

	@Override
	public String title() {
		return "��e��lLobby";
	}
}
