package net.wrathofdungeons.dungeonapi.inv;

import org.inventivetalent.menubuilder.inventory.InventoryMenuBuilder;

public class VerticalScrollableInventory extends ScrollableInventory {
    public int getMaxWidth(){
        return 7;
    }

    @Override
    public int getWidthOffset() {
        return 1;
    }

    public InventoryMenuBuilder toMenu(int startRow){
        return null;
    }
}
