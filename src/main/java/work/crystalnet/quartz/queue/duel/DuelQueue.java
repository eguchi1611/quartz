package work.crystalnet.quartz.queue.duel;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import work.crystalnet.quartz.Quartz;
import work.crystalnet.quartz.SendPacket;
import work.crystalnet.quartz.display.DuelObjective;
import work.crystalnet.quartz.info.UserFlash;
import work.crystalnet.quartz.info.UserInfo;
import work.crystalnet.quartz.info.WorldInfo;
import work.crystalnet.quartz.listener.DeathListener;
import work.crystalnet.quartz.listener.QuitListener;
import work.crystalnet.quartz.queue.Queue;
import work.crystalnet.quartz.serial.WorldCreateSerial.WorldCategory;

public abstract class DuelQueue extends Queue implements DeathListener, QuitListener {

	protected final boolean ranked;

	public boolean isRanked() {
		return ranked;
	}

	protected long endTime;
	protected Player winner;

	public DuelQueue(Server server, String name, WorldCategory category, boolean ranked) {
		super(server, name, 2, category);
		this.ranked = ranked;
	}

	@Override
	public String getDisplayID() {
		return ranked ? "HIDDEN" : getId();
	}

	@Override
	protected boolean match(Player user, Queue queue) {
		return super.match(user, queue) && queue instanceof DuelQueue && ((DuelQueue) queue).ranked == ranked;
	}

	@Override
	public String original() {
		return super.original() + (ranked ? "_ranked" : "");
	}

	@Override
	public String getName() {
		return (ranked ? "˜d" : "˜b") + super.getName() + "˜r";
	}

	@Override
	protected void onStart() {
		getUseWorld().setPVP(true);
		final List<Location> spawns = WorldInfo.of(getUseWorld()).getSpawns();
		for (int i = 0; i < getEnemies().size(); i++) {
			final Player enemy = getEnemies().get(i);
			enemy.sendMessage("˜e˜m                    ˜e˜lDuel Information˜r˜e˜m                    ");
			enemy.sendMessage("˜eDate: ˜l" + new Date().toString());
			enemy.sendMessage("˜eGame: ˜l" + getName());
			enemy.sendMessage("˜eOpponent: ˜c˜l" + getEnemy(enemy).getName());

			enemy.setGameMode(GameMode.SURVIVAL);
			enemy.setHealth(enemy.getMaxHealth());
			enemy.setFoodLevel(30);
			enemy.setFireTicks(0);
			for (PotionEffect effect : enemy.getActivePotionEffects()) {
				enemy.removePotionEffect(effect.getType());
			}
			UserFlash.of(enemy).setSuffix("");
			final UserInfo info = UserInfo.of(enemy);
			info.setPosition(spawns.get(i));
			info.getDeathListeners().add(this);
			info.getQuitListeners().add(this);
			UserFlash.of(enemy).setObjective(new DuelObjective(enemy, getEnemy(enemy), "˜b˜lWaiting..."));
		}
		new CountDownTimer().runTaskTimer(Quartz.getInstance(), 0L, 20L);
	}

	@Override
	public void onDeath(Player dead) {
		winner = getEnemy(dead);
		UserInfo.of(dead).setNoAttack(true);
		UserInfo.of(winner).setNoDamage(true);
		for (Player enemy : getEnemies()) {

			UserInfo.of(enemy).setQueue(null);
			enemy.setFireTicks(0);
			enemy.playSound(enemy.getLocation(), Sound.FIREWORK_BLAST, 1f, 1f);
			SendPacket.sendTitle(enemy, "˜6˜lGame END", "˜aWinner: " + winner.getName(), 0, 80, 10);
			if (UserInfo.of(enemy).getPosition() != null) {
				enemy.teleport(UserInfo.of(enemy).getPosition());
			} else {
				server.dispatchCommand(enemy, "location lobby_spawn");
			}
		}
		endTime = System.currentTimeMillis();
		setStatus(Status.PREEND);
		new AfterFinish().runTaskLater(Quartz.getInstance(), 4 * 20L);
	}

	@Override
	public void onQuit(Player user) {
		onDeath(user);
	}

	final class AfterFinish extends BukkitRunnable {

		@Override
		public void run() {
			final List<String> list = Arrays.asList(
					"˜6˜m                    ˜r ˜6˜lGame Result [" + getName() + "˜r˜6] ˜r˜6˜m                    ",
					"˜eTime: " + new SimpleDateFormat("mm:ss").format(new Date(endTime - getStartTime())), "",
					"˜eWinner: ˜a˜l" + winner.getName(), "˜eLoser: ˜c˜l" + getEnemy(winner).getName());
			for (Player enemy : getEnemies()) {
				list.forEach(enemy::sendMessage);
				server.dispatchCommand(enemy, "lobby");
			}
			setStatus(Status.END);
		}
	}

	final class Timer extends BukkitRunnable {

		@Override
		public void run() {
			if (getStatus() != Status.STARTED) {
				cancel();
				return;
			}
			for (Player user : getEnemies()) {
				UserFlash.of(user).setObjective(new DuelObjective(user, getEnemy(user), getStatus().getDisplay()));
				SendPacket.sendActionbar(user, "˜eCPS: " + UserInfo.of(user).getCps());
			}
		}
	}

	final class CountDownTimer extends BukkitRunnable {

		private int count = 3;

		@Override
		public void run() {
			count--;
			if (count < 0) {
				for (Player enemy : getEnemies()) {
					enemy.playSound(enemy.getLocation(), Sound.FIREWORK_BLAST, 1f, 1f);
					enemy.teleport(UserInfo.of(enemy).getPosition());
				}
				new Timer().runTaskTimer(Quartz.getInstance(), 0L, 20L);
				setStartTime(System.currentTimeMillis());
				cancel();
				return;
			}

			for (Player enemy : getEnemies()) {
				enemy.teleport(UserInfo.of(enemy).getPosition());
				enemy.playSound(enemy.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
				SendPacket.sendTitle(enemy, "˜6˜l" + (count + 1), null, 5, 10, 0);
			}
		}
	}

	protected Player getEnemy(Player me) {
		for (Player enemy : getEnemies()) {
			if (!enemy.equals(me))
				return enemy;
		}
		throw new IllegalStateException("Enemy was not found");
	}
}
