package net.wrathofdungeons.dungeonapi;

import org.bukkit.plugin.java.JavaPlugin;

public class DungeonAPI extends JavaPlugin {
    private static DungeonAPI instance;

    public void onEnable(){
        instance = this;
    }

    public static DungeonAPI getInstance(){
        return instance;
    }
}
