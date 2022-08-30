package net.dom.companies.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.dom.companies.Companies;
import net.dom.companies.database.CompaniesEmployees;
import net.dom.companies.database.Company;
import net.dom.companies.functions.compManagementAssist;
import net.dom.companies.functions.companyManager;
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

    private final Language lang;

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

        this.lang = Companies.getInstance().getLang();
    }

    private void cSetup() {
        gui.setItem(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8 , 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35),
                ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text("")).asGuiItem());

        gui.setItem(2, 2, new GuiItem(setupInfoItem(), event -> assist.action(player)));
        gui.setItem(2, 4, new GuiItem(setupEmpItem(), (event -> {
            if (!employee.getDuties().getPermission(3)) return;
            companyManager.openCompanyEmpPanel(player, compId);
        })));
        gui.setItem(2, 6, new GuiItem(setupVaultItem(), (event -> {
            //TODO BANKAS;
        })));
        gui.setItem(2, 8, new GuiItem(setupLicItem(), (event -> {
            if (!employee.getDuties().getPermission(5)) return;
            companyManager.openLicGui(player, compId);
        })));
        gui.setItem(3, 2, new GuiItem(setupWageItem(), (event -> {
            //TODO PAYOUT SALARY BUTTONS
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
                comp.getBusinessForm().toString() + " " + comp.getName()));
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

        iMeta.setDisplayName(ChatColor.YELLOW+
                StringUtils.center("Įmonės kapitalas", 34));
        lore.add("--------------------------------------");
        lore.add("Įstatinis kapitalas: "+comp.getInitContribution());
        lore.add("Banko sąskaitos balansas: ");
        lore.add("Daiktų įmonės saugykloje: ");
        lore.add("--------------------------------------");
        //TODO BASE64 VAULT
        //TODO BANK API

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

        int wageEmployees = 0;
        double totalWages = 0;
        double tax = 0;
        for (CompaniesEmployees emp : comp.getCE()) {
            if (emp.getSalary() != null) {
                totalWages += emp.getSalary();
                tax += emp.getSalary()*0.1;
                wageEmployees++;
            }
        }

        iMeta.setDisplayName(ChatColor.YELLOW+
                StringUtils.center("Buhalterija", 34));
        lore.add("--------------------------------------");
        lore.add("Bendra algų suma: "+totalWages);
        lore.add("Mokesčiai: "+tax);
        lore.add("Išmokama "+wageEmployees+" darbuotojams.");
        lore.add("Automatiškas algų išmokėjimas po "); //TODO SETUP WAGE TIMER
        lore.add("--------------------------------------");

        iMeta.setLore(lore);
        wagesItem.setItemMeta(iMeta);
        return wagesItem;
    }

    private ItemStack setupWageItemEmp() {
        ItemStack wagesItem = new ItemStack(Material.PAPER);
        ItemMeta iMeta = wagesItem.getItemMeta();
        List<String> lore = new ArrayList<>();

        iMeta.setDisplayName(ChatColor.YELLOW+
                StringUtils.center("Darbo užmokestis", 34));
        lore.add("--------------------------------------");
        lore.add("Jūsų alga: "+employee.getSalary());
        lore.add("Algos išmokėjimas: "); //TODO SETUP WAGE TIMER
        lore.add("--------------------------------------");

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
