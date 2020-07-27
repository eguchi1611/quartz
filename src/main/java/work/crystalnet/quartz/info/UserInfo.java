package work.crystalnet.quartz.info;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import work.crystalnet.quartz.CalculateClickTask;
import work.crystalnet.quartz.Quartz;
import work.crystalnet.quartz.knockback.KnockbackReceiver;
import work.crystalnet.quartz.listener.DeathListener;
import work.crystalnet.quartz.listener.QuitListener;
import work.crystalnet.quartz.queue.Queue;

public class UserInfo {

	private static Set<UserInfo> their = new HashSet<>();
	// Target Player
	private final Player me;
	// User Contents
	private Queue queue;
	private boolean graphic;
	private Location position;
	private boolean noDamage;
	private boolean noAttack;
	private boolean noHunger;
	private KnockbackReceiver knockbackReceiver;
	// Measure Click Per Second
	private BukkitTask task;
	private int cps;
	private int click;
	// Event Listener
	private Set<DeathListener> deathListeners = new HashSet<>();
	private Set<QuitListener> quitListeners = new HashSet<>();

	public void delete() {
		their.remove(this);
	}

	public static void reset() {
		their.clear();
		their = new HashSet<>();
	}

	public static UserInfo of(Player me) {
		for (UserInfo info : their) {
			if (info.me.equals(me))
				return info;
		}
		final UserInfo info = new UserInfo(me);
		their.add(info);
		return info;
	}

	private UserInfo(Player me) {
		this.me = me;
		task = me.getServer().getScheduler().runTaskTimer(Quartz.getInstance(), new CalculateClickTask(this), 0L, 20L);
	}

	public Queue getQueue() {
		return queue;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}

	public boolean isGraphic() {
		return graphic;
	}

	public void setGraphic(boolean graphic) {
		this.graphic = graphic;
	}

	public Location getPosition() {
		return position;
	}

	public void setPosition(Location position) {
		this.position = position;
	}

	public boolean isNoDamage() {
		return noDamage;
	}

	public void setNoDamage(boolean noDamage) {
		this.noDamage = noDamage;
	}

	public boolean isNoAttack() {
		return noAttack;
	}

	public void setNoAttack(boolean noAttack) {
		this.noAttack = noAttack;
	}

	public boolean isNoHunger() {
		return noHunger;
	}

	public void setNoHunger(boolean hunger) {
		noHunger = hunger;
	}

	public KnockbackReceiver getKnockbackReceiver() {
		return knockbackReceiver;
	}

	public void setKnockbackReceiver(KnockbackReceiver knockbackReceiver) {
		this.knockbackReceiver = knockbackReceiver;
	}

	public BukkitTask getTask() {
		return task;
	}

	public int getCps() {
		return cps;
	}

	public void setCps(int cps) {
		this.cps = cps;
	}

	public int getClick() {
		return click;
	}

	public void setClick(int click) {
		this.click = click;
	}

	public Set<DeathListener> getDeathListeners() {
		return deathListeners;
	}

	public Set<QuitListener> getQuitListeners() {
		return quitListeners;
	}
}
