package work.crystalnet.quartz.queue.duel;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import work.crystalnet.quartz.info.RecordMemory;
import work.crystalnet.quartz.info.UserInfo;
import work.crystalnet.quartz.knockback.NormalDuelKnockback;
import work.crystalnet.quartz.serial.WorldCreateSerial.WorldCategory;

public class Sharp2Prot2DuelQueue extends DuelQueue {

	public Sharp2Prot2DuelQueue(Server server, boolean ranked) {
		super(server, "Sharp2Prot2", WorldCategory.DUEL_NORMAL, ranked);
	}

	@Override
	protected void onStart() {
		super.onStart();
		for (Player enemy : getEnemies()) {
			server.dispatchCommand(enemy, "inventory duel_sharp2prot2");
			enemy.setMaximumNoDamageTicks(20);
			UserInfo.of(enemy).setKnockbackReceiver(new NormalDuelKnockback());
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
