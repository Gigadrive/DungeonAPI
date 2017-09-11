package net.wrathofdungeons.dungeonapi.cmd.manager;

import jdk.nashorn.internal.objects.annotations.Constructor;
import net.wrathofdungeons.dungeonapi.DungeonAPI;
import net.wrathofdungeons.dungeonapi.user.Rank;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Command {
    private String[] names;
    private Rank minRank;

    public Command(String name){
        this.names = new String[]{name};
        this.minRank = Rank.USER;
        register();
    }

    public Command(String[] names){
        this.names = names;
        this.minRank = Rank.USER;
        register();
    }

    public Command(String name, Rank minRank){
        this.names = new String[]{name};
        this.minRank = minRank;
        register();
    }

    public Command(String[] names, Rank minRank){
        this.names = names;
        this.minRank = minRank;
        register();
    }

    public String[] getNames(){
        return this.names;
    }

    public Rank getMinRank(){
        return this.minRank;
    }

    public boolean matches(String name){
        for(String s : getNames()){
            if(s.equalsIgnoreCase(name)) return true;
        }

        return false;
    }

    private void register(){
        CommandManager m = DungeonAPI.getCommandManager();
        if(!m.getCommands().contains(this)) m.getCommands().add(this);
    }

    public void execute(Player p, String label, String[] args){}

    public void unregister(){
        CommandManager m = DungeonAPI.getCommandManager();
        if(m.getCommands().contains(this)) m.getCommands().remove(this);
    }
}
