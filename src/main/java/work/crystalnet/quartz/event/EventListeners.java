package work.crystalnet.quartz.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import work.crystalnet.quartz.info.UserInfo;

public class EventListeners implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		event.setFormat("Åòf[%sÅòf]: %s");
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		final UserInfo info = UserInfo.of((Player) event.getEntity());
		if (!info.isNoHunger()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		final Player damaged = (Player) event.getEntity();
		final UserInfo info = UserInfo.of(damaged);
		if (info.isNoAttack()) {
			event.setCancelled(true);
		}
		if (info.isNoDamage()) {
			event.setDamage(0);
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		final UserInfo info = UserInfo.of(event.getPlayer());
		info.setClick(info.getClick() + 1);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		UserInfo.of(event.getPlayer()).getTask().cancel();
	}
}
