package work.crystalnet.quartz.command;

import java.util.Arrays;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import work.crystalnet.quartz.display.LobbyObjective;
import work.crystalnet.quartz.info.RecordMemory;
import work.crystalnet.quartz.info.UserFlash;
import work.crystalnet.quartz.info.UserInfo;
import work.crystalnet.quartz.record.DataRecord;

public class LobbyCommand extends Command {

	public LobbyCommand() {
		super("lobby", "Send you to lobby.", "/lobby", Arrays.asList("l"));
		setPermission("quartz.command.lobby");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		final Player user = (Player) sender;
		final Server server = sender.getServer();
		if(UserInfo.of(user).getQueue() != null) {
			user.sendMessage("§cキュー入っている状態ではロビーに戻れません");
			return true;
		}
		server.dispatchCommand(sender, "location lobby_spawn");
		server.dispatchCommand(sender, "inventory lobby_initial");
		if (!server.getServicesManager().load(DataRecord.class).write(user.getUniqueId(), RecordMemory.of(user),
				user.getAddress()))
			throw new IllegalStateException("データベースに同期できませんでした, " + user.getName());
		UserInfo.of(user).delete();
		UserFlash.of(user).delete();
		final UserFlash flash = UserFlash.of(user);
		flash.setPrefix("");
		flash.setSuffix("");
		flash.setFooter("§cEnjoy §e§lConflict §cand §a§lMinigames");
		flash.setHeader("§bWelcome to §lCrystal Network");
		flash.setObjective(new LobbyObjective(user));
		user.setFireTicks(0);
		user.setFoodLevel(40);
		user.setHealth(20);
		user.setMaxHealth(20);
		for (PotionEffect effect : user.getActivePotionEffects()) {
			user.removePotionEffect(effect.getType());
		}
		server.getOnlinePlayers().forEach(me -> UserFlash.of(me).sendTeamStylePacket());
		return true;
	}
}
