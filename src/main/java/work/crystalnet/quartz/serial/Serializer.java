package work.crystalnet.quartz.serial;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.google.common.io.Files;

public class Serializer implements Configuration {

	private final File file;

	private FileConfiguration config;

	public Serializer(File file) {
		this.file = file;
		try {
			Files.createParentDirs(file);
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		config = YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public String toString() {
		return "Serial [file=" + file + "]";
	}

	@Override
	public void addDefault(String path, Object value) {
		config.addDefault(path, value);
	}

	@Override
	public Set<String> getKeys(boolean deep) {
		return config.getKeys(deep);
	}

	@Override
	public void addDefaults(Map<String, Object> defaults) {
		config.addDefaults(defaults);
	}

	@Override
	public Map<String, Object> getValues(boolean deep) {
		return config.getValues(deep);
	}

	@Override
	public void addDefaults(Configuration defaults) {
		config.addDefaults(defaults);
	}

	@Override
	public boolean contains(String path) {
		return config.contains(path);
	}

	@Override
	public void setDefaults(Configuration defaults) {
		config.setDefaults(defaults);
	}

	@Override
	public boolean isSet(String path) {
		return config.isSet(path);
	}

	@Override
	public Configuration getDefaults() {
		return config.getDefaults();
	}

	@Override
	public String getCurrentPath() {
		return config.getCurrentPath();
	}

	@Override
	public ConfigurationOptions options() {
		return config.options();
	}

	@Override
	public String getName() {
		return config.getName();
	}

	@Override
	public Configuration getRoot() {
		return config.getRoot();
	}

	@Override
	public ConfigurationSection getParent() {
		return config.getParent();
	}

	@Override
	public Object get(String path) {
		return config.get(path);
	}

	@Override
	public Object get(String path, Object def) {
		return config.get(path, def);
	}

	@Override
	public void set(String path, Object value) {
		config.set(path, value);
	}

	@Override
	public ConfigurationSection createSection(String path) {
		return config.createSection(path);
	}

	@Override
	public ConfigurationSection createSection(String path, Map<?, ?> map) {
		return config.createSection(path, map);
	}

	@Override
	public String getString(String path) {
		return config.getString(path);
	}

	@Override
	public String getString(String path, String def) {
		return config.getString(path, def);
	}

	@Override
	public boolean isString(String path) {
		return config.isString(path);
	}

	@Override
	public int getInt(String path) {
		return config.getInt(path);
	}

	@Override
	public int getInt(String path, int def) {
		return config.getInt(path, def);
	}

	@Override
	public boolean isInt(String path) {
		return config.isInt(path);
	}

	@Override
	public boolean getBoolean(String path) {
		return config.getBoolean(path);
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		return config.getBoolean(path, def);
	}

	@Override
	public boolean isBoolean(String path) {
		return config.isBoolean(path);
	}

	@Override
	public double getDouble(String path) {
		return config.getDouble(path);
	}

	@Override
	public double getDouble(String path, double def) {
		return config.getDouble(path, def);
	}

	@Override
	public boolean isDouble(String path) {
		return config.isDouble(path);
	}

	@Override
	public long getLong(String path) {
		return config.getLong(path);
	}

	@Override
	public long getLong(String path, long def) {
		return config.getLong(path, def);
	}

	@Override
	public boolean isLong(String path) {
		return config.isLong(path);
	}

	@Override
	public List<?> getList(String path) {
		return config.getList(path);
	}

	@Override
	public List<?> getList(String path, List<?> def) {
		return config.getList(path, def);
	}

	@Override
	public boolean isList(String path) {
		return config.isList(path);
	}

	@Override
	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}

	@Override
	public List<Integer> getIntegerList(String path) {
		return config.getIntegerList(path);
	}

	@Override
	public List<Boolean> getBooleanList(String path) {
		return config.getBooleanList(path);
	}

	@Override
	public List<Double> getDoubleList(String path) {
		return config.getDoubleList(path);
	}

	@Override
	public List<Float> getFloatList(String path) {
		return config.getFloatList(path);
	}

	@Override
	public List<Long> getLongList(String path) {
		return config.getLongList(path);
	}

	@Override
	public List<Byte> getByteList(String path) {
		return config.getByteList(path);
	}

	@Override
	public List<Character> getCharacterList(String path) {
		return config.getCharacterList(path);
	}

	@Override
	public List<Short> getShortList(String path) {
		return config.getShortList(path);
	}

	@Override
	public List<Map<?, ?>> getMapList(String path) {
		return config.getMapList(path);
	}

	@Override
	public Vector getVector(String path) {
		return config.getVector(path);
	}

	@Override
	public Vector getVector(String path, Vector def) {
		return config.getVector(path, def);
	}

	@Override
	public boolean isVector(String path) {
		return config.isVector(path);
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String path) {
		return config.getOfflinePlayer(path);
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def) {
		return config.getOfflinePlayer(path, def);
	}

	@Override
	public boolean isOfflinePlayer(String path) {
		return config.isOfflinePlayer(path);
	}

	@Override
	public ItemStack getItemStack(String path) {
		return config.getItemStack(path);
	}

	@Override
	public ItemStack getItemStack(String path, ItemStack def) {
		return config.getItemStack(path, def);
	}

	@Override
	public boolean isItemStack(String path) {
		return config.isItemStack(path);
	}

	@Override
	public Color getColor(String path) {
		return config.getColor(path);
	}

	@Override
	public Color getColor(String path, Color def) {
		return config.getColor(path, def);
	}

	@Override
	public boolean isColor(String path) {
		return config.isColor(path);
	}

	@Override
	public ConfigurationSection getConfigurationSection(String path) {
		return config.getConfigurationSection(path);
	}

	@Override
	public boolean isConfigurationSection(String path) {
		return config.isConfigurationSection(path);
	}

	@Override
	public ConfigurationSection getDefaultSection() {
		return config.getDefaultSection();
	}
}
