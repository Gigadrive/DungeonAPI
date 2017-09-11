package net.wrathofdungeons.dungeonapi.user;

import net.wrathofdungeons.dungeonapi.DungeonAPI;
import net.wrathofdungeons.dungeonapi.MySQLManager;
import net.wrathofdungeons.dungeonapi.event.PlayerCoreDataLoadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

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

    public User(Player p){
        this.p = p;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        DungeonAPI.async(() -> {
            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `users` WHERE `uuid` = ?");
                ps.setString(1,p.getUniqueId().toString());
                ResultSet rs = ps.executeQuery();

                if(rs.first()){
                    this.rank = Rank.fromTag(rs.getString("rank"));

                    p.setScoreboard(scoreboard);

                    STORAGE.put(p,this);

                    PlayerCoreDataLoadedEvent event = new PlayerCoreDataLoadedEvent(p);
                    Bukkit.getPluginManager().callEvent(event);
                } else {
                    PreparedStatement insert = MySQLManager.getInstance().getConnection().prepareStatement("INSERT INTO `users` (`uuid`,`username`) VALUES(?,?);");
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

    public void updateName(){
        p.setDisplayName(getRank().getColor() + p.getName());

        String teamName = p.getName();

        for(Player all : Bukkit.getOnlinePlayers()){
            if(User.isLoaded(all)){
                User u = User.getUser(all);

                Team team = u.getScoreboard().getTeam(teamName);

                if(team == null) u.getScoreboard().registerNewTeam(teamName);

                team.setDisplayName(p.getName());
                team.setPrefix(getRank().getColor().toString());
                team.addEntry(p.getName());
            }
        }
    }

    public void saveData(){
        DungeonAPI.async(() -> {
            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("UPDATE `users` SET `username` = ? WHERE `uuid` = ?");
                ps.setString(1,p.getName());
                ps.setString(2,p.getUniqueId().toString());
                ps.executeUpdate();
                ps.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        });
    }
}
