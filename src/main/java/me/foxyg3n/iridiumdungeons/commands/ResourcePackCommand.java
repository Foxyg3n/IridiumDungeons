package me.foxyg3n.iridiumdungeons.commands;

import org.bukkit.command.CommandExecutor;

import me.foxyg3n.iridiumdungeons.IridiumDungeons;
import me.foxyg3n.iridiumdungeons.configs.ResourcePack;

public class ResourcePackCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if(!sender.hasPermission("*")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        ResourcePack resourcePack = IridiumDungeons.getInstance().getResourcePack();
        if(args.length == 0) {
            resourcePack.setResourcePack(null);
            sender.sendMessage("Reseting resourcepack...");
            return true;
        } else if(args.length != 1) {
            sender.sendMessage("Incorrect amount of arguments");
            return true;
        }
        resourcePack.setResourcePack(args[0]);
        sender.sendMessage("Resource pack set to " + args[0]);
        return true;
    }

}
