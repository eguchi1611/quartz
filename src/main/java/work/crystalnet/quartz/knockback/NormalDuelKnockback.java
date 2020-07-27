package work.crystalnet.quartz.knockback;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class NormalDuelKnockback implements KnockbackReceiver {

	@Override
	public Vector onKnockback(Player user, Player attacker) {
		Vector result = new Vector(user.getLocation().getX() - attacker.getLocation().getX(), 0,
				user.getLocation().getZ() - attacker.getLocation().getZ()).multiply(0.8d);
		result.normalize().setY(0.4d);
		return result;
	}
}
