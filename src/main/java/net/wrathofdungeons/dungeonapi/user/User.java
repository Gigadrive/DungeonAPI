package net.wrathofdungeons.dungeonapi.user;

import com.google.gson.Gson;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.wrathofdungeons.dungeonapi.DungeonAPI;
import net.wrathofdungeons.dungeonapi.MySQLManager;
import net.wrathofdungeons.dungeonapi.event.PlayerCoreDataLoadedEvent;
import net.wrathofdungeons.dungeonapi.util.BountifulAPI;
import net.wrathofdungeons.dungeonapi.util.DefaultFontInfo;
import net.wrathofdungeons.dungeonapi.util.PlayerUtilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class User {
    public static HashMap<Player,User> STORAGE = new HashMap<Player,User>();

    public static User getUser(Player p){
        if(STORAGE.containsKey(p)){
            return STORAGE.get(p);
        } else {
            return null;
        }
    }

    public static boolean isLoaded(Player p){
        return STORAGE.containsKey(p);
    }

    public static void load(Player p){
        if(!isLoaded(p)){
            new User(p);
        }
    }

    private Player p;
    private Rank rank;
    private Scoreboard scoreboard;
    private ArrayList<String> friends;
    private int guildID;
    private int startGuildID;
    private UserSettingsManager settingsManager;

    public User(Player p){
        if(STORAGE.containsKey(p)) return;

        this.p = p;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Gson gson = DungeonAPI.GSON;

        DungeonAPI.async(() -> {
            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `wrathofdungeons`.`users` WHERE `uuid` = ?");
                ps.setString(1,p.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();

                if(rs.first()){
                    this.rank = Rank.fromTag(rs.getString("rank"));
                    this.guildID = rs.getInt("guildID");
                    this.startGuildID = this.guildID;

                    if(rs.getString("settings") == null || rs.getString("settings").isEmpty()){
                        this.settingsManager = new UserSettingsManager();
                    } else {
                        this.settingsManager = gson.fromJson(rs.getString("settings"),UserSettingsManager.class);
                    }

                    p.setScoreboard(scoreboard);

                    reloadFriends();
                } else {
                    PreparedStatement insert = MySQLManager.getInstance().getConnection().prepareStatement("INSERT INTO `wrathofdungeons`.`users` (`uuid`,`username`) VALUES(?,?);");
                    insert.setString(1,p.getUniqueId().toString());
                    insert.setString(2,p.getName());
                    insert.executeUpdate();
                    insert.close();

                    new User(p);
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    public void reloadFriends(){
        try {
            if(friends == null){
                friends = new ArrayList<String>();
            } else {
                friends.clear();
            }

            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `wrathofdungeons`.`friendships` WHERE `player1` = ? OR `player2` = ?");
            ps.setString(1,p.getUniqueId().toString());
            ps.setString(2,p.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                String player1 = rs.getString("player1");
                String player2 = rs.getString("player2");

                if(player1.equalsIgnoreCase(p.getUniqueId().toString())){
                    friends.add(player2);
                } else if(player2.equalsIgnoreCase(p.getUniqueId().toString())){
                    friends.add(player1);
                }
            }

            MySQLManager.getInstance().closeResources(rs,ps);

            PlayerUtilities.clearCaches(p.getUniqueId());

            if(!STORAGE.containsKey(p)){
                STORAGE.put(p,this);

                PlayerCoreDataLoadedEvent event = new PlayerCoreDataLoadedEvent(p);
                Bukkit.getPluginManager().callEvent(event);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendSettingsUpdate(){
        DungeonAPI.executeBungeeCommand("CONSOLE","reloadsettings " + p.getName());
    }

    public Player getPlayer(){
        return this.p;
    }

    public Rank getRank(){
        return this.rank;
    }

    public boolean hasPermission(Rank rank){
        if(rank == null){
            return true;
        } else {
            return this.rank.getID() >= rank.getID();
        }
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public UserSettingsManager getSettingsManager() {
        return settingsManager;
    }

    public void updateName(){
        p.setDisplayName(getRank().getColor() + p.getName());

        String teamName = p.getName();

        for(Player all : Bukkit.getOnlinePlayers()){
            if(User.isLoaded(all)){
                User u = User.getUser(all);

                Team team = u.getScoreboard().getTeam(teamName);

                if(team == null) team = u.getScoreboard().registerNewTeam(teamName);

                team.setPrefix(getRank().getColor().toString());
                team.addEntry(p.getName());
            }
        }
    }

    public int getGuildID() {
        return guildID;
    }

    public void setGuildID(int guildID) {
        this.guildID = guildID;
    }

    public void connect(String server){
        DungeonAPI.sendPlayerToServer(p.getName(),server);
    }

    public void hideEntity(Entity e){
        if(e instanceof Player){
            p.hidePlayer((Player)e);
        } else {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(e.getEntityId());
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void updateTabList(){
        BountifulAPI.sendTabTitle(p,
                ChatColor.GRAY.toString() + ChatColor.BOLD.toString() + "Wrath of " + ChatColor.RED.toString() + ChatColor.BOLD + "D" + ChatColor.GRAY.toString() + ChatColor.BOLD.toString() + "ungeons" + "\n"
                ,

                "\n" +
                        ChatColor.GRAY.toString() + "Website: " + ChatColor.RED.toString() + "https://wrathofdungeons.net"
        );
    }

    public int getPing(){
        return ((CraftPlayer)p).getHandle().ping;
    }

    public void swingArm(){
        for(Player all : Bukkit.getOnlinePlayers()) swingArm(all);
    }

    public void swingArm(Player a){
        EntityPlayer ep = ((CraftPlayer)p).getHandle();
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation(ep,0);
        ((CraftPlayer)a).getHandle().playerConnection.sendPacket(packet);
    }

    public void sendCenteredMessage(String message){
        sendCenteredMessage(message,true);
    }

    public void bukkitReset(){
        p.setGameMode(GameMode.SURVIVAL);

        p.setMaxHealth(20);
        p.setHealth(p.getMaxHealth());

        p.setFireTicks(0);
        p.setFoodLevel(20);

        p.getInventory().clear();
        p.getInventory().setArmorContents(null);

        p.setWalkSpeed(0.2f);
        p.setLevel(0);
        p.setExp(0f);

        p.setAllowFlight(false);
        p.setFlying(p.getAllowFlight());

        for(PotionEffect pe : p.getActivePotionEffects()) p.removePotionEffect(pe.getType());
    }

    public String sendCenteredMessage(String message, boolean send){
        Player player = p;
        int CENTER_PX = 154;
        int MAX_PX = 250;

        message = ChatColor.translateAlternateColorCodes('&', message);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        int charIndex = 0;
        int lastSpaceIndex = 0;
        String toSendAfter = null;
        String recentColorCode = "";
        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
                continue;
            }else if(previousCode == true){
                previousCode = false;
                recentColorCode = "ยง" + c;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else if(c == ' ') lastSpaceIndex = charIndex;
            else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
            if(messagePxSize >= MAX_PX){
                toSendAfter = recentColorCode + message.substring(lastSpaceIndex + 1, message.length());
                message = message.substring(0, lastSpaceIndex + 1);
                break;
            }
            charIndex++;
        }
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        String s = sb.toString() + message;
        if(send) player.sendMessage(s);
        if(toSendAfter != null) sendCenteredMessage(toSendAfter);
        return s;
    }

    public CloudPlayer getCloudPlayer(){
        return CloudAPI.getInstance().getOnlinePlayer(getPlayer().getUniqueId());
    }

    public void saveData(){
        DungeonAPI.async(() -> {
            try {
                Gson gson = DungeonAPI.GSON;
                PreparedStatement ps = null;

                if(this.startGuildID != this.guildID){
                    ps = MySQLManager.getInstance().getConnection().prepareStatement("UPDATE `wrathofdungeons`.`users` SET `username` = ?, `guildID` = ?, `settings` = ? WHERE `uuid` = ?");
                    ps.setString(1,p.getName());
                    ps.setInt(2,guildID);
                    ps.setString(3,gson.toJson(settingsManager));
                    ps.setString(4,p.getUniqueId().toString());
                } else {
                    ps = MySQLManager.getInstance().getConnection().prepareStatement("UPDATE `wrathofdungeons`.`users` SET `username` = ?, `settings` = ? WHERE `uuid` = ?");
                    ps.setString(1,p.getName());
                    ps.setString(2,gson.toJson(settingsManager));
                    ps.setString(3,p.getUniqueId().toString());
                }

                ps.executeUpdate();
                ps.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        });
    }
}
