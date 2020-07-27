package work.crystalnet.quartz.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.StringUtil;

import work.crystalnet.quartz.Quartz;
import work.crystalnet.quartz.VariableSupplier;
import work.crystalnet.quartz.info.UserInfo;
import work.crystalnet.quartz.serial.GraphicSerial;
import work.crystalnet.quartz.serial.Serializer;

public class GraphicCommand extends Command {

	public GraphicCommand() {
		super("graphic", "“o˜^‚³‚ê‚½ƒCƒ“ƒxƒ“ƒgƒŠ‚ð•\Ž¦‚µ‚Ü‚·", "/graphic <id>", Collections.emptyList());
		setPermission("quartz.command.graphic");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		final Player target = (Player) sender;
		final Server server = target.getServer();
		if (args.length < 1)
			return false;
		final String key = args[0].toLowerCase();
		final Serializer serial = Quartz.getSerializer(Quartz.SERIAL_DICT_GRAPHIC);
		final GraphicSerial graphic = (GraphicSerial) serial.get(key);
		if (graphic == null) {
			sender.sendMessage("˜c“o˜^‚³‚ê‚Ä‚¢‚È‚¢ID‚ªŽw’è‚³‚ê‚Ü‚µ‚½");
			return true;
		}
		final Inventory inventory = server.createInventory(null, graphic.contents.size(), graphic.title);
		inventory.setContents(graphic.contents.toArray(new ItemStack[graphic.contents.size()]));
		for (ItemStack item : inventory.getContents()) {
			if (item == null) {
				continue;
			}
			final ItemMeta meta = item.getItemMeta();
			if (meta.getLore() == null) {
				continue;
			}
			final List<String> current = meta.getLore();
			final List<String> lore = new ArrayList<>();
			for (String line : current) {
				if (line.contains("${") && line.contains("}")) {
					final String part = line.substring(line.indexOf("${") + 2, line.indexOf("}"));
					lore.add(line.replace("${" + part + "}", VariableSupplier.variable(part)));
					continue;
				}
				lore.add(line);
			}
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		target.openInventory(inventory);
		UserInfo.of(target).setGraphic(true);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		switch (args.length) {
		case 1:
			final Serializer serial = Quartz.getSerializer(Quartz.SERIAL_DICT_GRAPHIC);
			return StringUtil.copyPartialMatches(args[0], serial.getKeys(false), new ArrayList<String>());
		}
		return Collections.emptyList();
	}
}
