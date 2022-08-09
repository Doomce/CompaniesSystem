package net.dom.companies.functions;

import net.dom.companies.Companies;
import net.dom.companies.database.Company;
import net.dom.companies.database.hibernateUtil;
import net.dom.companies.licences.*;
import net.dom.companies.menus.compLicMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.*;

public class licencesManager {

    private Companies comp;

    public licencesManager(Companies pl) {
        this.comp = pl;
    }

    public List<Licence> setupCompLicences(Company comp) {
        List<Licence> loadedLicences = new ArrayList<>();
        loadedLicences.addAll(List.of(
                new buildingLic(),
                new autoWagePaymentPack(),
                new ProfitPack(),
                new chestShopAllowationPack(),
                new procurementIPack(),
                new procurementIIPack(),
                new supportPack(),
                new extensionIPack(),
                new extensionIIPack(),
                new extensionIIIPack()
        ));
        if (!comp.getLicences().isEmpty()) {
            for (Integer lic : comp.getLicences()) {
                loadedLicences.get(lic).setOwned();
            }
        }
        return loadedLicences;
    }

    private boolean isPassingRequirements(Licence lic, List<Licence> owned) {
        if (lic.getRequirements().isEmpty()) return true;
        int isHaving = 0;
        for (Integer id : lic.getRequirements()) {
            if (owned.get(id).isOwned()) isHaving++;
        }
        if (isHaving == lic.getRequirements().size()) return true;
        return false;
    }

    public void sellLic(Player p, int licId, long compId) {
        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            Company company = session.load(Company.class, compId);

            List<Licence> licList = setupCompLicences(company);
            Licence selectedLic = licList.get(licId);

            if (!selectedLic.isOwned()) {
                p.sendMessage("Neturi sios licencijos.");
                return;
            }
            //TODO ECO CHECK;
            // ECO+++ COST

            Transaction tx = session.beginTransaction();
            company.removeLicence(licId);
            tx.commit();

            compLicMenu gui = new compLicMenu(p, compId, setupCompLicences(company));
            Bukkit.getScheduler().runTask(comp, gui::open);

            session.close();
            p.sendMessage("Licencija parduota.");
        });
    }

    public void purchaseLic(Player p, int licId, long compId) {
        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            Company company = session.load(Company.class, compId);

            List<Licence> licList = setupCompLicences(company);
            Licence selectedLic = licList.get(licId);

            if (selectedLic.isOwned()) {
                p.sendMessage("Jau turi šią licenciją.");
                return;
            }
            if (!isPassingRequirements(selectedLic, licList)) {
                p.sendMessage("Neatitinka reikalavimu.");
                return;
            }
            if (selectedLic instanceof buildingLic) {
                if (licList.get(3).isOwned()) {
                    p.sendMessage("Statyba su pardavimais nesiderina....");
                    return;
                }
            }
            if (selectedLic instanceof chestShopAllowationPack) {
                if (licList.get(0).isOwned()) {
                    p.sendMessage("Statyba su pardavimais nesiderina....");
                    return;
                }
            }
            //TODO ECO CHECK;
            // ECO --- COST

            Transaction tx = session.beginTransaction();
            company.addLicence(licId);
            tx.commit();

            compLicMenu gui = new compLicMenu(p, compId, setupCompLicences(company));
            Bukkit.getScheduler().runTask(comp, gui::open);

            session.close();
            p.sendMessage("Nupirkai licencija: "+selectedLic.getName());
        });
    }


}
