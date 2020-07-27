package work.crystalnet.quartz;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.extern.java.Log;
import work.crystalnet.quartz.VariableSupplier.Supplier;
import work.crystalnet.quartz.command.CompetentCommand;
import work.crystalnet.quartz.command.DictCommand;
import work.crystalnet.quartz.command.GraphicCommand;
import work.crystalnet.quartz.command.InventoryCommand;
import work.crystalnet.quartz.command.LobbyCommand;
import work.crystalnet.quartz.command.LocationCommand;
import work.crystalnet.quartz.command.PingCommand;
import work.crystalnet.quartz.command.QueueCommand;
import work.crystalnet.quartz.command.WorldCommand;
import work.crystalnet.quartz.event.CompetentEvent;
import work.crystalnet.quartz.event.EventListeners;
import work.crystalnet.quartz.event.JoinEvent;
import work.crystalnet.quartz.event.ListenerEvent;
import work.crystalnet.quartz.event.SQLEvent;
import work.crystalnet.quartz.info.RecordMemory;
import work.crystalnet.quartz.info.UserFlash;
import work.crystalnet.quartz.info.UserInfo;
import work.crystalnet.quartz.info.WorldInfo;
import work.crystalnet.quartz.queue.Queue;
import work.crystalnet.quartz.queue.Queue.Status;
import work.crystalnet.quartz.queue.duel.DuelQueue;
import work.crystalnet.quartz.record.DataRecord;
import work.crystalnet.quartz.serial.CompetentSerial;
import work.crystalnet.quartz.serial.GraphicSerial;
import work.crystalnet.quartz.serial.InventorySerial;
import work.crystalnet.quartz.serial.Serializer;
import work.crystalnet.quartz.serial.WorldCreateSerial;

@Log
public class Quartz extends JavaPlugin {

	private interface CommandReceiver {

		void onInvoke(Player sender, String[] args);
	}

	public static final UUID SERIAL_WORLD_SETTINGS = UUID.randomUUID();
	public static final UUID SERIAL_DICT_INVENTORY = UUID.randomUUID();
	public static final UUID SERIAL_DICT_GRAPHIC = UUID.randomUUID();
	public static final UUID SERIAL_DICT_COMPETENT = UUID.randomUUID();
	public static final UUID SERIAL_DICT_LOCATION = UUID.randomUUID();

	public static Quartz getInstance() {
		return JavaPlugin.getPlugin(Quartz.class);
	}

	public static Serializer getSerializer(UUID uuid) {
		final Serializer serial = getInstance().registedSerials.get(uuid);
		if (serial == null) {
			log.warning("An unregistered serial was obtained");
		}
		return serial;
	}

	private Map<UUID, Serializer> registedSerials;
	private File dataFolder;
	private Map<String, CommandReceiver> commands;

	@Override
	public void onDisable() {
		for (Player user : getServer().getOnlinePlayers()) {
			if (!getServer().getServicesManager().load(DataRecord.class).write(user.getUniqueId(),
					RecordMemory.of(user), user.getAddress()))
				throw new IllegalStateException("データベースに同期できませんでした, " + user.getName());
		}
	}

	@Override
	public void onEnable() {
		// Initial Serialization
		ConfigurationSerialization.registerClass(WorldCreateSerial.class);
		ConfigurationSerialization.registerClass(InventorySerial.class);
		ConfigurationSerialization.registerClass(GraphicSerial.class);
		ConfigurationSerialization.registerClass(CompetentSerial.class);

		// Initial System Settings
		saveDefaultConfig();
		dataFolder = new File(getConfig().getString("folder"));

		// Initial Events
		final PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new JoinEvent(), this);
		pluginManager.registerEvents(new CompetentEvent(), this);
		pluginManager.registerEvents(new SQLEvent(), this);
		pluginManager.registerEvents(new ListenerEvent(), this);
		pluginManager.registerEvents(new EventListeners(), this);

		// Initial Services
		final ServicesManager serviceManager = getServer().getServicesManager();
		serviceManager.register(DataRecord.class, new DataRecord(getConfig().getString("sqlite")), this,
				ServicePriority.Normal);

		// Initial Commands
		commands = new HashMap<>();
		commands.put("closeinventory", (sender, args) -> {
			sender.closeInventory();
		});
		commands.put("buhc_head", (sender, args) -> {
			sender.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10, 1, true, true), true);
			sender.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 1, true, true), true);
		});

		final CommandMap commandMap = ((CraftServer) getServer()).getCommandMap();
		commandMap.register("lobby", new LobbyCommand());
		commandMap.register("graphic", new GraphicCommand());
		commandMap.register("competent", new CompetentCommand());
		commandMap.register("location", new LocationCommand());
		commandMap.register("ping", new PingCommand());
		commandMap.register("world", new WorldCommand());
		commandMap.register("dict", new DictCommand());
		commandMap.register("inventory", new InventoryCommand());
		commandMap.register("queue", new QueueCommand());

		for (Map.Entry<String, CommandReceiver> entry : commands.entrySet()) {
			final String name = entry.getKey();
			final Command command = new Command(name) {

				@Override
				public boolean execute(CommandSender sender, String commandLabel, String[] args) {
					entry.getValue().onInvoke((Player) sender, args);
					return true;
				}
			};
			command.setDescription("Quartz, Tips command.");
			command.setUsage("null");
			commandMap.register(name, command);
		}

		// Initial Configuration A
		registerSerial(SERIAL_WORLD_SETTINGS, new Serializer(new File(dataFolder, "world_settings.yml")));

		// Initial Worlds
		for (Map.Entry<String, Object> entry : getSerializer(SERIAL_WORLD_SETTINGS).getValues(false).entrySet()) {
			final WorldCreateSerial serial = (WorldCreateSerial) entry.getValue();
			final World world = getServer().createWorld(
					new WorldCreator(entry.getKey()).seed(serial.seed).environment(serial.environment).type(serial.type)
							.generateStructures(serial.generateStructures).generatorSettings(serial.generatorSettings));
			final WorldInfo info = WorldInfo.of(world);
			info.setCategory(serial.category);
			info.setSpawns(serial.spawns);
		}

		// Initial Configuration B
		registerSerial(SERIAL_DICT_INVENTORY, new Serializer(new File(dataFolder, "dict_inventory.yml")));
		registerSerial(SERIAL_DICT_GRAPHIC, new Serializer(new File(dataFolder, "dict_graphic.yml")));
		registerSerial(SERIAL_DICT_COMPETENT, new Serializer(new File(dataFolder, "dict_competent.yml")));
		registerSerial(SERIAL_DICT_LOCATION, new Serializer(new File(dataFolder, "dict_location.yml")));

		// Register Supplier
		VariableSupplier.getSuppliers().add(new Supplier() {

			@Override
			public String request(String key) {
				if (key.equals("in_duel")) {
					int result = 0;
					for (Queue queue : Queue.getAll()) {
						if (queue instanceof DuelQueue && !((DuelQueue) queue).isRanked()
								&& queue.getStatus() == Status.STARTED) {
							result += queue.getEnemies().size();
						}
					}
					return "§7Total Playing: §l" + result;
				}
				if (key.equals("in_duel_ranked")) {
					int result = 0;
					for (Queue queue : Queue.getAll()) {
						if (queue instanceof DuelQueue && ((DuelQueue) queue).isRanked()
								&& queue.getStatus() == Status.STARTED) {
							result += queue.getEnemies().size();
						}
					}
					return "§7Total Playing: §l" + result;
				}
				if (key.startsWith("q_")) {
					int result = 0;
					for (Queue queue : Queue.getAll()) {
						if (queue.getStatus() == Status.STANDBY) {
							if (key.equalsIgnoreCase("q_" + queue.original())) {
								result += queue.getEnemies().size();
							}
						}
					}
					return "§7Joined: §l" + result;
				}
				if (key.startsWith("in_")) {
					int result = 0;
					for (Queue queue : Queue.getAll()) {
						if (queue.getStatus() == Status.STARTED) {
							if (key.equalsIgnoreCase("in_" + queue.original())) {
								result += queue.getEnemies().size();
							}
						}
					}
					return "§7Playing: §l" + result;
				}
				return null;
			}
		});

		// Reset players
		for (Player user : getServer().getOnlinePlayers()) {
//			getServer().dispatchCommand(user, "lobby");
			UserInfo.of(user).delete();
			UserFlash.of(user).delete();
		}
	}

	@Override
	public void onLoad() {
		// Initial Variable
		registedSerials = new HashMap<>();
		Queue.reset();
		UserFlash.reset();
		UserInfo.reset();
		WorldInfo.reset();
		log.info("Initialized, all information has been reset.");
	}

	private void registerSerial(UUID uuid, Serializer serial) {
		if (registedSerials.containsKey(uuid))
			throw new IllegalStateException();
		registedSerials.put(uuid, serial);
		serial.reload();
		log.info("Initialized serial, " + serial);
	}
}
