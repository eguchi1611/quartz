package work.crystalnet.quartz.info;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;

import work.crystalnet.quartz.serial.WorldCreateSerial.WorldCategory;

public class WorldInfo {

	private static Set<WorldInfo> their = new HashSet<>();
	// Target World
	private final World here;
	// Contents
	private WorldCategory category;
	private List<Location> spawns;
	private String using;

	public static void reset() {
		their.clear();
		their = new HashSet<>();
	}

	public static WorldInfo of(World here) {
		for (WorldInfo info : their) {
			if (info.here.equals(here))
				return info;
		}
		final WorldInfo info = new WorldInfo(here);
		their.add(info);
		return info;
	}

	public void delete() {
		their.remove(this);
	}

	private WorldInfo(World here) {
		this.here = here;
	}

	public WorldCategory getCategory() {
		return category;
	}

	public void setCategory(WorldCategory category) {
		this.category = category;
	}

	public List<Location> getSpawns() {
		return spawns;
	}

	public void setSpawns(List<Location> spawns) {
		this.spawns = spawns;
	}

	public String getUsing() {
		return using;
	}

	public void setUsing(String using) {
		this.using = using;
	}
}
