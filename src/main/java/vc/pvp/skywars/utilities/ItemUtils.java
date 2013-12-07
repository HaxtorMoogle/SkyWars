package vc.pvp.skywars.utilities;

import com.sk89q.worldedit.InvalidItemException;
import com.sk89q.worldedit.ServerInterface;
import com.sk89q.worldedit.UnknownItemException;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.blocks.ClothColor;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class ItemUtils {

    public static ItemStack parseItem(String[] args) throws UnknownItemException, InvalidItemException {
        if (args.length < 1) {
            return null;
        }
        ServerInterface server = null;
        String[] typeAndData = args[0].split(":", 2);
        String blockInput = typeAndData[0];
        BlockType blockType;
        int blockId = -1;
        int blockData = -1;
        boolean parseDataValue = true;
        ItemStack itemStack = null;

        try {
            blockId = Integer.parseInt(blockInput);
            blockType = BlockType.fromID(blockId);
            itemStack = new ItemStack(Material.getMaterial(blockType.toString()));
        } catch (NumberFormatException e) {
            blockType = BlockType.lookup(blockInput);
            if (blockType == null) {
                int t = server.resolveItem(blockInput);
                if (t > 0) {
                    blockType = BlockType.fromID(t);
                    itemStack = new ItemStack(Material.getMaterial(blockType.toString()));
                    blockId = t;
                }
            } else {
                itemStack = new ItemStack(Material.getMaterial(blockType.toString()));
            }
        }
        if (itemStack == null) {
            // Check if it's wool
            ClothColor col = ClothColor.lookup(blockInput);
            if (col == null) {
                throw new UnknownItemException(args[0]);
            }
            itemStack = new ItemStack(Material.WOOL);
            blockData = col.getID();
            itemStack.setDurability((short) blockData);
            parseDataValue = false;
        }
        if (blockId == -1) {
            blockId = blockType.getID();
        }
        if (parseDataValue) {
            try {
                if (typeAndData.length > 1 && typeAndData[1].length() > 0) {
                    blockData = Integer.parseInt(typeAndData[1]);
                } else {
                    blockData = 0;
                }
            } catch (NumberFormatException e) {
                blockData = 0;
            }
            itemStack.setDurability((short) blockData);
        }

        if (args.length > 1 && Integer.parseInt(args[1]) > 0) {
            itemStack.setAmount(Integer.parseInt(args[1]));
        }

        if (args.length > 2) {
            String[] enchAndLev = args[2].split(":", 2);
            try {
                itemStack.addEnchantment(Enchantment.getByName(enchAndLev[0]), Integer.parseInt(enchAndLev[1]));
            } catch (IllegalArgumentException e) {
                // TODO: Handle enchantments properly
            }
        }

        return itemStack;
    }

    public static ItemStack name(ItemStack itemStack, String name, String... lores) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!name.isEmpty()) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }

        if (lores.length > 0) {
            List<String> loreList = new ArrayList<String>(lores.length);

            for (String lore : lores) {
                loreList.add(ChatColor.translateAlternateColorCodes('&', lore));
            }

            itemMeta.setLore(loreList);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
