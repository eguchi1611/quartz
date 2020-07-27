package work.crystalnet.quartz.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import work.crystalnet.quartz.Quartz;
import work.crystalnet.quartz.serial.Serializer;

public class LocationCommand extends Command {

	public LocationCommand() {
		super("location", "指定された場所にテレポートします", "/location <id>", Collections.emptyList());
		setPermission("quartz.command.location");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		final Player target = (Player) sender;
		if (args.length < 1)
			return false;
		final String key = args[0].toLowerCase();
		final Serializer serial = Quartz.getSerializer(Quartz.SERIAL_DICT_LOCATION);
		final Location location = (Location) serial.get(key);
		if (location == null) {
			sender.sendMessage("§c登録されていないIDが指定されました");
			return true;
		}
		target.teleport(location);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		switch (args.length) {
		case 1:
			final Serializer serial = Quartz.getSerializer(Quartz.SERIAL_DICT_LOCATION);
			return StringUtil.copyPartialMatches(args[0], serial.getKeys(false), new ArrayList<String>());
		}
		return Collections.emptyList();
	}
}
