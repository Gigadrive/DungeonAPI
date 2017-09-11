package net.wrathofdungeons.dungeonapi.listener;

import net.wrathofdungeons.dungeonapi.DungeonAPI;
import net.wrathofdungeons.dungeonapi.cmd.manager.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.Arrays;

public class PlayerChatListener implements Listener {
    @EventHandler
    public void onChat(PlayerChatEvent e){
        Player p = e.getPlayer();
        String msg = e.getMessage();

        if(msg.startsWith("/") && msg.length() > 1){
            String[] s = msg.substring(1,msg.length()).split(" ");

            Command cmd = null;
            for(Command c : DungeonAPI.getCommandManager().getCommands()) if(c.matches(s[0])) cmd = c;

            if(cmd != null){
                cmd.execute(p,s[0], Arrays.asList(s).subList(1, s.length).toArray(new String[]{}));
            } else {
                p.sendMessage("unknown command! " + msg.substring(1,msg.length()));
            }
        }
    }
}
