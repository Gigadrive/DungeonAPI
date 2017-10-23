package net.wrathofdungeons.dungeonapi.listener;

import net.wrathofdungeons.dungeonapi.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class PluginMessageListener implements Listener, org.bukkit.plugin.messaging.PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(channel.equals("BungeeCord")){
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

            try {
                if(in.readUTF().equals("reloadFriends")){
                    Player p = Bukkit.getPlayer(in.readUTF());

                    if(p != null && User.isLoaded(p)){
                        User u = User.getUser(p);

                        u.reloadFriends();
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
