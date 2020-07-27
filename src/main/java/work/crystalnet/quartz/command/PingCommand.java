package work.crystalnet.quartz.command;

import java.util.Collections;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class PingCommand extends Command {

	public PingCommand() {
		super("ping", "�v���C���[��Ping�𑪒肵�܂�", "/ping [player]", Collections.emptyList());
		setPermission("quartz.command.ping");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player target;
		if (args.length < 1) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				sender.sendMessage("��c�v���C���[���w�肵�Ă�������");
				return true;
			}
		} else {
			target = sender.getServer().getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage("��c�v���C���[��������܂���");
				return true;
			}
		}

		final int ping = ((CraftPlayer) target).getHandle().ping;
		sender.sendMessage("��a" + target.getName() + "'s ping: " + ping + "ms");
		return true;
	}
}
