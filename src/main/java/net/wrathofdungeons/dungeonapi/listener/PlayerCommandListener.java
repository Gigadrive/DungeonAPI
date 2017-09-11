package net.wrathofdungeons.dungeonapi.listener;

import net.wrathofdungeons.dungeonapi.DungeonAPI;
import net.wrathofdungeons.dungeonapi.cmd.manager.Command;
import net.wrathofdungeons.dungeonapi.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerCommandListener implements Listener {
    @EventHandler
    public void onPreproccess(PlayerCommandPreprocessEvent e){
        Player p = e.getPlayer();
        String msg = e.getMessage();

        if(User.isLoaded(p)){
            User u = User.getUser(p);

            if(msg.startsWith("/") && msg.length() > 1){
                String[] s = msg.substring(1,msg.length()).split(" ");
                String label = s[0];

                ArrayList<String> commandWhitelist = new ArrayList<String>();
                commandWhitelist.add("mbchat");

                if(commandWhitelist.contains(label)) return;
                e.setCancelled(true);

                Command cmd = null;
                for(Command c : DungeonAPI.getCommandManager().getCommands()) if(c.matches(label)) cmd = c;

                if(cmd != null && u.hasPermission(cmd.getMinRank())){
                    cmd.execute(p,s[0], Arrays.asList(s).subList(1, s.length).toArray(new String[]{}));
                } else {
                    p.sendMessage(ChatColor.RED + "Unknown command.");
                }
            }
        }
    }
}
