package work.crystalnet.quartz.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import work.crystalnet.quartz.info.UserInfo;
import work.crystalnet.quartz.listener.DeathListener;
import work.crystalnet.quartz.listener.QuitListener;

public class ListenerEvent implements Listener {

	@EventHandler
	public void onPlayerDeath(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		final Player user = (Player) event.getEntity();
		// If user will be dead
		if (user.getHealth() - event.getFinalDamage() < 0 || event.getCause() == EntityDamageEvent.DamageCause.VOID) {
			event.setDamage(0);
			user.setHealth(user.getMaxHealth());
			final UserInfo info = UserInfo.of(user);
			if (info.getDeathListeners().size() < 1) {
				event.setCancelled(true);
				user.getServer().dispatchCommand(user, "lobby");
				return;
			}
			for (DeathListener listen : info.getDeathListeners()) {
				listen.onDeath(user);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		final Player user = event.getPlayer();
		final UserInfo info = UserInfo.of(user);
		for (QuitListener listen : info.getQuitListeners()) {
			listen.onQuit(user);
		}
	}

	@EventHandler
	public void onKnockback(PlayerVelocityEvent event) {
		final Player user = event.getPlayer();
		if (!(user.getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;
		final EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) user.getLastDamageCause();
		if (damageEvent.getCause() != DamageCause.ENTITY_ATTACK || !(damageEvent.getDamager() instanceof Player))
			return;
		final UserInfo info = UserInfo.of(user);
		if (info.getKnockbackReceiver() != null) {
			final Vector result = info.getKnockbackReceiver().onKnockback(user, (Player) damageEvent.getDamager());
			if (result != null) {
				event.setVelocity(result);
				return;
			}
		}
		event.setVelocity(new Vector());
	}
}
