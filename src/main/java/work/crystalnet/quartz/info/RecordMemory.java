package work.crystalnet.quartz.info;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import work.crystalnet.quartz.record.DataRecord;

public class RecordMemory {

	private static Set<RecordMemory> their = new HashSet<>();
	// Target Player
	private final Player me;
	// User Contents
	private int rank;
	private int point;
	private int coin;
	private int token;
	private String lastIp;

	public static void reset() {
		their.clear();
		their = new HashSet<>();
	}

	public static RecordMemory of(Player me) {
		for (RecordMemory record : their) {
			if (record.me.equals(me))
				return record;
		}
		final RecordMemory memory = new RecordMemory(me);
		if (!me.getServer().getServicesManager().load(DataRecord.class).load(me.getUniqueId(), memory)) {
			me.kickPlayer("データベースに接続できませんでした。管理者にお問い合わせください。");
		}
		their.add(memory);
		return memory;
	}

	private RecordMemory(Player me) {
		this.me = me;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public int getToken() {
		return token;
	}

	public void setToken(int token) {
		this.token = token;
	}

	public String getLastIp() {
		return lastIp;
	}

	public void setLastIp(String lastIp) {
		this.lastIp = lastIp;
	}
}
