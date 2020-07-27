package work.crystalnet.quartz.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import work.crystalnet.quartz.info.UserFlash;
import work.crystalnet.quartz.info.UserInfo;
import work.crystalnet.quartz.queue.Queue;
import work.crystalnet.quartz.queue.Queue.Status;

public class QueueCommand extends Command {

	public QueueCommand() {
		super("queue", "キューに参加します", "/queue <id>|leave", Arrays.asList("q"));
		setPermission("quartz.command.queue");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length < 1)
			return false;

		final Player target = (Player) sender;
		final UserInfo info = UserInfo.of(target);
		if (info.getQueue() != null) {
			if (args[0].equalsIgnoreCase("leave")) {
				if (info.getQueue().getStatus() != Status.STANDBY) {
					target.sendMessage("§c試合中にQueueから抜けようとするとは。BANしますよ？");
					return true;
				}
				info.getQueue().leave(target);
				UserFlash.of(target).setSuffix("");
				target.sendMessage("§cQueueから抜けました");
				target.getServer().dispatchCommand(target, "inventory lobby_initial");
				return true;
			}
			target.sendMessage("§c既にQueueに参加しています");
			return true;
		}

		final Queue queue = Queue.permit(target, args[0].toLowerCase());
		if (queue == null) {
			target.sendMessage("§cキューに参加できませんでした");
			return true;
		}

		if (queue.getStatus() == Status.STANDBY) {
			target.sendMessage("§aキューに参加しました, " + queue.getDisplayID());
			target.getServer().dispatchCommand(target, "inventory joined_queue");
			UserInfo.of(target).setQueue(queue);
			String suffix = "§c@" + queue.getName();
			if (suffix.length() > 16) {
				suffix = suffix.substring(0, 16);
			}
			UserFlash.of(target).setSuffix(suffix);
		}
		target.closeInventory();
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		switch (args.length) {
		case 1:
			final List<String> list = new ArrayList<>();
			list.addAll(Arrays.asList("leave", "sharp2prot2_ranked", "sharp2prot2", "builduhc_ranked", "builduhc",
					"gapple_ranked", "gapple", "nodelay_ranked", "nodelay", "sumo_ranked", "sumo"));
			for (Queue queue : Queue.getAll()) {
				if (queue.getStatus() == Status.STANDBY) {
					list.add(queue.getId());
				}
			}
			return StringUtil.copyPartialMatches(args[0], list, new ArrayList<String>());
		}
		return Collections.emptyList();
	}
}
