package work.crystalnet.quartz.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import lombok.extern.java.Log;
import work.crystalnet.quartz.Quartz;
import work.crystalnet.quartz.info.UserInfo;
import work.crystalnet.quartz.info.WorldInfo;
import work.crystalnet.quartz.queue.duel.BuildUHCDuelQueue;
import work.crystalnet.quartz.queue.duel.GappleDuelQueue;
import work.crystalnet.quartz.queue.duel.NoDelayDuelQueue;
import work.crystalnet.quartz.queue.duel.Sharp2Prot2DuelQueue;
import work.crystalnet.quartz.queue.duel.SumoDuelQueue;
import work.crystalnet.quartz.serial.WorldCreateSerial;
import work.crystalnet.quartz.serial.WorldCreateSerial.WorldCategory;

@Log
public abstract class Queue {

	private static Set<Queue> their = new HashSet<>();

	public enum Status {
		STANDBY("§d§lStandby"), STARTED("§a§lStarted"), PREEND("§c§lEnd"), END("null");

		private Status(String display) {
			this.display = display;
		}

		private final String display;

		public String getDisplay() {
			return display;
		}
	}

	private static Queue newQueue(Server server, String line) {
		if (line.startsWith("sharp2prot2_ranked"))
			return new Sharp2Prot2DuelQueue(server, true);
		else if (line.startsWith("sharp2prot2"))
			return new Sharp2Prot2DuelQueue(server, false);
		else if (line.startsWith("builduhc_ranked"))
			return new BuildUHCDuelQueue(server, true);
		else if (line.startsWith("builduhc"))
			return new BuildUHCDuelQueue(server, false);
		else if (line.startsWith("gapple_ranked"))
			return new GappleDuelQueue(server, true);
		else if (line.startsWith("gapple"))
			return new GappleDuelQueue(server, false);
		else if (line.startsWith("nodelay_ranked"))
			return new NoDelayDuelQueue(server, true);
		else if (line.startsWith("nodelay"))
			return new NoDelayDuelQueue(server, false);
		else if (line.startsWith("sumo_ranked"))
			return new SumoDuelQueue(server, true);
		else if (line.startsWith("sumo"))
			return new SumoDuelQueue(server, false);
		return null;
	}

	private final String id = RandomStringUtils.randomNumeric(6);
	protected final Server server;

	private final String name;
	private final int limit;
	private final WorldCategory category;

	protected Status status = Status.STANDBY;
	protected long startTime;

	private World useWorld = null;
	private List<Player> enemies = new ArrayList<>();

	public static void reset() {
		their.clear();
		their = new HashSet<>();
	}

	public static Collection<Queue> getAll() {
		return their;
	}

	public Queue(Server server, String name, int limit, WorldCategory category) {
		this.server = server;
		this.name = name;
		this.limit = limit;
		this.category = category;
	}

	public String original() {
		return name.toLowerCase();
	}

	protected boolean match(Player user, Queue queue) {
		return queue != null && getClass() == queue.getClass() && status == Status.STANDBY && enemies.size() < limit;
	}

	public static Queue permit(Player user, String key) {
		final Queue queue = newQueue(user.getServer(), key);
		if (queue == null) {
			if (NumberUtils.isNumber(key)) {
				for (Queue q : Queue.getAll()) {
					if (q.id.equalsIgnoreCase(key)) {
						if (q.getStatus() != Status.STANDBY)
							return null;
						UserInfo.of(user).setQueue(q);
						q.enemies.add(user);
						q.tryStart();
						return q;
					}
				}
			}
			return null;
		}
		Queue will = null;
		for (Queue have : their) {
			if (have.match(user, queue)) {
				if (will != null)
					throw new IllegalStateException("参加可能なキューが２つ以上あります");
				will = have;
			}
		}
		if (will == null) {
			will = queue;
			their.add(will);
		}
		UserInfo.of(user).setQueue(will);
		will.enemies.add(user);
		// Try Start
		will.tryStart();
		return will;
	}

	public void leave(Player target) {
		if (status != Status.STANDBY)
			throw new IllegalStateException();
		UserInfo.of(target).setQueue(null);
		enemies.remove(target);
	}

	public final void setStatus(Status status) {
		this.status = status;
		switch (status) {
		case STARTED:
			startTime = System.currentTimeMillis();
			onStart();
			break;
		case END:
			WorldInfo.of(useWorld).delete();
			final String name = useWorld.getName();
			if (!server.unloadWorld(name, false)) {
				log.severe("ワールドの再読み込みに失敗しました");
			}
			final WorldCreateSerial serial = (WorldCreateSerial) Quartz.getSerializer(Quartz.SERIAL_WORLD_SETTINGS)
					.get(name);
			World reWorld = server.createWorld(new WorldCreator(name));
			final WorldInfo info = WorldInfo.of(reWorld);
			info.setCategory(serial.category);
			info.setSpawns(serial.spawns);

			for (Player user : server.getOnlinePlayers()) {
				final UserInfo ui = UserInfo.of(user);
				if (ui.getQueue() != null) {
					ui.getQueue().tryStart();
				}
			}
			break;
		default:
			break;
		}
	}

	public final boolean tryStart() {
		if (status != Status.STANDBY)
			return false;
		if (limit < enemies.size())
			throw new IllegalStateException();
		if (limit != enemies.size())
			return false;

		useWorld = null;
		for (World world : server.getWorlds()) {
			final WorldInfo info = WorldInfo.of(world);
			if (info.getUsing() == null && info.getCategory() == category) {
				useWorld = world;
				continue;
			}
		}
		if (useWorld == null) {
			for (Player enemy : enemies) {
				enemy.sendMessage("§c使えるワールドが見つかりませんでした。もう少しお待ち下さい。");
			}
			return false;
		}
		for (Player enemy : enemies) {
			// Check
			if (!UserInfo.of(enemy).getQueue().equals(this))
				throw new IllegalStateException("プレイヤーとキューの同期ができていないため開始できませんでした");
			enemy.sendMessage("§7Please wait a little longer.");
		}
		startTime = System.currentTimeMillis();
		setStatus(Status.STARTED);
		return true;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getLimit() {
		return limit;
	}

	public WorldCategory getCategory() {
		return category;
	}

	public Status getStatus() {
		return status;
	}

	public World getUseWorld() {
		return useWorld;
	}

	public List<Player> getEnemies() {
		return enemies;
	}

	public String getDisplayID() {
		return getId();
	}

	protected void onStart() {
	}
}
