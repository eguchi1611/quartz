package work.crystalnet.quartz.serial;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class WorldCreateSerial implements ConfigurationSerializable {

	public static final String KET_SEED = "seed";
	public static final String KEY_ENVIRONMENT = "environment";
	public static final String KEY_TYPE = "type";
	public static final String KEY_GENERATE_STRUCTURES = "generate_structures";
	public static final String KEY_GENERATOR_SETTINGS = "generator_settings";
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_SPAWNS = "spawns";

	public final long seed;
	public final World.Environment environment;
	public final WorldType type;
	public final boolean generateStructures;
	public final String generatorSettings;
	public final WorldCategory category;
	public final List<Location> spawns;

	public WorldCreateSerial(long seed, Environment environment, WorldType type, boolean generateStructures,
			String generatorSettings, WorldCategory category, List<Location> spawns) {
		this.seed = seed;
		this.environment = environment;
		this.type = type;
		this.generateStructures = generateStructures;
		this.generatorSettings = generatorSettings;
		this.category = category;
		this.spawns = spawns;
	}

	@SuppressWarnings("unchecked")
	public WorldCreateSerial(Map<String, Object> input) {
		this((long) input.get(KET_SEED), World.Environment.valueOf((String) input.get(KEY_ENVIRONMENT)),
				WorldType.valueOf((String) input.get(KEY_TYPE)), (boolean) input.get(KEY_GENERATE_STRUCTURES),
				(String) input.get(KEY_GENERATOR_SETTINGS), WorldCategory.valueOf((String) input.get(KEY_CATEGORY)),
				(List<Location>) input.get(KEY_SPAWNS));
	}

	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> result = new LinkedHashMap<>();
		result.put(KET_SEED, seed);
		result.put(KEY_ENVIRONMENT, environment.name());
		result.put(KEY_TYPE, type.name());
		result.put(KEY_GENERATE_STRUCTURES, generateStructures);
		result.put(KEY_GENERATOR_SETTINGS, generatorSettings);
		result.put(KEY_CATEGORY, category.name());
		result.put(KEY_SPAWNS, spawns);
		return result;
	}

	public enum WorldCategory {

		NONE, DUEL_NORMAL, SUMO;
	}
}
