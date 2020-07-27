package work.crystalnet.quartz.event;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		final Player target = event.getPlayer();
		final Server server = target.getServer();
		target.setScoreboard(server.getScoreboardManager().getNewScoreboard());
		server.dispatchCommand(target, "l");
	}
}
