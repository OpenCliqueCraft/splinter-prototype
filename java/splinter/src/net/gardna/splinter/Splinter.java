package net.gardna.splinter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

class MyCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("You ran /mycommand");

        return true;
    }
}

public class Splinter extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("mycommand").setExecutor(new MyCommandExecutor());
    }
}
