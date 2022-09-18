package net.dom.companies.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.dom.companies.database.CompaniesEmployees;
import net.dom.companies.database.Company;
import net.dom.companies.functions.companyManager;
import net.dom.companies.functions.menuFunctions;
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

    public compListMenu(Player p, List<CompaniesEmployees> compPlaces, boolean allowCreate) {
        this.player = p;

        for (CompaniesEmployees emp : compPlaces) {
            gui.addItem(new GuiItem(compItem(emp, emp.getCompany().getName()),
                    (event -> menuFunctions.openCompanyMenu(p, emp.getCompany().getCompId()))));
        }

        setup();

        if (allowCreate) {
            gui.setItem(3, 5, ItemBuilder.from(Material.BRICKS)
                    .name(Component.text("Įmonės kūrimas").decorate(TextDecoration.BOLD).color(TextColor.fromHexString("#138D75")))
                    .asGuiItem(event -> new compCreateMenu(p, null).open()));
        }
    }

    public compListMenu(Player p, List<Company> companies) {
        this.player = p;

        for (Company company : companies) {
            gui.addItem(new GuiItem(compItem(null, company.getName()),
                    (event -> menuFunctions.openCompanyMenu(p, company.getCompId()))));
        }

        setup();
    }

    private void setup() {
        gui.setItem(3, 2, ItemBuilder.from(Material.PAPER)
                .name(Component.text("Praeitas puslapis").decorate(TextDecoration.BOLD).color(TextColor.fromHexString("#2874A6")))
                .asGuiItem(event -> gui.previous()));

        gui.setItem(3, 8, ItemBuilder.from(Material.PAPER)
                .name(Component.text("Kitas puslapis").decorate(TextDecoration.BOLD).color(TextColor.fromHexString("#B03A2E")))
                .asGuiItem(event -> gui.next()));
    }

    public void open() {
        gui.open(player);
    }

    private ItemStack compItem(CompaniesEmployees employee, String compName) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta iMeta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (employee != null) {
            lore.add(ChatColor.GRAY + "Šioje įmonėje esate:");

            switch (employee.getDuties()) {
                case OWNER: {
                    lore.add(ChatColor.GRAY + "Savininkas");
                    break;
                }
                case WORKER: {
                    lore.add(ChatColor.GRAY + "Darbuotojas");
                    break;
                }
                case CO_OWNER: {
                    lore.add(ChatColor.GRAY + "Direktoriaus pavaduotojas");
                    break;
                }
            }

            if (employee.isShareholder()) lore.add(ChatColor.GRAY + "(AKCININKAS)");

        }

        iMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+""+compName);
        iMeta.setLore(lore);
        item.setItemMeta(iMeta);
        return item;
    }

}
