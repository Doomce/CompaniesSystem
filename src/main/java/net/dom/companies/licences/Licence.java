package net.dom.companies.licences;

import net.dom.companies.lang.MenuMessages;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Licence {

    private String name;

    private List<Integer> requirements;

    private List<String> desc;

    private Double cost;

    private Double sellCost;

    private Integer lvl;

    private Material material;

    Licence(String licName, Double cost, Double sCost, Integer minLevel, Material item) {
        this.name = licName;
        this.cost = cost;
        this.sellCost = sCost;
        this.lvl = minLevel;
        this.material = item;
    }

    public ItemStack getItemStack(boolean isOwned) {
        ItemStack is = new ItemStack(material);
        ItemMeta iMeta = is.getItemMeta();
        List<String> lore = new ArrayList<>();

        iMeta.setDisplayName(ChatColor.YELLOW+name);

        if (isOwned) {
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

        iMeta.setLore(lore);
        is.setItemMeta(iMeta);
        return is;
    }

    public Double getSellCost() {
        return sellCost;
    }

    public List<String> getDescription() {
        return desc;
    }

    public void setDescription(List<String> description) {
        this.desc = description;
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


    public void setRequirements(List<Integer> requirements) {
        this.requirements = requirements;
    }

    public List<Integer> getRequirements() {
        return requirements;
    }

    public static class Properties {
        public Licence lic;
        public int id;
        public boolean isOwned;
        public LocalDate expiry;

        Properties(Licence licence, int code, boolean owned) {
            this.id = code;
            this.lic = licence;
            this.isOwned = owned;
        }

        public Licence getLic() {
            return lic;
        }

        public int getId() {
            return id;
        }
    }
}
