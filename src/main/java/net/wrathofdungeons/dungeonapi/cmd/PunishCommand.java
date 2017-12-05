package net.wrathofdungeons.dungeonapi.cmd;

import net.wrathofdungeons.dungeonapi.DungeonAPI;
import net.wrathofdungeons.dungeonapi.cmd.manager.Command;
import net.wrathofdungeons.dungeonapi.user.Rank;
import net.wrathofdungeons.dungeonapi.user.User;
import net.wrathofdungeons.dungeonapi.util.BanReason;
import net.wrathofdungeons.dungeonapi.util.ItemUtil;
import net.wrathofdungeons.dungeonapi.util.PlayerUtilities;
import net.wrathofdungeons.dungeonapi.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.inventivetalent.menubuilder.inventory.InventoryMenuBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

public class PunishCommand extends Command {
    public PunishCommand(){
        super(new String[]{"punish","ban","tempban","kick","mute","tempmute"}, Rank.MODERATOR);
    }

    @Override
    public void execute(Player p, String label, String[] args) {
        User u = User.getUser(p);

        if(args.length == 1){
            String name = args[0];
            UUID uuid = PlayerUtilities.getUUIDFromName(name);

            if(uuid != null){
                name = PlayerUtilities.getNameFromUUID(uuid);

                a(p,uuid,name);
            } else {
                p.sendMessage(ChatColor.RED + "Unknown UUID.");
            }
        } else {
            p.sendMessage(ChatColor.RED + "/" + label + " <Player>");
        }
    }

    private void a(Player p, UUID uuid, String name){
        InventoryMenuBuilder inv = new InventoryMenuBuilder(Util.INVENTORY_3ROWS);
        inv.withTitle("Add punishment to player: " + name);

        ItemStack i = new ItemStack(Material.SKULL_ITEM,1,(short)3);
        SkullMeta iM = (SkullMeta)i.getItemMeta();
        iM.setOwner(name);
        iM.setDisplayName(ChatColor.WHITE + name);
        i.setItemMeta(iM);

        inv.withItem(10, ItemUtil.hideFlags(i));

        final String n = name;

        inv.withItem(12,ItemUtil.hideFlags(ItemUtil.namedItem(Material.CLAY_BRICK,ChatColor.GREEN + "General",null)),((player, action, item) -> b(p,uuid,n,BanReason.Category.GENERAL)),ClickType.LEFT);
        inv.withItem(13,ItemUtil.hideFlags(ItemUtil.namedItem(Material.DIAMOND_SWORD,ChatColor.RED + "Hacking",null)),((player, action, item) -> b(p,uuid,n,BanReason.Category.GENERAL)),ClickType.LEFT);
        inv.withItem(14,ItemUtil.hideFlags(ItemUtil.namedItem(Material.BOOK_AND_QUILL,ChatColor.GOLD + "Chat Behavior",null)),((player, action, item) -> b(p,uuid,n,BanReason.Category.GENERAL)),ClickType.LEFT);
        inv.withItem(16,ItemUtil.hideFlags(ItemUtil.namedItem(Material.BARRIER,ChatColor.DARK_RED + "Close",null)),((player, action, item) -> p.closeInventory()),ClickType.LEFT);

        inv.show(p);
    }

    private void b(Player p, UUID uuid, String name, BanReason.Category category){
        b(p,uuid,name,category,1);
    }

    private void b(final Player p, final UUID uuid, final String name, final BanReason.Category category, final int page){
        User u = User.getUser(p);

        ItemStack pl = ItemUtil.namedItem(Material.STAINED_GLASS_PANE, " ", null, 15);
        ItemStack prev = ItemUtil.namedItem(Material.ARROW, ChatColor.GOLD + "<< " + ChatColor.AQUA + "Previous page", null);
        ItemStack next = ItemUtil.namedItem(Material.ARROW, ChatColor.AQUA + "Next page" + ChatColor.GOLD + " >>", null);
        ItemStack close = ItemUtil.namedItem(Material.BARRIER, ChatColor.DARK_RED + "Close", null);

        ArrayList<BanReason> reasons = new ArrayList<BanReason>();
        for(BanReason b : BanReason.STORAGE) if(b.getCategory() == category) reasons.add(b);

        Collections.sort(reasons, (o1, o2) -> o1.getName().compareTo(o2.getName()));

        int sizePerPage = 36;
        int total = reasons.size();

        double d = (((double)total)/((double)sizePerPage));
        int maxPages = ((Double)d).intValue();
        if(maxPages < d) maxPages++;
        if(maxPages <= 0) maxPages = 1;

        InventoryMenuBuilder inv = new InventoryMenuBuilder(Util.MAX_INVENTORY_SIZE);
        inv.withTitle("Select a reason (" + page + "/" + maxPages + ")");

        int slot = 0;
        for(BanReason reason : reasons.stream().skip((page-1) * sizePerPage).limit(sizePerPage).collect(Collectors.toCollection(ArrayList::new))){
            ItemStack i = new ItemStack(reason.getIcon(),1,(short)reason.getIconDurability());
            ItemMeta iM = i.getItemMeta();
            iM.setDisplayName(ChatColor.GREEN + reason.getName());
            ArrayList<String> iL = new ArrayList<String>();

            if(reason.getDescription() != null && !reason.getDescription().isEmpty()) for(String s : Util.getWordWrapLore(reason.getDescription())){
                iL.add(ChatColor.GRAY + s);

                iL.add(" ");
            }

            iL.add(ChatColor.WHITE + "Type of Punishment: " + ChatColor.RED + reason.getTypeOfPunishment().toString());
            if(reason.getTypeOfPunishment() != BanReason.PunishmentType.KICK && reason.getTypeOfPunishment() != BanReason.PunishmentType.PERMMUTE && reason.getTypeOfPunishment() != BanReason.PunishmentType.PERMBAN) iL.add(ChatColor.WHITE + "Duration: " + ChatColor.GOLD + String.valueOf(reason.getTimeValue()) + " " + reason.getTimeUnit().toLowerCase());

            iM.setLore(iL);
            i.setItemMeta(iM);

            inv.withItem(slot,ItemUtil.hideFlags(i),((player, action, item) -> {
                p.closeInventory();
                DungeonAPI.executeBungeeCommand("BungeeConsole","handlepunishment " + p.getName() + " " + name + " " + reason.getId());
            }),ClickType.LEFT);

            slot++;
        }

        inv.withItem(36, pl);
        inv.withItem(37, pl);
        inv.withItem(38, pl);
        inv.withItem(39, pl);
        inv.withItem(40, pl);
        inv.withItem(41, pl);
        inv.withItem(42, pl);
        inv.withItem(43, pl);
        inv.withItem(44, pl);

        if(page != 1) inv.withItem(47,prev, ((player, clickType, itemStack) -> b(p,uuid,name,category,page-1)), ClickType.LEFT);
        inv.withItem(49,close, ((player, clickType, itemStack) -> a(p,uuid,name)), ClickType.LEFT);
        if(maxPages > page) inv.withItem(51,next, ((player, clickType, itemStack) -> b(p,uuid,name,category,page+1)), ClickType.LEFT);

        inv.show(p);
    }
}
