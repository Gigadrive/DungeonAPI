package net.wrathofdungeons.dungeonapi.util;

import net.wrathofdungeons.dungeonapi.MySQLManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerUtilities {
    private static HashMap<String,UUID> NAME_UUID_CACHE = new HashMap<String,UUID>();
    private static HashMap<UUID,String> UUID_NAME_CACHE = new HashMap<UUID,String>();
    private static HashMap<UUID,ArrayList<String>> UUID_FRIENDREQUESTS_CACHE = new HashMap<UUID,ArrayList<String>>();
    private static HashMap<UUID,ArrayList<String>> UUID_OUTGOINGFRIENDREQUESTS_CACHE = new HashMap<UUID,ArrayList<String>>();

    public static void clearCaches(){
        NAME_UUID_CACHE.clear();
        UUID_NAME_CACHE.clear();
        UUID_FRIENDREQUESTS_CACHE.clear();
        UUID_OUTGOINGFRIENDREQUESTS_CACHE.clear();
    }

    public static void clearCaches(UUID uuid){
        String toRemove = null;

        for(String name : NAME_UUID_CACHE.keySet())
            if(NAME_UUID_CACHE.get(name).toString().equals(uuid.toString())) toRemove = name;

        if(toRemove != null) NAME_UUID_CACHE.remove(toRemove);

        UUID_NAME_CACHE.remove(uuid);
        UUID_FRIENDREQUESTS_CACHE.remove(uuid);
        UUID_OUTGOINGFRIENDREQUESTS_CACHE.remove(uuid);
    }

    public static String getNameFromUUID(UUID uuid){
        if(UUID_NAME_CACHE.containsKey(uuid)){
            return UUID_NAME_CACHE.get(uuid);
        } else {
            String name = null;

            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `wrathofdungeons`.`users` WHERE `uuid` = ?");
                ps.setString(1,uuid.toString());
                ResultSet rs = ps.executeQuery();

                if(rs.first()){
                    name = rs.getString("username");
                    UUID_NAME_CACHE.put(uuid,name);
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }

            return name;
        }
    }

    public static UUID getUUIDFromName(String name){
        if(NAME_UUID_CACHE.containsKey(name)){
            return NAME_UUID_CACHE.get(name);
        } else {
            UUID uuid = null;

            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `wrathofdungeons`.`users` WHERE `username` = ?");
                ps.setString(1,name);
                ResultSet rs = ps.executeQuery();

                if(rs.first()){
                    uuid = UUID.fromString(rs.getString("uuid"));
                    NAME_UUID_CACHE.put(name,uuid);
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }

            return uuid;
        }
    }

    public static ArrayList<String> getFriendRequestsToUUID(UUID uuid){
        if(uuid == null) return null;

        if(UUID_FRIENDREQUESTS_CACHE.containsKey(uuid)){
            return UUID_FRIENDREQUESTS_CACHE.get(uuid);
        } else {
            ArrayList<String> friendRequests = new ArrayList<String>();

            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `wrathofdungeons`.`friend_requests` WHERE `to` = ?");
                ps.setString(1,uuid.toString());

                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    friendRequests.add(rs.getString("from"));
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }

            UUID_FRIENDREQUESTS_CACHE.put(uuid,friendRequests);

            return friendRequests;
        }
    }

    public static ArrayList<String> getFriendRequestsFromUUID(UUID uuid){
        if(uuid == null) return null;

        if(UUID_OUTGOINGFRIENDREQUESTS_CACHE.containsKey(uuid)){
            return UUID_OUTGOINGFRIENDREQUESTS_CACHE.get(uuid);
        } else {
            ArrayList<String> friendRequests = new ArrayList<String>();

            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `wrathofdungeons`.`friend_requests` WHERE `from` = ?");
                ps.setString(1,uuid.toString());

                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    friendRequests.add(rs.getString("to"));
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }

            UUID_OUTGOINGFRIENDREQUESTS_CACHE.put(uuid,friendRequests);

            return friendRequests;
        }
    }
}
