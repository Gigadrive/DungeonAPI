package net.wrathofdungeons.dungeonapi.inv;

import org.bukkit.inventory.ItemStack;
import org.inventivetalent.menubuilder.inventory.ItemListener;

public class ScrollableInventoryItem {
    private int slot;
    private ItemStack itemStack;
    private ItemListener itemListener;

    public ScrollableInventoryItem(int slot, ItemStack itemStack){
        this.slot = slot;
        this.itemStack = itemStack;
    }

    public ScrollableInventoryItem(int slot, ItemStack itemStack, ItemListener listener){
        this.slot = slot;
        this.itemStack = itemStack;
        this.itemListener = listener;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ItemListener getItemListener() {
        return itemListener;
    }

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
