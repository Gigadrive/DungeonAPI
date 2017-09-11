package net.wrathofdungeons.dungeonapi.listener;

import net.wrathofdungeons.dungeonapi.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();

        if(User.isLoaded(p)){
            User.getUser(p).saveData();
            User.STORAGE.remove(p);
        }
    }
}
