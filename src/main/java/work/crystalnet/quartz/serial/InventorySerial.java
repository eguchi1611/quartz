package work.crystalnet.quartz.serial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class InventorySerial implements ConfigurationSerializable {

	public static final String KEY_CONTENTS = "contents";
	public static final String KEY_HELMET = "helmet";
	public static final String KEY_CHESTPLATE = "chestplate";
	public static final String KEY_LEGGINGS = "leggings";
	public static final String KEY_BOOTS = "boots";

	public final List<ItemStack> contents;
	public final ItemStack helmet;
	public final ItemStack chestplate;
	public final ItemStack leggings;
	public final ItemStack boots;

	public InventorySerial(List<ItemStack> contents, ItemStack helmet, ItemStack chestplate, ItemStack leggings,
			ItemStack boots) {
		this.contents = contents;
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
	}

	@SuppressWarnings("unchecked")
	public InventorySerial(Map<String, Object> input) {
		this((List<ItemStack>) input.get(KEY_CONTENTS), (ItemStack) input.get(KEY_HELMET),
				(ItemStack) input.get(KEY_CHESTPLATE), (ItemStack) input.get(KEY_LEGGINGS),
				(ItemStack) input.get(KEY_BOOTS));
	}

	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> result = new HashMap<>();
		result.put(KEY_CONTENTS, contents);
		result.put(KEY_HELMET, helmet);
		result.put(KEY_CHESTPLATE, chestplate);
		result.put(KEY_LEGGINGS, leggings);
		result.put(KEY_BOOTS, boots);
		return result;
	}
}
