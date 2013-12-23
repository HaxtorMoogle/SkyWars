package vc.pvp.skywars.utilities;

import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.blocks.ItemType;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class ItemUtils {

    @SuppressWarnings("deprecation")
    public static ItemStack parseItem(String[] args) {
        if (args.length < 1) {
            return null;
        }
        String[] typeAndData = args[0].split(":", 2);
        String blockInput = typeAndData[0];
        Material mat = Material.matchMaterial(blockInput);
        if (mat == null) {
            BlockType blockType = BlockType.lookup(blockInput);
            if (blockType != null) {
                mat = Material.getMaterial(blockType.getID());
            }
        }
        if (mat == null) {
            ItemType itemType = ItemType.lookup(blockInput);
            if (itemType != null) {
                mat = Material.getMaterial(itemType.getID());
            }
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
            net.milkbowl.vault.item.ItemInfo itemInfo = net.milkbowl.vault.item.Items.itemByName(blockInput);
            mat = itemInfo.material;
        }
        if (mat == null) {
            return null;
        }
        ItemStack itemStack = new ItemStack(mat);

        try {
            if (typeAndData.length > 1 && typeAndData[1].length() > 0) {
                short blockData = Short.parseShort(typeAndData[1]);
                itemStack.setDurability(blockData);
            }
        } catch (NumberFormatException e) {
        }

        if (args.length > 1 && Integer.parseInt(args[1]) > 0) {
            try {
            itemStack.setAmount(Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                itemStack.setAmount(1);
            }
        }

        if (args.length > 2) {
            String[] enchAndLev = args[2].split(":", 2);
            Enchantment ench = findEnchantment(enchAndLev[0], itemStack);
            if (ench != null) {
                try {
                    int level = Integer.parseInt(enchAndLev[1]);
                    if (level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
                        itemStack.addEnchantment(ench, level);
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
    }
}
