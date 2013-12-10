package vc.pvp.skywars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import vc.pvp.skywars.config.PluginConfig;
import vc.pvp.skywars.utilities.Messaging;
import vc.pvp.skywars.utilities.WEUtils;


@CommandDescription("Portal operations")
@CommandPermissions("skywars.command.portal")
public class PortalCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 3) {
                return false;
            }
            String subCommand = args[1];
            if (subCommand.equalsIgnoreCase("define")) {
                Vector[] vector = WEUtils.getSelection(player);
                if (vector == null) {
                    sender.sendMessage(new Messaging.MessageFormatter().format("no-region-selected"));
                    return false;
                }
                PluginConfig.setPortal(args[2], vector[0], vector[1]);
                sender.sendMessage(new Messaging.MessageFormatter().format("portal-set"));
                return true;
            } else if (subCommand.equalsIgnoreCase("redefine")) {
                
            } else if (subCommand.equalsIgnoreCase("remove")) {
                
            } else if (subCommand.equalsIgnoreCase("list")) {
                
            }
        }
        sender.sendMessage("Invalid syntax.");
        return true;
    }
    
    
}
