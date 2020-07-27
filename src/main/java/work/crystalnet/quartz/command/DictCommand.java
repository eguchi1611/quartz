package work.crystalnet.quartz.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.StringUtil;

import work.crystalnet.quartz.Quartz;
import work.crystalnet.quartz.serial.CompetentSerial;
import work.crystalnet.quartz.serial.GraphicSerial;
import work.crystalnet.quartz.serial.InventorySerial;
import work.crystalnet.quartz.serial.Serializer;

public class DictCommand extends Command {

	public DictCommand() {
		super("dict", "保存されている情報を編集します",
				"/dict inventory <id>\n/dict graphic <id> <row>\n/dict competent <id> <command-line>\n/dict location <id>",
				Collections.emptyList());
		setPermission("quartz.command.dict");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!(sender instanceof Player))
			return false;
		final Player target = (Player) sender;
		final PlayerInventory workshop = target.getInventory();
		final String line = String.join(" ", args).toLowerCase();
		if (line.startsWith("inventory")) {
			if (args.length < 2)
				return false;
			String key = args[1].toLowerCase();
			InventorySerial serial = new InventorySerial(Arrays.asList(workshop.getContents()), workshop.getHelmet(),
					workshop.getChestplate(), workshop.getLeggings(), workshop.getBoots());
			final Serializer dict = Quartz.getSerializer(Quartz.SERIAL_DICT_INVENTORY);
			dict.set(key, serial);
			dict.save();
			target.sendMessage("§aInventoryを作成/更新しました");
			return true;
		} else if (line.startsWith("graphic")) {
			if (args.length < 3)
				return false;
			String key = args[1].toLowerCase();
			int size = 6;
			try {
				size = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				sender.sendMessage("§c数字を入力してください");
				return true;
			}
			if (size <= 6 && 0 < size) {
				size = size * 9;
			} else {
				sender.sendMessage("§c有効な数字を入力してください");
				return true;
			}
			final Block block = target.getTargetBlock((Set<Material>) null, 0xf);
			if (block.getState() instanceof Chest) {
				final Chest chest = (Chest) block.getState();
				final InventoryHolder holder = chest.getInventory().getHolder();
				if (holder instanceof DoubleChest) {
					final DoubleChest doubleChest = (DoubleChest) holder;
					final List<ItemStack> list = Arrays.asList(doubleChest.getInventory().getContents()).subList(0,
							size);
					GraphicSerial serial = new GraphicSerial(doubleChest.getInventory().getName(), list);
					final Serializer dict = Quartz.getSerializer(Quartz.SERIAL_DICT_GRAPHIC);
					dict.set(key, serial);
					dict.save();
					sender.sendMessage("§aGraphicを作成/更新しました");
					return true;
				}
			}
			sender.sendMessage("§cDoubleChestに視点をあわせてください");
			return true;
		} else if (line.startsWith("competent")) {
			if (args.length < 3)
				return false;
			final ItemStack item = target.getItemInHand();
			if (item.getType() == Material.AIR) {
				sender.sendMessage("§c登録するアイテムを持ってください");
				return true;
			}
			final String key = args[1].toLowerCase();
			final String cmd = args[2].toLowerCase().replace("-", " ");
			final CompetentSerial competentSerial = new CompetentSerial(cmd, item);
			final Serializer serial = Quartz.getSerializer(Quartz.SERIAL_DICT_COMPETENT);
			serial.set(key, competentSerial);
			serial.save();
			sender.sendMessage("§aCompetentを作成/更新しました");
			return true;
		} else if (line.startsWith("location")) {
			if (args.length < 2) {
				final Location location = target.getLocation();
				final String key = args[1].toLowerCase();
				final Serializer serial = Quartz.getSerializer(Quartz.SERIAL_DICT_LOCATION);
				serial.set(key, location);
				serial.save();
				sender.sendMessage("§aLocationを作成/更新しました");
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		switch (args.length) {
		case 1:
			return StringUtil.copyPartialMatches(args[0],
					Arrays.asList("inventory", "graphic", "competent", "location"), new ArrayList<String>());
		}
		return Collections.emptyList();
	}
}
