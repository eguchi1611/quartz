package work.crystalnet.quartz.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class WorldCommand extends Command {

	public WorldCommand() {
		super("world", "ƒ[ƒ‹ƒh‚ğŠÇ—‚µ‚Ü‚·", "/world [world]", Arrays.asList("w"));
		setPermission("quartz.command.world");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!(sender instanceof Player))
			return false;
		if (args.length < 1) {
			sender.sendMessage("˜b[World List]");
			for (World world : sender.getServer().getWorlds()) {
				sender.sendMessage("˜b|" + world.getName());
			}
		} else {
			final World world = sender.getServer().getWorld(args[0]);
			if (world == null) {
				sender.sendMessage("˜cThe world is not known.");
				return true;
			}
			((Player) sender).teleport(world.getSpawnLocation().add(.5d, .5d, .5d));
			sender.sendMessage("˜aSent you to " + world.getName());
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		switch (args.length) {
		case 1:
			return StringUtil.copyPartialMatches(args[0],
					sender.getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList()),
					new ArrayList<String>());
		}
		return Collections.emptyList();
	}
}
