package net.wrathofdungeons.dungeonapi.util;

import net.wrathofdungeons.dungeonapi.MySQLManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

public class PlayerUtilities {
    private static HashMap<String,UUID> NAME_UUID_CACHE = new HashMap<String,UUID>();
    private static HashMap<UUID,String> UUID_NAME_CACHE = new HashMap<UUID,String>();

    public static void clearCaches(){
        NAME_UUID_CACHE.clear();
        UUID_NAME_CACHE.clear();
    }

    public static String getNameFromUUID(UUID uuid){
        if(UUID_NAME_CACHE.containsKey(uuid)){
            return UUID_NAME_CACHE.get(uuid);
        } else {
            String name = null;

            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `users` WHERE `uuid` = ?");
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
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `users` WHERE `username` = ?");
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
}
