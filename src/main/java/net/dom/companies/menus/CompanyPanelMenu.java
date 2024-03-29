package net.dom.companies.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.dom.companies.Companies;
import net.dom.companies.database.CompaniesEmployees;
import net.dom.companies.database.Company;
import net.dom.companies.functions.compManagementAssist;
import net.dom.companies.functions.employeeManager;
import net.dom.companies.functions.menuFunctions;
import net.dom.companies.lang.Language;
import net.dom.companies.objects.duty;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/*
Comp State errors for assist:
Code - Error- problem solving

0 - Salary check - Call wage change prompt;.
1 - Taxes check - Pay taxes function..
2 - Payout salary check - Payout salaries function.


 */


public class CompanyPanelMenu {

    private final Player player;
    private final Company comp;
    private final long compId;
    private final CompaniesEmployees employee;

    protected compManagementAssist assist;

    private Gui gui;


    /**
     * @param var1 Player
     * @param var2 A company
     * @param var3 A company employee. If admin mode - null;
     */
    public CompanyPanelMenu(Player var1, Company var2, CompaniesEmployees var3) {

        this.player = var1;
        this.comp = var2;
        this.compId = var2.getCompId();
        this.employee = var3;
        this.assist = new compManagementAssist(var2, var3);
    }

    private void cSetup() {
        gui.setItem(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8 , 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35),
                ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text("")).asGuiItem());

        gui.setItem(2, 2, new GuiItem(setupInfoItem(), event -> assist.action(player)));
        gui.setItem(2, 4, new GuiItem(setupEmpItem(), (event -> {
            if (!employee.getDuties().getPermission(3)) return;
            menuFunctions.openCompanyEmpMenu(player, compId);
        })));
        gui.setItem(2, 6, new GuiItem(setupVaultItem(), (event -> {
            player.sendMessage(ChatColor.RED+" Banko veiksmai atliekami tik įmonių priežiūros tarnyboje!");
        })));
        gui.setItem(2, 8, new GuiItem(setupLicItem(), (event -> {
            if (!employee.getDuties().getPermission(5)) return;
            menuFunctions.openLicMenu(player, compId);
        })));
        gui.setItem(3, 2, new GuiItem(setupWageItem(), (event -> {
            menuFunctions.payout(compId);
        })));
        //TODO KONTRAKTAI
        //TODO MOKESČIAI
        //TODO IMONES UZDARYMAS
    }

    private void empSetup() {
        gui.setItem(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8 , 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26),
                ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text("")).asGuiItem());

        gui.setItem(2, 2, new GuiItem(setupInfoItem()));
        gui.setItem(2, 3, new GuiItem(setupEmpItem()));
        gui.setItem(2, 4, new GuiItem(setupWageItemEmp()));
        //TODO OFISO KOORDINATES.
        gui.setItem(2, 6, new GuiItem(setupLicItem())); //TODO GALBUT SURASYTI I LORE KOKIE LEIDIMAI
        gui.setItem(2, 8, new GuiItem(quitItem()));

    }

    public void setupInv() {
        if (employee == null || employee.getDuties().equals(duty.OWNER) || employee.getDuties().equals(duty.CO_OWNER)) {
            gui = Gui.gui()
                    .type(GuiType.CHEST)
                    .disableAllInteractions()
                    .title(Component.text(comp.getName()))
                    .rows(4)
                    .create();
            cSetup();
        } else {
            gui = Gui.gui()
                    .type(GuiType.CHEST)
                    .disableAllInteractions()
                    .title(Component.text(comp.getName()))
                    .rows(3)
                    .create();
            empSetup();
        }
    }

    public void open() {
        gui.open(player);
    }

    private ItemStack setupInfoItem() {
        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta iMeta = infoItem.getItemMeta();
        List<String> lore = new ArrayList<>();

        iMeta.setDisplayName(Language.get("menus.companyPanel.infoItem.name", "{name}",
                "UAB " + comp.getName()));
        lore.addAll(Language.getList("menus.companyPanel.infoItem.lore",
                "{state}", assist.getMessage(), "{code}", comp.getCompId()+""));

        iMeta.setLore(lore);
        infoItem.setItemMeta(iMeta);
        return infoItem;
    }

    private ItemStack setupEmpItem() {
        ItemStack employeesListItem = new ItemStack(Material.PAPER);
        ItemMeta iMeta = employeesListItem.getItemMeta();
        List<String> lore = new ArrayList<>();

        int isWorking = 0, workers = 0;
        for (CompaniesEmployees emp : comp.getCE()) {
            if (emp.getSalary() != null) {
                isWorking++;
            }
            workers++;
        }

        iMeta.setDisplayName(Language.get("menus.companyPanel.empItem.name"));

        if (employee.getDuties().getPermission(3)) {
            lore.addAll(Language.getList("menus.companyPanel.empItem.lore.ceo",
                    "{Total}", workers, "{Working}", isWorking));
        } else {
            lore.add(Language.get("menus.companyPanel.empItem.lore.worker.section"));
            comp.getCE().stream().sorted(Comparator.comparing(CompaniesEmployees::getDuties)).forEach((emp -> {
                Player p = Bukkit.getPlayer(emp.getId().getEmployeeId());
                if (p.isOnline()) {
                    lore.add(Language.get("menus.companyPanel.empItem.lore.worker.element",
                            "{Name}", p.getName(), "{Color}", "<#2b9348>", "{State}", "ON"));
                } else {
                    lore.add(Language.get("menus.companyPanel.empItem.lore.worker.element",
                            "{Name}", p.getName(), "{Color}", "<#dc2f02>", "{State}", "OFF"));
                }
            }));
            lore.add(Language.get("menus.companyPanel.empItem.lore.worker.section"));
        }
        iMeta.setLore(lore);
        employeesListItem.setItemMeta(iMeta);
        return employeesListItem;
    }

    private ItemStack setupVaultItem() {
        ItemStack vaultItem = new ItemStack(Material.PAPER);
        ItemMeta iMeta = vaultItem.getItemMeta();
        List<String> lore = new ArrayList<>();
        iMeta.setDisplayName(Language.get("menus.companyPanel.vaultItem.name"));
        lore.addAll(Language.getList("menus.companyPanel.vaultItem.lore",
                "{money}", comp.getBalance(), "{init}", comp.getInitContribution()));
        iMeta.setLore(lore);
        vaultItem.setItemMeta(iMeta);
        return vaultItem;
    }

    private ItemStack setupLicItem() {
        ItemStack licItem = new ItemStack(Material.PAPER);
        ItemMeta iMeta = licItem.getItemMeta();
        List<String> lore = new ArrayList<>();

        iMeta.setDisplayName(ChatColor.YELLOW+
                StringUtils.center("Įmonės licencijos", 34));
        lore.add("--------------------------------------");
        lore.add("Turimų licencijų kiekis: " + comp.getLicences().size());
        lore.add("--------------------------------------");
        iMeta.setLore(lore);
        licItem.setItemMeta(iMeta);
        return licItem;
    }

    private ItemStack setupWageItem() {
        ItemStack wagesItem = new ItemStack(Material.PAPER);
        ItemMeta iMeta = wagesItem.getItemMeta();
        List<String> lore = new ArrayList<>();

        Double[] wageData = Companies.getInstance().compMng().calcWage(compId);

        iMeta.setDisplayName(Language.get("menus.companyPanel.wageItem.name"));
        lore.addAll(Language.getList("menus.companyPanel.wageItem.lore.ceo",
                "{wagesum}", wageData[0], "{gpm}", wageData[1], "{psd}", wageData[2],
                "{total}", wageData[0] - wageData[1] - wageData[2]));

        iMeta.setLore(lore);
        wagesItem.setItemMeta(iMeta);
        return wagesItem;
    }

    private ItemStack setupWageItemEmp() {
        ItemStack wagesItem = new ItemStack(Material.PAPER);
        ItemMeta iMeta = wagesItem.getItemMeta();
        List<String> lore = new ArrayList<>();
        Double[] wageData = Companies.getInstance().getFH().getEmpMng().getWage(employee.getId().getEmployeeId(), compId);
        iMeta.setDisplayName(Language.get("menus.companyPanel.wageItem.name"));
        lore.addAll(Language.getList("menus.companyPanel.wageItem.lore.ceo",
                "{neto}", wageData[0], "{gpm}", wageData[1], "{psd}", wageData[2],
                "{bruto}", wageData[0] - wageData[1] - wageData[2]));

        iMeta.setLore(lore);
        wagesItem.setItemMeta(iMeta);
        return wagesItem;
    }

    private ItemStack quitItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta iMeta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        iMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+
                StringUtils.center("PALIKTI ĮMONĘ", 34));
        lore.add("--------------------------------------");
        lore.add("Veiksmas negrįžtamas: CTRL+Q");
        lore.add("--------------------------------------");

        iMeta.setLore(lore);
        item.setItemMeta(iMeta);
        return item;
    }
}
