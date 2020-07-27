package work.crystalnet.quartz.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.StringUtil;

import work.crystalnet.quartz.Quartz;
import work.crystalnet.quartz.serial.InventorySerial;

public class InventoryCommand extends Command {

	public InventoryCommand() {
		super("inventory", "インベントリーを適用します", "/inventory <id>", Collections.emptyList());
		setPermission("quartz.command.inventory");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length < 1)
			return false;
		final InventorySerial inventorySerial = (InventorySerial) Quartz.getSerializer(Quartz.SERIAL_DICT_INVENTORY)
				.get(args[0].toLowerCase());
		if (inventorySerial == null) {
			sender.sendMessage("§cInventory was not found");
			return true;
		}
		final PlayerInventory inventory = ((Player) sender).getInventory();
		inventory.setHelmet(inventorySerial.helmet);
		inventory.setChestplate(inventorySerial.chestplate);
		inventory.setLeggings(inventorySerial.leggings);
		inventory.setBoots(inventorySerial.boots);
		inventory.setContents(inventorySerial.contents.toArray(new ItemStack[inventorySerial.contents.size()]));
		((Player) sender).updateInventory();
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		switch (args.length) {
		case 1:
			return StringUtil.copyPartialMatches(args[0],
					Quartz.getSerializer(Quartz.SERIAL_DICT_INVENTORY).getKeys(false), new ArrayList<String>());
		}
		return Collections.emptyList();
	}
}
