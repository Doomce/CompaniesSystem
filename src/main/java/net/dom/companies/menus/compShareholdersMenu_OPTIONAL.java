package net.dom.companies.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.dom.companies.Companies;
import net.dom.companies.database.CompaniesEmployees;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class compShareholdersMenu_OPTIONAL {

    public void gui(Player p, long compId, List<CompaniesEmployees> employees, boolean isAdmin) {


        PaginatedGui gui = Gui.paginated().pageSize(27).rows(4).disableAllInteractions()
                .title(Component.text("Įmonės akcininkai"))
                .create();

        gui.setItem(List.of(27, 29, 30, 31, 32, 33, 35),
                ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text("")).asGuiItem());

        gui.setItem(28,
                ItemBuilder.from(Material.PAPER)
                        .name(Component.text("Praeitas puslapis").decorate(TextDecoration.BOLD))
                .asGuiItem(event -> gui.previous()));

        gui.setItem(34,
                ItemBuilder.from(Material.PAPER)
                        .name(Component.text("Kitas puslapis").decorate(TextDecoration.BOLD))
                .asGuiItem(event -> gui.previous()));

        /*gui.setItem(31,
            ItemBuilder.from(Material.PAPER)
                    .name(Component.text("Priimti naują darbuotoją").decorate(TextDecoration.BOLD))
                .asGuiItem(event -> Companies.getInstance().getEmpMng().inviteEmpPrompt(p, compId)));

        gui.setItem(35, new GuiItem(ItemBuilder.from(Material.CLOCK)
                .name(Component.text("<- Atgal").decorate(TextDecoration.BOLD).color(TextColor.fromHexString("#138D75")))
                .build(), (event -> companyManager.openCompanyPanel(p, compId))));

                TODO: IDEA- AKCININKU BUTTONAI.

        
        for (CompaniesEmployees compEmp : employees) {
            gui.addItem(new GuiItem(EmpItem(compEmp), event -> {
                if (event.getAction().equals(InventoryAction.DROP_ALL_SLOT)) {
                    Companies.getInstance().getEmpMng().kick(p, compId, compEmp.getId().getEmployeeId(), false);
                    return;
                }
                if (!event.isShiftClick()) return;
                if (event.isRightClick()) {
                    Companies.getInstance().getEmpMng().demote(p, compId, compEmp, false);
                    return;
                }
                if (event.isLeftClick()) {
                    Companies.getInstance().getEmpMng().promote(p, compId, compEmp, false);
                }
            }));
        }

         */

        Bukkit.getScheduler().runTask(Companies.getInstance(), () -> gui.open(p));
    }

    private ItemStack EmpItem(CompaniesEmployees compEmp) {
        ItemStack item = ItemBuilder.skull().owner(Bukkit.getOfflinePlayer(compEmp.getId().getEmployeeId())).build();
        ItemMeta iMeta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        iMeta.setDisplayName(Bukkit.getPlayer(compEmp.getId().getEmployeeId()).getName());

        //if salary > 0, then it is working;

        lore.add("--");
        lore.add(compEmp.getDuties().toString());
        lore.add(compEmp.getSalary()+"");
        lore.add(compEmp.getWorkingFrom()+"");
        lore.add("--");

        lore.add("");

        iMeta.setLore(lore);
        item.setItemMeta(iMeta);
        return item;
    }



}
