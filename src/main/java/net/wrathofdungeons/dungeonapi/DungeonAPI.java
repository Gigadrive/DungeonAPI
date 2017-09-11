package net.wrathofdungeons.dungeonapi;

import net.wrathofdungeons.dungeonapi.cmd.TestCommand;
import net.wrathofdungeons.dungeonapi.cmd.manager.CommandManager;
import net.wrathofdungeons.dungeonapi.listener.PlayerChatListener;
import net.wrathofdungeons.dungeonapi.listener.PlayerCommandListener;
import net.wrathofdungeons.dungeonapi.listener.PlayerJoinListener;
import net.wrathofdungeons.dungeonapi.listener.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.menubuilder.chat.ChatCommandListener;
import org.inventivetalent.menubuilder.inventory.InventoryListener;

public class DungeonAPI extends JavaPlugin {
    private static DungeonAPI instance;
    private static CommandManager commandManager;

    public ChatCommandListener chatCommandListener;
    public InventoryListener inventoryListener;

    public void onEnable(){
        instance = this;
        commandManager = new CommandManager();
        saveDefaultConfig();

        registerCommands();
        registerListeners();
    }

    public void onDisable(){
        MySQLManager.getInstance().unload();
    }

    public static void setCommandManager(CommandManager m){
        commandManager = m;
    }

    public static CommandManager getCommandManager(){
        return commandManager;
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerCommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);

        Bukkit.getPluginManager().registerEvents(inventoryListener = new InventoryListener(this),this);
    }

    private void registerCommands(){
        getCommand("mbchat").setExecutor(chatCommandListener = new ChatCommandListener(this));

        new TestCommand();
    }

    public static void async(Runnable runnable){
        Bukkit.getScheduler().scheduleAsyncDelayedTask(DungeonAPI.getInstance(),runnable);
    }

    public static DungeonAPI getInstance(){
        return instance;
    }
}
