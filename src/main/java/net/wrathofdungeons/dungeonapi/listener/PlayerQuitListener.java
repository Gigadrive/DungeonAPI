package net.wrathofdungeons.dungeonapi.listener;

import net.wrathofdungeons.dungeonapi.DungeonAPI;
import net.wrathofdungeons.dungeonapi.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();

        new BukkitRunnable(){
            @Override
            public void run() {
                if(User.isLoaded(p)){
                    User.getUser(p).saveData();
                    User.STORAGE.remove(p);
                }
            }
        }.runTaskLaterAsynchronously(DungeonAPI.getInstance(),20);
    }
}
