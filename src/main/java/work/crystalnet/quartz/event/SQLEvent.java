package work.crystalnet.quartz.event;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import work.crystalnet.quartz.info.RecordMemory;
import work.crystalnet.quartz.record.DataRecord;

public class SQLEvent implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		final Player user = event.getPlayer();
		final Server server = user.getServer();
		if (!server.getServicesManager().load(DataRecord.class).write(user.getUniqueId(), RecordMemory.of(user),
				user.getAddress()))
			throw new IllegalStateException("データベースに同期できませんでした, " + user.getName());
	}
}
