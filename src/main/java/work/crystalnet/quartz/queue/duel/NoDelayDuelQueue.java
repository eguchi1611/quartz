package work.crystalnet.quartz.queue.duel;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import work.crystalnet.quartz.info.RecordMemory;
import work.crystalnet.quartz.info.UserInfo;
import work.crystalnet.quartz.knockback.NoDelayKnockback;
import work.crystalnet.quartz.serial.WorldCreateSerial.WorldCategory;

public class NoDelayDuelQueue extends DuelQueue {

	public NoDelayDuelQueue(Server server, boolean ranked) {
		super(server, "NoDelay", WorldCategory.DUEL_NORMAL, ranked);
	}

	@Override
	protected void onStart() {
		super.onStart();
		for (Player enemy : getEnemies()) {
			server.dispatchCommand(enemy, "inventory duel_nodelay");
			enemy.setMaximumNoDamageTicks(0);
			UserInfo.of(enemy).setKnockbackReceiver(new NoDelayKnockback());
		}
	}

	@Override
	public void onDeath(Player dead) {
		super.onDeath(dead);
		final int winnerCoin = (int) (2000 / (System.currentTimeMillis() - getStartTime()) * 1000) + 100;
		final Player winner = getEnemy(dead);
		{
			final RecordMemory record = RecordMemory.of(winner);
			record.setCoin(record.getCoin() + winnerCoin);
			record.setPoint(record.getPoint() + 2);
			if (ranked) {
				record.setToken(record.getToken() + 50);
			}
		}
		{
			final RecordMemory record = RecordMemory.of(dead);
			record.setPoint(record.getPoint() + 1);
		}
	}
}
