package net.wrathofdungeons.dungeonapi;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DungeonAPI extends JavaPlugin {
    private static DungeonAPI instance;

    public void onEnable(){
        instance = this;
        saveDefaultConfig();
    }

    public void onDisable(){
        MySQLManager.getInstance().unload();
    }

    public static void async(Runnable runnable){
        Bukkit.getScheduler().scheduleAsyncDelayedTask(DungeonAPI.getInstance(),runnable);
    }

    public static DungeonAPI getInstance(){
        return instance;
    }
}
