package net.wrathofdungeons.dungeonapi.inv;

import org.inventivetalent.menubuilder.inventory.InventoryMenuBuilder;

import java.util.ArrayList;
import java.util.Collections;

public class ScrollableInventory {
    private ArrayList<ScrollableInventoryItem> items;

    public ScrollableInventory(){
        items = new ArrayList<ScrollableInventoryItem>();
    }

    public int getMaxWidth(){
        return -1;
    }

    public int getMaxHeight(){
        return -1;
    }

    public int getWidthOffset(){
        return 0;
    }

    public int getHeightOffset(){
        return 0;
    }

    public ArrayList<ScrollableInventoryItem> getItems() {
        return items;
    }

    public ArrayList<ScrollableInventoryItem> getItemsOrdered() {
        ArrayList<ScrollableInventoryItem> items = new ArrayList<ScrollableInventoryItem>();
        items.addAll(this.items);

        Collections.sort(items, ((o1, o2) -> ((Integer)o1.getSlot()).compareTo(o2.getSlot())));

        return items;
    }

    public ScrollableInventoryItem getItem(int slot){
        for(ScrollableInventoryItem item : items) if(item.getSlot() == slot) return item;

        return null;
    }

    public void setItems(ArrayList<ScrollableInventoryItem> items) {
        this.items = items;
    }

    public InventoryMenuBuilder toMenu(){
        return toMenu(0);
    }

    public InventoryMenuBuilder toMenu(int startRow){
        return null;
    }
}
