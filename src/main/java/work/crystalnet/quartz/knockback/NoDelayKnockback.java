package work.crystalnet.quartz.knockback;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import work.crystalnet.quartz.Quartz;

public class NoDelayKnockback implements KnockbackReceiver {

	private boolean down;

	@Override
	public Vector onKnockback(Player user, Player attacker) {
		if (user.getLocation().getY() - attacker.getLocation().getY() > 2.75d) {
			down = true;
			user.getServer().getScheduler().runTaskLater(Quartz.getInstance(), () -> {
				down = false;
			}, 15L);
		}
		Vector result = new Vector(user.getLocation().getX() - attacker.getLocation().getX(), 0,
				user.getLocation().getZ() - attacker.getLocation().getZ()).normalize().multiply(.4d);
		if (down)
			return result.setY(-.125d);
		return result.setY(.18d);
	}
}
