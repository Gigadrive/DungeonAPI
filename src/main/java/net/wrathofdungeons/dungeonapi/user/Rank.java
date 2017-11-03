package net.wrathofdungeons.dungeonapi.user;

import org.bukkit.ChatColor;

public enum Rank {
    ADMIN(5,"Admin",ChatColor.DARK_RED,ChatColor.DARK_RED + "[" + ChatColor.RED + "Admin" + ChatColor.DARK_RED + "]"),
    GM(4,"Game Master",ChatColor.RED,ChatColor.DARK_RED + "[" + ChatColor.RED + "GM" + ChatColor.DARK_RED + "]"),
    MODERATOR(3,"Moderator",ChatColor.GOLD,ChatColor.GOLD + "[" + ChatColor.YELLOW + "Mod" + ChatColor.GOLD + "]"),
    DONATOR(2,"Donator",ChatColor.DARK_GREEN,ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Donator" + ChatColor.DARK_GREEN + "]"),
    USER(1,"User",ChatColor.WHITE,null);

    private int id;
    private String name;
    private ChatColor color;
    private String chatPrefix;

    Rank(int id, String name, ChatColor color, String chatPrefix){
        this.id = id;
        this.name = name;
        this.color = color;
        this.chatPrefix = chatPrefix;
    }

    public int getID(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public ChatColor getColor(){
        return this.color;
    }

    public String getChatPrefix() {
        return chatPrefix;
    }

    public static Rank fromTag(String t){
        for(Rank r : values()) if(r.toString().equalsIgnoreCase(t)) return r;

        return Rank.USER;
    }

    public static Rank fromID(int i){
        for(Rank r : values()) if(r.getID() == i) return r;

        return Rank.USER;
    }
}
