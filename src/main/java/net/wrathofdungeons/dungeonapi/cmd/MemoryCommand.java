package net.wrathofdungeons.dungeonapi.cmd;

import net.wrathofdungeons.dungeonapi.cmd.manager.Command;
import net.wrathofdungeons.dungeonapi.user.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MemoryCommand extends Command {
    public MemoryCommand(){
        super("memory", Rank.ADMIN);
    }

    @Override
    public void execute(Player p, String label, String[] args) {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;

        p.sendMessage(ChatColor.GREEN + "Total Memory: " + ChatColor.YELLOW + (total / 1048576L) + " MB");
        p.sendMessage(ChatColor.GREEN + "Used Memory: " + ChatColor.YELLOW + (used / 1048576L) + " MB");
        p.sendMessage(ChatColor.GREEN + "Free Memory: " + ChatColor.YELLOW + (free / 1048576L) + " MB");
    }
}
