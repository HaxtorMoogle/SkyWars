package vc.pvp.skywars.commands;

import com.sk89q.worldedit.InvalidItemException;
import com.sk89q.worldedit.UnknownItemException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import vc.pvp.skywars.SkyWars;
import vc.pvp.skywars.controllers.ChestController;
import vc.pvp.skywars.controllers.KitController;
import vc.pvp.skywars.utilities.Messaging;

@CommandDescription("Reloads the chests, kits and the plugin.yml")
@CommandPermissions("skywars.command.reload")
public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            ChestController.get().load();
        } catch (UnknownItemException ex) {
            Logger.getLogger(ReloadCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidItemException ex) {
            Logger.getLogger(ReloadCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            KitController.get().load();
        } catch (UnknownItemException ex) {
            Logger.getLogger(ReloadCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidItemException ex) {
            Logger.getLogger(ReloadCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        SkyWars.get().reloadConfig();
        new Messaging(SkyWars.get());

        sender.sendMessage(new Messaging.MessageFormatter().format("success.reload"));
        return true;
    }
}
