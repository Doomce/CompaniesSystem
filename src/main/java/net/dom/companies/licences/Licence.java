package net.dom.companies.licences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dom.companies.lang.MenuMessages;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Licence {

    private Integer code;
    private String name;
    private boolean isOwned = false;
    private List<String> description;

    @Nullable
    private Double cost; //REQUIREMENT

    @Nullable
    private Integer lvl; //REQUIREMENT

    @Nullable
    private Material itemStack; //OPTIONAL

    Licence(int licCode, String licName, @Nullable Double licCost, @Nullable Integer minLevel) {
        this.name = licName;
        this.code = licCode;
        this.cost = licCost;
        this.lvl = minLevel;
    }

    Licence(int licCode, String licName, @Nullable Double licCost, @Nullable Integer minLevel, Material item) {
        this.name = licName;
        this.code = licCode;
        this.cost = licCost;
        this.lvl = minLevel;
        this.itemStack = item;
    }

    public ItemStack getItemStack() {
        if (itemStack == null) {
            itemStack = Material.PAPER;
        }
        ItemStack item = new ItemStack(itemStack);
        ItemMeta iMeta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        iMeta.setDisplayName(ChatColor.YELLOW+name);

        if (isOwned()) {
            lore.add(MenuMessages.PURCHASED.getMessage());
            lore.add("Pardavimo kaina: "+getSellCost());
            iMeta.addEnchant(Enchantment.LURE, 1, true);
            iMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            lore.add(MenuMessages.NOT_PURCHASED.getMessage());
            lore.add("Pirkimo kaina: "+getCost());
            lore.add("Minimalus lygis: "+getLvl());
            if (!getRequirements().isEmpty()) {
                lore.add("Reikalingos licencijos: "+getRequirements().toString());
            }
        }
        lore.addAll(getDescription());

        lore.add("----------------------------");
        lore.add(StringUtils.center("Kodas: "+getCode(), 28));

        iMeta.setLore(lore);
        item.setItemMeta(iMeta);
        return item;
    }

    public Double getSellCost() {
        return cost*0.70;
    }

    public List<String> getDescription() {
        return description;
    }

    public Double getCost() {
        return cost;
    }

    public Integer getLvl() {
        return lvl;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public void setOwned() {
        isOwned = true;
    }

    public List<Integer> getRequirements() {
        return Collections.emptyList();
    }
}
