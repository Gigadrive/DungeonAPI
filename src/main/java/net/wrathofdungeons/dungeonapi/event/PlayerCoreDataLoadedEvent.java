package net.wrathofdungeons.dungeonapi.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCoreDataLoadedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player p;

    public PlayerCoreDataLoadedEvent(Player p){
        this.p = p;
    }

    public Player getPlayer(){
        return this.p;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
