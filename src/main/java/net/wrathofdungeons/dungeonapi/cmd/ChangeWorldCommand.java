package net.wrathofdungeons.dungeonapi.cmd;

import net.wrathofdungeons.dungeonapi.cmd.manager.Command;
import net.wrathofdungeons.dungeonapi.user.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ChangeWorldCommand extends Command {
    public ChangeWorldCommand(){
        super("changeworld", Rank.MODERATOR);
    }

    @Override
    public void execute(Player p, String label, String[] args) {
        if(args.length == 1){
            World world = Bukkit.getWorld(args[0]);

            if(world != null){
                if(p.getWorld() != world){
                    p.sendMessage(ChatColor.GREEN + "Teleporting to " + ChatColor.YELLOW + world.getName() + ChatColor.GREEN + "!");
                    p.teleport(world.getSpawnLocation());
                } else {
                    p.sendMessage(ChatColor.RED + "You are already in that world.");
                }
            } else {
                p.sendMessage(ChatColor.RED + "Invalid world! These worlds are available:");
                for(World w : Bukkit.getWorlds()) p.sendMessage(ChatColor.RED + "-" + ChatColor.YELLOW + " " + w.getName());
            }
        } else {
            p.sendMessage(ChatColor.RED + "/" + label + " <World>");
        }
    }
}
