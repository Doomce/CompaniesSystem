package net.dom.companies.functions;

import net.dom.companies.Companies;
import net.dom.companies.database.CompaniesEmployees;
import net.dom.companies.database.Company;
import net.dom.companies.database.hibernateUtil;
import net.dom.companies.licences.Licence;
import net.dom.companies.menus.CompanyPanelMenu;
import net.dom.companies.menus.compEmpsMenu;
import net.dom.companies.menus.compLicMenu;
import net.dom.companies.prompts.EmpInvitationPrompt;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.hibernate.Session;

import java.util.HashMap;

import static net.dom.companies.functions.functionsHandler.plugin;

public class menuFunctions {

    public static void openCompanyMenu(Player p, Long compId) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            Company company = session.get(Company.class, compId);

            CompaniesEmployees employee = company.getCE().stream()
                    .filter((emp) -> emp.getId().getEmployeeId().equals(p.getUniqueId()))
                    .findAny()
                    .orElse(null);

            CompanyPanelMenu CP = new CompanyPanelMenu(p, company, employee);
            CP.setupInv();
            Bukkit.getScheduler().runTask(plugin, CP::open);

            session.close();
        });
    }


    public static void openCompanyEmpMenu(Player p, long compId) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            compEmpsMenu compEmpGui = new compEmpsMenu();
            compEmpGui.gui(p, compId, employeeManager.getCompanyEmployees(compId), false);

        });
    }

    public static void openLicMenu(Player p, Long compId) {
        Companies plugin = functionsHandler.plugin;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            Company company = session.get(Company.class, compId);

            compLicMenu gui = new compLicMenu(p, compId, plugin.getFH().getLicHandler().manageLicencesForComp(company));
            Bukkit.getScheduler().runTask(plugin, gui::open);

            session.close();
        });
    }

    public static void licAction(Player p, Long compId, Licence.Properties licProperty) {
        if (licProperty.isOwned) {
            plugin.getFH().getLicHandler().sellLic(p, compId, licProperty.id);
            return;
        }
        plugin.getFH().getLicHandler().purchaseLic(p, compId, licProperty.id);
    }

    public static void empAction(int action, Player p, long compId, CompaniesEmployees employee) {
        employeeManager empMng = plugin.getFH().empMng;
        switch (action) {
            case 1: {
                empMng.kick(p, compId, employee.getId().getEmployeeId(), false);
                break;
            }
            case 2: {
                empMng.demote(p, compId, employee, false);
                break;
            }
            case 3: {
                empMng.promote(p, compId, employee, false);
                break;
            }
            case 4: {
                empMng.wagePrompt(p, employee);
                break;
            }
        }
    }

    public static void invitationPrompt(Player p, Long compId) {
        if (!p.hasPermission("companies.invite")) return;
        p.closeInventory();

        HashMap<Object, Object> data = new HashMap<>();
        data.put("compId", compId);

        ConversationFactory factory = new ConversationFactory(plugin);
        factory.withFirstPrompt(new EmpInvitationPrompt(plugin)).withTimeout(25)
                .withEscapeSequence("atsaukti").withEscapeSequence("atšaukti").withInitialSessionData(data)
                .withLocalEcho(false).buildConversation(p).begin();
    }

    public static void payout(Long id) {
        plugin.getFH().getEmpMng().payOutWages(id);
    }

}
