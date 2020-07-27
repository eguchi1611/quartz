package work.crystalnet.quartz.serial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class GraphicSerial implements ConfigurationSerializable {

	public static final String KEY_TITLE = "title";
	public static final String KEY_CONTENTS = "contents";

	public final String title;
	public final List<ItemStack> contents;

	public GraphicSerial(String title, List<ItemStack> contents) {
		this.title = title;
		this.contents = contents;
	}

	@SuppressWarnings("unchecked")
	public GraphicSerial(Map<String, Object> input) {
		this((String) input.get(KEY_TITLE), (List<ItemStack>) input.get(KEY_CONTENTS));
	}

	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> result = new HashMap<>();
		result.put(KEY_TITLE, title);
		result.put(KEY_CONTENTS, contents);
		return result;
	}
}
