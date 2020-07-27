package work.crystalnet.quartz.event;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import work.crystalnet.quartz.Quartz;
import work.crystalnet.quartz.info.UserInfo;
import work.crystalnet.quartz.serial.CompetentSerial;
import work.crystalnet.quartz.serial.Serializer;

public class CompetentEvent implements Listener {

	@EventHandler
	public void onItemClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK
				|| !event.hasItem())
			return;
		if (invoke(event.getPlayer(), event.getItem())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		final Player target = (Player) event.getWhoClicked();
		if (!UserInfo.of(target).isGraphic() || event.getCurrentItem() == null)
			return;
		event.setCancelled(true);
		invoke(target, event.getCurrentItem());
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		if (invoke(event.getPlayer(), event.getItem())) {
			event.getItem().setAmount(event.getItem().getAmount() - 1);
			event.setCancelled(true);
		}
	}

	private boolean invoke(CommandSender sender, ItemStack item) {
		final Server server = sender.getServer();
		final Serializer serial = Quartz.getSerializer(Quartz.SERIAL_DICT_COMPETENT);
		boolean result = false;
		for (String key : serial.getKeys(false)) {
			final CompetentSerial competentSerial = (CompetentSerial) serial.get(key);
			if (similar(item, competentSerial.item)) {
				server.dispatchCommand(sender, competentSerial.command);
				result = true;
			}
		}
		return result;
	}

	private boolean similar(ItemStack item1, ItemStack item2) {
		boolean material = item1.getType() == item2.getType();
		boolean displayName = item1.getItemMeta() != null && item2.getItemMeta() != null
				&& item1.getItemMeta().getDisplayName() != null
				&& item1.getItemMeta().getDisplayName().equals(item2.getItemMeta().getDisplayName());
		return material && displayName;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		final Player target = (Player) event.getPlayer();
		UserInfo.of(target).setGraphic(false);
	}
}
