package net.wrathofdungeons.dungeonapi.cmd.manager;

import java.util.ArrayList;

public class CommandManager {
    private ArrayList<Command> commands;

    public CommandManager(){
        this.commands = new ArrayList<Command>();
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public void unregister(Command command){
        command.unregister();
    }
}
