package net.wrathofdungeons.dungeonapi.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;

public class LightningStrikeListener implements Listener {
    @EventHandler
    public void onStrike(LightningStrikeEvent e){
        if(!e.isCancelled()){
            if(!e.getLightning().isEffect()){
                e.setCancelled(true);
            }
        }
    }
}
