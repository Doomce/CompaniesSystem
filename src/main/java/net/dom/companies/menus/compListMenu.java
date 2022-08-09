package net.dom.companies.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.dom.companies.database.Company;
import net.dom.companies.functions.companyManager;
import net.dom.companies.objects.duty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class compListMenu {

    protected PaginatedGui gui = Gui.paginated()
            .title(Component.text("Darbovietės").decorate(TextDecoration.BOLD).color(TextColor.fromHexString("#138D75")))
            .disableAllInteractions()
            .pageSize(18)
            .rows(3)
            .create();
    protected Player player;

    public compListMenu(Player p, List<Company> companies, List<duty> duties, boolean allowCreate) {
        this.player = p;

        for (int i = 0; i < companies.size(); i++) {
            Company comp = companies.get(i);
            gui.addItem(new GuiItem(compItem(comp, duties.get(i)),
                    (event -> companyManager.openCompanyPanel(p, comp.getCompId()))));
        }

        gui.setItem(3, 2, ItemBuilder.from(Material.PAPER)
                .name(Component.text("Praeitas puslapis").decorate(TextDecoration.BOLD).color(TextColor.fromHexString("#2874A6")))
                .asGuiItem(event -> gui.previous()));

        gui.setItem(3, 8, ItemBuilder.from(Material.PAPER)
                .name(Component.text("Kitas puslapis").decorate(TextDecoration.BOLD).color(TextColor.fromHexString("#B03A2E")))
                .asGuiItem(event -> gui.next()));

        if (allowCreate) {
            gui.setItem(3, 5, ItemBuilder.from(Material.BRICKS)
                    .name(Component.text("Įmonės kūrimas").decorate(TextDecoration.BOLD).color(TextColor.fromHexString("#138D75")))
                    .asGuiItem(event -> new compCreateMenu(p, null).open()));
        }
    }

    public void open() {
        gui.open(player);
    }

    private ItemStack compItem(Company var2, duty var3) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta iMeta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"Šioje įmonėje esate:");

        switch (var3) {
            case OWNER: {
                lore.add(ChatColor.GRAY+"Savininkas");
                break;
            }
            case WORKER: {
                lore.add(ChatColor.GRAY+"Darbuotojas");
                break;
            }
            case CO_OWNER: {
                lore.add(ChatColor.GRAY+"Direktoriaus pavaduotojas");
                break;
            }
        }

        iMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+""+var2.getName());
        iMeta.setLore(lore);
        item.setItemMeta(iMeta);
        return item;
    }

}
