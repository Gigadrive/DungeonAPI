package net.wrathofdungeons.dungeonapi;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import de.dytanic.cloudnet.api.CloudAPI;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.wrathofdungeons.dungeonapi.cmd.ChangeWorldCommand;
import net.wrathofdungeons.dungeonapi.cmd.TestCommand;
import net.wrathofdungeons.dungeonapi.cmd.manager.CommandManager;
import net.wrathofdungeons.dungeonapi.listener.*;
import net.wrathofdungeons.dungeonapi.user.User;
import net.wrathofdungeons.dungeonapi.util.BanReason;
import net.wrathofdungeons.dungeonapi.util.BarUtil;
import net.wrathofdungeons.dungeonapi.util.PlayerUtilities;
import net.wrathofdungeons.dungeonapi.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.menubuilder.chat.ChatCommandListener;
import org.inventivetalent.menubuilder.inventory.InventoryListener;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DungeonAPI extends JavaPlugin {
    private static DungeonAPI instance;
    private static CommandManager commandManager;

    public ChatCommandListener chatCommandListener;
    public InventoryListener inventoryListener;

    public static final Gson GSON = new Gson();

    public void onEnable(){
        instance = this;
        commandManager = new CommandManager();
        saveDefaultConfig();

        registerCommands();
        registerListeners();

        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "TheChest");

        new Thread(new Runnable() {

            @Override
            public void run() {
                while(true) {
                    for(String s : BarUtil.getPlayers()) {
                        Player o = Bukkit.getPlayer(s);
                        if(o != null) BarUtil.teleportBar(o);
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();

        new BukkitRunnable(){
            @Override
            public void run() {
                PlayerUtilities.clearCaches();
                BanReason.init();
            }
        }.runTaskTimerAsynchronously(this,1,5*60*20);
    }

    public static String getServerName(){
        return CloudAPI.getInstance().getServerId();
    }

    public void onDisable(){
        for(Player p : Bukkit.getOnlinePlayers()){
            if(User.isLoaded(p)) User.getUser(p).saveData();
        }

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

        PluginMessageListener l = new PluginMessageListener();
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", l);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "TheChest", l);

        Bukkit.getPluginManager().registerEvents(inventoryListener = new InventoryListener(this),this);
    }

    private void registerCommands(){
        getCommand("mbchat").setExecutor(chatCommandListener = new ChatCommandListener(this));

        //new TestCommand();
        new ChangeWorldCommand();
    }

    public static void async(Runnable runnable){
        Bukkit.getScheduler().scheduleAsyncDelayedTask(DungeonAPI.getInstance(),runnable);
    }

    public static void sync(Runnable runnable){
        Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonAPI.getInstance(),runnable);
    }

    public static ArrayList<Location> getBlocksInRadius(Location loc, int radius){
        ArrayList<Location> a = new ArrayList<Location>();

        for (int x = -(radius); x <= radius; x ++){
            for (int y = -(radius); y <= radius; y ++){
                for (int z = -(radius); z <= radius; z ++){
                    if(loc.getWorld().getBlockAt(loc) != null){
                        Block b = loc.getWorld().getBlockAt(loc).getRelative(x,y,z);
                        if(b != null && b.getType() != null) a.add(b.getLocation());
                    }
                }
            }
        }

        return a;
    }

    public static void executeBungeeCommand(String executor, String command){
        if(command.startsWith("/")) command = command.substring(1, command.length());

        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

        if(player != null){
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("globalcommand");
            out.writeUTF(executor);
            out.writeUTF(command);

            player.sendPluginMessage(DungeonAPI.getInstance(), "BungeeCord", out.toByteArray());
        }
    }

    public static void sendPlayerToServer(String playerName, String server){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

        if(p != null){
            try {
                out.writeUTF("ConnectOther");
                out.writeUTF(playerName);
                out.writeUTF(server);
            } catch (IOException e) {
                e.printStackTrace();
            }

            p.sendPluginMessage(DungeonAPI.getInstance(), "BungeeCord", stream.toByteArray());
        }
    }

    public static String executeShellCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    public static void nmsRemoveAI(Entity bukkitEntity) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) bukkitEntity)
                .getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        nmsEntity.c(tag);
        tag.setInt("NoAI", 1);
        nmsEntity.f(tag);
    }

    public static void nmsMakeSilent(Entity bukkitEntity) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) bukkitEntity)
                .getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        nmsEntity.c(tag);
        tag.setInt("Silent", 1);
        nmsEntity.f(tag);
    }

    public static double calculateKD(int kills, int deaths){
        if(deaths <= 0){
            return kills;
        } else if(kills <= 0) {
            return 0;
        } else {
            return Util.round(kills/deaths,2);
        }
    }

    public static double calculateWL(int wins, int loses){
        return calculateKD(wins,loses);
    }

    public static Location getBlockCenter(Location loc){
        if(loc == null){
            return null;
        } else {
            return new Location(loc.getWorld(),loc.getBlockX(),loc.getY(),loc.getBlockZ(),loc.getYaw(),loc.getPitch()).add(0.5,0,0.5);
        }
    }

    public static void stopServer(){
        Bukkit.shutdown();
    }

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2){
        List<Block> blocks = new ArrayList<Block>();
        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        for (int x = bottomBlockX; x <= topBlockX; x++)
        {
            for (int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for (int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static List<Block> blocksFromTwoPointsMaximized(Location loc1, Location loc2){
        loc1.setY(0);
        loc2.setY(loc2.getWorld().getMaxHeight());

        return blocksFromTwoPoints(loc1,loc2);
    }

    public static boolean isInRectangle(Location loc, Location loc1, Location loc2){
        double[] dim = new double[2];

        dim[0] = loc1.getX();
        dim[1] = loc2.getX();
        Arrays.sort(dim);
        if(loc.getX() > dim[1] || loc.getX() < dim[0])
            return false;

        dim[0] = loc1.getY();
        dim[1] = loc2.getY();
        Arrays.sort(dim);
        if(loc.getY() > dim[1] || loc.getY() < dim[0])
            return false;

        dim[0] = loc1.getZ();
        dim[1] = loc2.getZ();
        Arrays.sort(dim);
        if(loc.getZ() > dim[1] || loc.getZ() < dim[0])
            return false;

        return true;
    }

    public static boolean isInRectangleMaximized(Location loc, Location loc1, Location loc2){
        loc1.setY(0);
        loc2.setY(loc2.getWorld().getMaxHeight());

        return isInRectangle(loc,loc1,loc2);
    }

    public static DungeonAPI getInstance(){
        return instance;
    }
}
