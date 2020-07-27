package work.crystalnet.quartz.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import work.crystalnet.quartz.Quartz;
import work.crystalnet.quartz.serial.CompetentSerial;
import work.crystalnet.quartz.serial.Serializer;

public class CompetentCommand extends Command {

	public CompetentCommand() {
		super("competent", "“o˜^‚³‚ê‚½ƒAƒCƒeƒ€‚ð—^‚¦‚Ü‚·", "/competent <id>", Collections.emptyList());
		setPermission("quartz.command.competent");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length < 1)
			return false;
		final Player target = (Player) sender;
		final Serializer serial = Quartz.getSerializer(Quartz.SERIAL_DICT_COMPETENT);
		final String key = args[0].toLowerCase();
		if (!serial.contains(key)) {
			target.sendMessage("˜c‘¶Ý‚µ‚È‚¢’l‚ªŽw’è‚³‚ê‚Ü‚µ‚½");
			return true;
		}
		target.getInventory().addItem(((CompetentSerial) serial.get(key)).item);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		switch (args.length) {
		case 1:
			final Serializer serial = Quartz.getSerializer(Quartz.SERIAL_DICT_COMPETENT);
			return StringUtil.copyPartialMatches(args[0], serial.getKeys(false), new ArrayList<String>());
		}
		return Collections.emptyList();
	}
}
