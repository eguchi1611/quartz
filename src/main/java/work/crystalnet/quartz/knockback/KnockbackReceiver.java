package work.crystalnet.quartz.knockback;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface KnockbackReceiver {

	Vector onKnockback(Player user, Player attacker);
}
