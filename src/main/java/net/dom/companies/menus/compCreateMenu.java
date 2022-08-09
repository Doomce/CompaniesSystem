package net.dom.companies.menus;

import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.dom.companies.Companies;
import net.dom.companies.provisions.businessForms;
import net.dom.companies.prompts.accCreateContributionPrompt;
import net.dom.companies.prompts.accCreateNamePrompt;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class compCreateMenu {

    private Player player;
    protected Gui gui = Gui.gui()
            .type(GuiType.CHEST).disableAllInteractions().rows(1).title(Component.text("Imones kurimas"))
            .create();

    private Conversation setupConversation(Player p, Map<Object, Object> data, Prompt pClass) {
        ConversationFactory factory = new ConversationFactory(Companies.getInstance());
        factory.withFirstPrompt(pClass).withTimeout(25).withInitialSessionData(data)
                .withEscapeSequence("atsaukti").withEscapeSequence("atšaukti").withLocalEcho(false);
        return factory.buildConversation(p);
    }
    public compCreateMenu(Player p, final Map<Object, Object> finalData) {
        this.player = p;

        boolean changeable;
        Map<Object, Object> data;
        if (finalData == null) {
            changeable = true;
            data = new HashMap<>();
            data.put("form", 0);
            data.put("init", 0.00);
            data.put("name", null);
        } else {
            data = finalData;
            changeable = false;
        }


        gui.addItem(new GuiItem(Material.AIR));
        gui.addItem(new GuiItem(formItem((int) data.get("form")), (event -> {
            if (!changeable) return;
            int selection = businessForms.valueOf(ChatColor.stripColor(event.getCurrentItem().getItemMeta()
                    .getDisplayName()).replaceAll("Verslo valdymo forma: ", "")).ordinal();
            if (event.isRightClick()) {
                if (selection >= 1) {
                    selection = 0;
                } else {
                    selection++;
                }
            } else if (event.isLeftClick()) {
                if (selection <= 0) {
                    selection = 1;
                } else {
                    selection--;
                }
            }
            if (selection == 0) {
                data.replace("name", p.getName());
            } else {
                data.replace("name", null);
            }
            gui.updateItem(1, formItem(selection));
            gui.updateItem(3, contributionItem(selection, (Double) data.get("init")));
            gui.updateItem(5, nameItem((String) data.get("name")));
            data.replace("form", selection);
            gui.updateItem(7, commitItem(data));
            gui.update();
        })));
        gui.addItem(new GuiItem(Material.AIR));
        gui.addItem(new GuiItem(contributionItem((int) data.get("form"), (Double) data.get("init")), (event -> {
            gui.close(p);
            setupConversation(p, data, new accCreateContributionPrompt()).begin();
        })));
        gui.addItem(new GuiItem(Material.AIR));
        gui.addItem(new GuiItem(nameItem((String) data.get("name")), (event -> {
            if ((int) data.get("form") == 0) return;
            gui.close(p);
            setupConversation(p, data, new accCreateNamePrompt()).begin();
        })));
        gui.addItem(new GuiItem(Material.AIR));
        gui.addItem(new GuiItem(commitItem(data), (event -> {
            gui.close(p);
            Companies.getInstance().getDb().createCompany(p.getUniqueId(), finalData);
        })));
    }

    public void open() {
        gui.open(player);
    }

    private static ItemStack formItem(int selected) {
        ItemStack item = new ItemStack(Material.BRICK);
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.GRAY+"- Individuali įmonė");
        lore.add(ChatColor.GRAY+"- Uždaroji akcinė bendrovė");
        lore.set(selected, ChatColor.YELLOW+ChatColor.stripColor(lore.get(selected)));

        businessForms bf = businessForms.values()[selected];
        lore.add("");
        lore.add(ChatColor.GRAY+"Max Darb: "+bf.getClassByName().getMaxWorkers());
        lore.add(ChatColor.GRAY+"Max Profit: "+bf.getClassByName().maxProfit());
        lore.add(ChatColor.GRAY+"Istatas: "+bf.getClassByName().initContribution());
        lore.add(ChatColor.GRAY+"Max Lic: "+bf.getClassByName().licCount());
        lore.add(ChatColor.GRAY+"Max Kontraktu: "+bf.getClassByName().maxInProgressContracts());
        lore.add("");

        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(ChatColor.YELLOW+"Verslo valdymo forma: "+bf);
        iMeta.setLore(lore);
        item.setItemMeta(iMeta);
        return item;
    }

    private static ItemStack contributionItem(int selected, double amount) {
        ItemStack item = new ItemStack(Material.BRICK);
        List<String> lore = new ArrayList<>();

        businessForms bf = businessForms.values()[selected];
        lore.add(ChatColor.GRAY+"Jūsų įnešama suma: "+amount);
        lore.add(ChatColor.GRAY+"Minimali suma: "+bf.getClassByName().initContribution());

        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(ChatColor.YELLOW+"ĮSTATINIS  KAPITALAS");
        iMeta.setLore(lore);
        item.setItemMeta(iMeta);
        return item;
    }

    private static ItemStack nameItem(String name) {
        ItemStack item = new ItemStack(Material.BRICK);
        List<String> lore = new ArrayList<>();

        if (name == null) {
            lore.add(ChatColor.GRAY+"Pavadinimas nenustatytas");
        } else {
            lore.add(ChatColor.GRAY + "" + name);
        }

        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(ChatColor.YELLOW+"IMONES PAVADINIMAS");
        iMeta.setLore(lore);
        item.setItemMeta(iMeta);
        return item;
    }

    private static ItemStack commitItem(Map<Object, Object> data) {
        int form = (int) data.get("form");
        double init = (double) data.get("init");
        String name = (String) data.get("name");
        double minInit = businessForms.values()[form].getClassByName().initContribution();

        ItemStack item = new ItemStack(Material.BRICK);
        List<String> lore = new ArrayList<>();
        if (name == null) {
            lore.add(ChatColor.GRAY+"X Nera pavadinimo");
        } else {
            lore.add(ChatColor.GRAY + "Pavadinimas: " +businessForms.values()[form].name()+" "+ name);
        }
        if (init<minInit) {
            lore.add(ChatColor.GRAY+"X per mazas istatas.");
        } else {
            lore.add(ChatColor.GRAY+"Inasas: "+init+".");
        }

        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(ChatColor.YELLOW+"IMONES SUKURIMAS");
        iMeta.setLore(lore);
        item.setItemMeta(iMeta);
        return item;
    }

}
