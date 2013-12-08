package vc.pvp.skywars.utilities;

import com.sk89q.worldedit.InvalidItemException;
import com.sk89q.worldedit.UnknownItemException;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.blocks.ClothColor;
import com.sk89q.worldedit.blocks.ItemType;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class ItemUtils {

    public static ItemStack parseItem(String[] args) throws UnknownItemException, InvalidItemException {
        if (args.length < 1) {
            return null;
        }
        String[] typeAndData = args[0].split(":", 2);
        String blockInput = typeAndData[0];
        BlockType blockType = null;
        int blockData = -1;
        boolean blockFound = false;
        ItemStack itemStack = null;
        try {
            int blockId = Integer.parseInt(blockInput);
            Material mat = Material.getMaterial(blockId);
            itemStack = new ItemStack(mat);
            blockFound = true;
        } catch (Exception e) {
            blockType = BlockType.lookup(blockInput);
        }
        if (blockType == null && !blockFound) {
            try {
                Material mat = Material.matchMaterial(blockInput);
                itemStack = new ItemStack(mat);
            } catch (Exception e) {
                ItemType itemType = ItemType.lookup(blockInput);
                if (itemType != null) {
                    Material mat = Material.getMaterial(itemType.getID());
                    itemStack = new ItemStack(mat);
                    blockFound = true;
                } else {
                    return null;
                }
            }
        }
        if (!blockFound) {
            try {
                Material mat = Material.getMaterial(blockType.getID());
                itemStack = new ItemStack(mat);
            } catch (Exception e) {
                return null;
            }
        }

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

        if (args.length > 1 && Integer.parseInt(args[1]) > 0) {
            itemStack.setAmount(Integer.parseInt(args[1]));
        }

        if (args.length > 2) {
            String[] enchAndLev = args[2].split(":", 2);
            Enchantment ench = findEnchantment(enchAndLev[0], itemStack);
            if (ench != null) {
                try {
                    int level = Integer.parseInt(enchAndLev[1]);
                    if (level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
                        itemStack.addEnchantment(ench, level);
                        Bukkit.getServer().getLogger().log(Level.INFO, "Added enchantment: " + ench.toString() + ", level: " + level + " on item: " + itemStack.toString());
                    }
                } catch (NumberFormatException e) {
                }
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

    @SuppressWarnings("deprecation")
    public static Enchantment findEnchantment(String enchantmentInput, ItemStack itemStack) {
        Enchantment enchantment = null;
        EnchantmentUtils enchantmentType = null;
        try {
            int enchantmentID = Integer.parseInt(enchantmentInput);
            enchantment = Enchantment.getById(enchantmentID);
        } catch (NumberFormatException e) {
            enchantment = Enchantment.getByName(enchantmentInput);
        }
        if (enchantment == null) {
            enchantmentType = EnchantmentUtils.lookup(enchantmentInput);
            if (enchantmentType != null) {
                enchantment = Enchantment.getByName(enchantmentType.toString());
            }
        }

        Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
        if (enchantment != null) {
            if (enchantment.canEnchantItem(itemStack)) {
                for (Enchantment existingEnchantment : enchantments.keySet()) {
                    if (enchantment.conflictsWith(existingEnchantment)) {
                        return null;
                    }
                }
                return enchantment;
            } else {
                return null;
            }
        } else {
            return null;
        }
        //Field[] field = Enchantment.class.getFields();


        //return enchantment;
    }
}
