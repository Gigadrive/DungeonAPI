package net.wrathofdungeons.dungeonapi.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent e){
        if(e.getBlockPlaced().getType().getId() == 97){
            e.setCancelled(true);
        }
    }
}
