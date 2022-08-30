package net.dom.companies.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.dom.companies.Companies;
import net.dom.companies.database.CompaniesEmployees;
import net.dom.companies.functions.companyManager;
import net.dom.companies.functions.employeeManager;
import net.dom.companies.functions.menuFunctions;
import net.dom.companies.lang.Language;
import net.dom.companies.objects.duty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class compEmpsMenu {

    public void gui(Player p, long compId, List<CompaniesEmployees> employees, boolean isAdmin) {


        PaginatedGui gui = Gui.paginated().pageSize(27).rows(4).disableAllInteractions()
                .title(Component.text("Įmonės darbuotojai"))
                .create();

        gui.setItem(List.of(27, 29, 30, 32, 33),
                ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text("")).asGuiItem());

        gui.setItem(28,
                ItemBuilder.from(Material.PAPER)
                        .name(Component.text("Praeitas puslapis").decorate(TextDecoration.BOLD))
                .asGuiItem(event -> gui.previous()));

        gui.setItem(34,
                ItemBuilder.from(Material.PAPER)
                        .name(Component.text("Kitas puslapis").decorate(TextDecoration.BOLD))
                .asGuiItem(event -> gui.previous()));

        gui.setItem(31,
            ItemBuilder.from(Material.PAPER)
                    .name(Component.text("Priimti naują darbuotoją").decorate(TextDecoration.BOLD))
                .asGuiItem(event -> menuFunctions.invitationPrompt(p, compId)));

        gui.setItem(35, new GuiItem(ItemBuilder.from(Material.CLOCK)
                .name(Component.text("<- Atgal").decorate(TextDecoration.BOLD).color(TextColor.fromHexString("#138D75")))
                .build(), (event -> menuFunctions.openCompanyMenu(p, compId))));
        
        for (CompaniesEmployees compEmp : employees) {
            gui.addItem(new GuiItem(EmpItem(compEmp), event -> {
                if (event.getAction().equals(InventoryAction.DROP_ALL_SLOT)) {
                    menuFunctions.empAction(1, p, compId, compEmp);
                    return;
                }
                if (!event.isShiftClick()) {
                    menuFunctions.empAction(4, p, compId, compEmp);
                    return;
                }
                if (event.isRightClick()) {
                    menuFunctions.empAction(3, p, compId, compEmp);
                    return;
                }
                if (event.isLeftClick()) {
                    menuFunctions.empAction(2, p, compId, compEmp);
                }
            }));
        }

        Bukkit.getScheduler().runTask(Companies.getInstance(), () -> gui.open(p));
    }

    private ItemStack EmpItem(CompaniesEmployees compEmp) {
        ItemStack item = ItemBuilder.skull().owner(Bukkit.getOfflinePlayer(compEmp.getId().getEmployeeId())).build();
        ItemMeta iMeta = item.getItemMeta();

        Player employee = Bukkit.getPlayer(compEmp.getId().getEmployeeId());
        if (employee.isOnline()) {
            iMeta.setDisplayName(Language.get("menus.workersMenu.name",
                    "{Name}", employee.getName(), "{Color}", "<#2b9348>", "{State}", "ON"));
        } else {
            iMeta.setDisplayName(Language.get("menus.workersMenu.name",
                    "{Name}", employee.getName(), "{Color}", "<#dc2f02>", "{State}", "OFF"));
        }

        String salary;
        if (compEmp.getSalary() == null && !compEmp.getDuties().equals(duty.OWNER)) {
            salary = "<#dc2f02>NENUSTATYTA!";
        } else if (compEmp.getDuties().equals(duty.OWNER)) {
            salary = "<#2b9348>DIVIDENDAI";
        } else {
            salary = "<#2b9348>"+compEmp.getSalary()+" EUR";
        }

        List<String> lore = new ArrayList<>(Language.getList("menus.workersMenu.lore",
                "{Duties}", translateDuties(compEmp.getDuties()),
                "{Wage}", salary,
                "{Date}", compEmp.getWorkingFrom() + " (" + DAYS.between(compEmp.getWorkingFrom(), LocalDate.now()) + "d.)"));

        String section = lore.get(lore.size()-1);
        List<String> controls = generateControls(compEmp);
        lore.addAll(controls);
        if (!controls.isEmpty()) lore.add(section);

        iMeta.setLore(lore);
        item.setItemMeta(iMeta);
        return item;
    }

    private List<String> generateControls(CompaniesEmployees compEmp) {
        List<String> lore = new ArrayList<>();
        if (compEmp.getDuties().ordinal()>duty.OWNER.ordinal()) {
            lore.addAll(Language.getList("menus.workersMenu.actions.kick"));
            lore.add("");
            lore.addAll(Language.getList("menus.workersMenu.actions.wage"));
        }
        if (compEmp.getDuties().ordinal()>duty.CO_OWNER.ordinal()) {
            lore.add("");
            lore.addAll(Language.getList("menus.workersMenu.actions.promote"));
            if (compEmp.getDuties().ordinal()<duty.WORKER.ordinal()) {
                lore.add("");
                lore.addAll(Language.getList("menus.workersMenu.actions.demote"));
            }
        }
        return lore;
    }

    private String translateDuties(duty duty) {
        String duties = "";
        switch (duty) {
            case CO_OWNER: {
                duties = "<#9e2a2b>PAVADUOTOJAS";
                break;
            }
            case MANAGER: {
                duties = "<#e09f3e>VADYBININKAS";
                break;
            }
            case OWNER: {
                duties = "<#9e2a2b>DIREKTORIUS";
                break;
            }
            case WORKER: {
                duties = "<#a3b18a>DARBUOTOJAS";
                break;
            }
        }
        return duties;
    }



}
