package net.wrathofdungeons.dungeonapi.cmd;

import net.wrathofdungeons.dungeonapi.cmd.manager.Command;
import org.bukkit.entity.Player;

public class TestCommand extends Command {
    public TestCommand(){
        super(new String[]{"test"});
    }

    @Override
    public void execute(Player p, String label, String[] args) {
        p.sendMessage("TEST");
    }
}
