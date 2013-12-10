package vc.pvp.skywars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import vc.pvp.skywars.config.PluginConfig;
import vc.pvp.skywars.utilities.Messaging;
import vc.pvp.skywars.utilities.WEUtils;

@CommandDescription("Use define, redefine, list or remove for portal operations.")
@CommandPermissions("skywars.command.portal")
public class PortalCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 2) {
                return false;
            }
            String subCommand = args[1];
            if (subCommand.equalsIgnoreCase("define")) {
                if (args[2] == null) {
                    sender.sendMessage(new Messaging.MessageFormatter().format("no-name-specified"));
                    return true;
                }
                if (PluginConfig.getPortal(args[2]) != null) {
                    sender.sendMessage(new Messaging.MessageFormatter().format("portal-already-exists"));
                    return true;
                }
                Vector[] vector = WEUtils.getSelection(player);
                if (vector == null) {
                    sender.sendMessage(new Messaging.MessageFormatter().format("no-region-selected"));
                    return true;
                }
                String world = player.getWorld().getName();
                PluginConfig.setPortal(args[2], world, vector[0], vector[1]);
                sender.sendMessage(new Messaging.MessageFormatter().format("portal-set"));
                return true;
            } else if (subCommand.equalsIgnoreCase("redefine")) {
                if (args[2] == null) {
                    sender.sendMessage(new Messaging.MessageFormatter().format("no-name-specified"));
                    return true;
                }
                if (PluginConfig.getPortal(args[2]) == null) {
                    sender.sendMessage(new Messaging.MessageFormatter().format("no-such-portal"));
                    return true;
                }
                Vector[] vector = WEUtils.getSelection(player);
                if (vector == null) {
                    sender.sendMessage(new Messaging.MessageFormatter().format("no-region-selected"));
                    return true;
                }
                String world = player.getWorld().getName();
                PluginConfig.setPortal(args[2], world, vector[0], vector[1]);
                sender.sendMessage(new Messaging.MessageFormatter().format("portal-set"));
                return true;
            } else if (subCommand.equalsIgnoreCase("remove")) {
            } else if (subCommand.equalsIgnoreCase("list")) {
            }
        }
        sender.sendMessage("Invalid syntax.");
        return true;
    }
}
