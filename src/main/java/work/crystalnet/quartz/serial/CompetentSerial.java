package work.crystalnet.quartz.serial;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class CompetentSerial implements ConfigurationSerializable {

	public static final String KEY_COMMAND = "command";
	public static final String KET_ITEM = "item";

	public final String command;
	public final ItemStack item;

	public CompetentSerial(String command, ItemStack item) {
		this.command = command;
		this.item = item;
	}

	public CompetentSerial(Map<String, Object> input) {
		this((String) input.get(KEY_COMMAND), (ItemStack) input.get(KET_ITEM));
	}

	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> result = new HashMap<>();
		result.put(KEY_COMMAND, command);
		result.put(KET_ITEM, item);
		return result;
	}
}
