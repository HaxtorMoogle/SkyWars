package vc.pvp.skywars.config;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import vc.pvp.skywars.SkyWars;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.World;
//import org.bukkit.util.Vector;
import org.bukkit.util.Vector;
import vc.pvp.skywars.utilities.LogUtils;

public class PluginConfig {

    private static FileConfiguration storage;
    private static Location lobbySpawn;
    private static List<String> whitelistedCommands = Lists.newArrayList();

    static {
        storage = SkyWars.get().getConfig();
        World w = Bukkit.getWorld(storage.getString("lobby.world"));
        double x = storage.getDouble("lobby.x");
        double y = storage.getDouble("lobby.y");
        double z = storage.getDouble("lobby.z");
        float yaw = (float) storage.getDouble("lobby.yaw");
        float pitch = (float) storage.getDouble("lobby.pitch");
        lobbySpawn = new Location(w, x, y, z, yaw, pitch);
        if (storage.contains("whitelisted-commands")) {
            whitelistedCommands = storage.getStringList("whitelisted-commands");
        }
    }

    public static Location getLobbySpawn() {
        return lobbySpawn;
    }

    public static int getLobbyRadius() {
        return storage.getInt("lobby.radius", 2);
    }

    public static void setLobbySpawn(Location location) {
        storage = SkyWars.get().getConfig();
        lobbySpawn = location.clone();
        storage.set("lobby.world", lobbySpawn.getWorld().getName());
        storage.set("lobby.x", lobbySpawn.getX());
        storage.set("lobby.y", lobbySpawn.getY());
        storage.set("lobby.z", lobbySpawn.getZ());
        storage.set("lobby.yaw", lobbySpawn.getYaw());
        storage.set("lobby.pitch", lobbySpawn.getPitch());
        SkyWars.get().saveConfig();
    }

    public static void migrateConfig() {
        Double version = storage.getDouble("config-version", 0.0);
        if (version < 1.0) {
            LogUtils.log(Level.INFO, "Upgrading configuration file from from version " + version + " to 1.0.");
            storage.set("config-version", 1.0);
            String oldSpawn = storage.getString("lobby.spawn", null);
            if (oldSpawn != null) {
                String[] spawn = oldSpawn.split(" ");
                storage.set("lobby.x", Double.parseDouble(spawn[0]));
                storage.set("lobby.y", Double.parseDouble(spawn[1]));
                storage.set("lobby.z", Double.parseDouble(spawn[2]));
                if (spawn.length >= 5) {
                    storage.set("lobby.yaw", Double.parseDouble(spawn[3]));
                    storage.set("lobby.pitch", Double.parseDouble(spawn[4]));
                }
                storage.getConfigurationSection("lobby").set("spawn", null);
            } else {
                storage.set("lobby.x", 0);
                storage.set("lobby.y", 64);
                storage.set("lobby.z", 0);
                storage.set("lobby.yaw", 0.0);
                storage.set("lobby.pitch", 0.0);
            }
            storage.set("lobby.radius", 2);
            storage.set("fill-populated-chests", false);
        }
        SkyWars.get().saveConfig();
    }

    public static void setPortal(String name, String world, Vector pos1, Vector pos2) {
        storage = SkyWars.get().getConfig();
        storage.set("portals." + name + ".world", world);
        storage.set("portals." + name + ".x1", (Math.round(pos1.getX())));
        storage.set("portals." + name + ".y1", (Math.round(pos1.getY())));
        storage.set("portals." + name + ".z1", (Math.round(pos1.getZ())));
        storage.set("portals." + name + ".x2", (Math.round(pos2.getX())));
        storage.set("portals." + name + ".y2", (Math.round(pos2.getY())));
        storage.set("portals." + name + ".z2", (Math.round(pos2.getZ())));
        SkyWars.get().saveConfig();
    }

    public static Location[] getPortal(String name) {
        storage = SkyWars.get().getConfig();
        String path = "portals." + name + ".";
        String worldName = storage.getString(path + "world", null);
        if (worldName == null) {
            return null;
        }
        double x1 = storage.getDouble(path + "x1");
        double y1 = storage.getDouble(path + "y1");
        double z1 = storage.getDouble(path + "z1");
        double x2 = storage.getDouble(path + "x2");
        double y2 = storage.getDouble(path + "y2");
        double z2 = storage.getDouble(path + "z2");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        Location loc1 = new Location(world, Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
        Location loc2 = new Location(world, Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
        Location[] loc = {loc1, loc2};
        return loc;
    }

    public static boolean removePortal(String name) {
        storage = SkyWars.get().getConfig();
        String path = "portals." + name;
        String check = storage.getString(path);
        if (check == null) {
            return false;
        }
        storage.getConfigurationSection("portals").set(name, null);
        SkyWars.get().saveConfig();
        return true;
    }

    public static Set<String> listPortals() {
        storage = SkyWars.get().getConfig();
        Set<String> portals = storage.getConfigurationSection("portals").getKeys(false);
        if (portals == null) {
            return null;
        }
        return portals;
    }

    public static boolean isCommandWhitelisted(String command) {
        return whitelistedCommands.contains(command.replace("/", ""));
    }

    public static int getIslandsPerWorld() {
        return storage.getInt("islands-per-row", 100);
    }

    public static int getIslandSize() {
        return storage.getInt("island-size", 100);
    }

    public static int getScorePerKill(Player player) {
        String group = SkyWars.getPermission().getPrimaryGroup(player);

        if (storage.contains("score.groups." + group + ".per-kill")) {
            return storage.getInt("score.groups." + group + ".per-kill");
        }

        return storage.getInt("score.per-kill", 3);
    }

    public static int getScorePerWin(Player player) {
        String group = SkyWars.getPermission().getPrimaryGroup(player);

        if (storage.contains("score.groups." + group + ".per-win")) {
            return storage.getInt("score.groups." + group + ".per-win");
        }

        return storage.getInt("score.per-win", 10);
    }

    public static int getScorePerDeath(Player player) {
        String group = SkyWars.getPermission().getPrimaryGroup(player);

        if (storage.contains("score.groups." + group + ".per-death")) {
            return storage.getInt("score.groups." + group + ".per-death");
        }

        return storage.getInt("score.per-death", -1);
    }

    public static int getScorePerLeave(Player player) {
        String group = SkyWars.getPermission().getPrimaryGroup(player);

        if (storage.contains("score.groups." + group + ".per-leave")) {
            return storage.getInt("score.groups." + group + ".per-leave");
        }

        return storage.getInt("score.per-leave", -1);
    }

    public static long getStatisticsUpdateInterval() {
        return storage.getInt("statistics.update-interval", 600) * 20L;
    }

    public static int getStatisticsTop() {
        return storage.getInt("statistics.top", 30);
    }

    public static boolean buildSchematic() {
        return storage.getBoolean("island-building.enabled", false);
    }

    public static int blocksPerTick() {
        return storage.getInt("island-building.blocks-per-tick", 20);
    }

    public static long buildInterval() {
        return storage.getLong("island-building.interval", 1);
    }

    public static boolean buildCages() {
        return storage.getBoolean("build-cages", true);
    }

    public static boolean ignoreAir() {
        return storage.getBoolean("ignore-air", false);
    }

    public static boolean fillChests() {
        return storage.getBoolean("fill-chests", true);
    }

    public static boolean fillPopulatedChests() {
        return storage.getBoolean("fill-populated-chests", true);
    }

    public static boolean useEconomy() {
        return storage.getBoolean("use-economy", false);
    }

    public static boolean chatHandledByOtherPlugin() {
        return storage.getBoolean("chat-handled-by-other-plugin", false);
    }

    public static boolean clearInventory() {
        return storage.getBoolean("clear-inventory-on-join", true);
    }

    public static boolean saveInventory() {
        return storage.getBoolean("save-inventory", false);
    }
}
