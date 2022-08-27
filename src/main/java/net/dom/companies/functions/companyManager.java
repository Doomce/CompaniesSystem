package net.dom.companies.functions;

import net.dom.companies.Companies;
import net.dom.companies.Utils.ItemSerialization;
import net.dom.companies.database.*;
import net.dom.companies.licences.LicHandler;
import net.dom.companies.licences.Licence;
import net.dom.companies.menus.compEmpsMenu;
import net.dom.companies.menus.compLicMenu;
import net.dom.companies.menus.CompanyPanelMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.hibernate.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class companyManager {

    private static Companies comp;

    public companyManager(Companies plugin) {
        this.comp = plugin;
    }

    public static void openCompanyPanel(Player p, Long compId) {
        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            Company company = session.get(Company.class, compId);

            CompaniesEmployees employee = company.getCE().stream()
                    .filter((emp) -> emp.getId().getEmployeeId().equals(p.getUniqueId()))
                    .findAny()
                    .orElse(null);

            CompanyPanelMenu CP = new CompanyPanelMenu(p, company, employee);
            CP.setupInv();
            Bukkit.getScheduler().runTask(comp, CP::open);

            session.close();
        });
    }

    public static List<CompaniesEmployees> getCompanyEmployees(Long compId) {
        List<CompaniesEmployees> employees = new ArrayList<>();
        Session session = hibernateUtil.getSessionFactory().openSession();
        Company company = session.get(Company.class, compId);
        employees.addAll(company.getCE());
        employees.sort(Comparator.comparingInt((CE) -> CE.getDuties().ordinal()));
        session.close();
        return employees;
    }

    public static void openCompanyEmpPanel(Player p, long compId) {
        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
            compEmpsMenu compEmpGui = new compEmpsMenu();
            compEmpGui.gui(p, compId, getCompanyEmployees(compId), false);

        });
    }


    public static void invTest(Player p) throws IOException {
        String ss = ItemSerialization.itemStackArrayToBase64(p.getInventory().getContents());

        Inventory gui = Bukkit.createInventory(null, 54);

        p.sendMessage(ItemSerialization.itemStackArrayFromBase64(ss).length+"");
        for (ItemStack IS : ItemSerialization.itemStackArrayFromBase64(ss)) {
            if (IS == null) continue;
            gui.addItem(IS);
        }

        p.openInventory(gui);
    }

    public static void openLicGui(Player p, Long compId) {
        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            Company company = session.get(Company.class, compId);

            compLicMenu gui = new compLicMenu(p, compId, comp.getFuncMng().getLicHandler().manageLicencesForComp(company));
            Bukkit.getScheduler().runTask(comp, gui::open);

            session.close();
        });
    }

    public static void licAction(Player p, Long compId, Licence.Properties licProperty) {
        if (licProperty.isOwned) {
            comp.getFuncMng().getLicHandler().sellLic(p, compId, licProperty.id);
            return;
        }
        comp.getFuncMng().getLicHandler().purchaseLic(p, compId, licProperty.id);
    }

    static int getMaxWorkers(Company company) {
        int workers = company.getBusinessForm().getClassByName().getMaxWorkers();
        return workers;
    }

}
