package net.dom.companies.licences;

import net.dom.companies.Companies;
import net.dom.companies.database.CompaniesEmployees;
import net.dom.companies.database.CompaniesEmployeesKeys;
import net.dom.companies.database.Company;
import net.dom.companies.database.hibernateUtil;
import net.dom.companies.menus.compLicMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.Predicate;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class LicHandler {

    private static HashMap<Integer, Licence> loadedLicences = new HashMap<>();

    Companies plugin;

    public LicHandler(Companies pl) {
        this.plugin = pl;
        initializeLicences();
    }

    /*
    YAML STRUCTURE:

    001:
        name: "Statybu leidimas"
        cost: 23000
        sellCost: 12000
        minLevel: 0
        costInflation: true
        requirements:
            - 002
            - 003
        description:
            - ""
            - ""
        material: "BRICKS"
    002:
        name: "Automatinis algų pervedimas"
        cost: 23000
        sellCost: 12000
        minLevel: 3
        costInflation: true
        requirements: []
        description:
            - ""
            - ""
        material: "PAPER"

    003:
        name: "ChestShop(R) integracija"
        cost: 23000
        sellCost: 12000
        minLevel: 3
        costInflation: true
        requirements: []
        description:
            - ""
            - ""
        material: "CHEST"

    004:
        name: "Padidintas pelno limitas"
        cost: 23000
        sellCost: 12000
        minLevel: 3
        costInflation: true
        requirements: []
        description:
            - ""
            - ""
        material: "GOLD_INGOT"

    005:
        name: "Paramos gavėjo statusas"
        cost: 23000
        sellCost: 12000
        minLevel: 3
        costInflation: true
        requirements: []
        description:
            - ""
            - ""
        material: "PAPER"

    */


    private void initializeLicences() {
        File licFile = new File(plugin.getDataFolder(), "licences.yml");
        if (!licFile.exists()) {
            plugin.saveResource("licences.yml", false);
        }
        YamlConfiguration licConfig = YamlConfiguration.loadConfiguration(licFile);

        licConfig.getConfigurationSection("").getKeys(false).forEach(s -> {
            String name = licConfig.getString(s+".name");
            double cost = licConfig.getDouble(s+".cost");
            double sCost = licConfig.getDouble(s+".sellCost");
            int lvl = licConfig.getInt(s+".minLevel");
            boolean costInflation = licConfig.getBoolean(s+".costInflation"); //TODO FUTURE
            List<Integer> req = licConfig.getIntegerList(s+".requirements");
            List<String> desc = licConfig.getStringList(s+".description");
            Material material = Material.getMaterial(
                    Objects.requireNonNullElse(licConfig.getString(s + ".material"), "PAPER"));

            Licence lic = new Licence(name, cost, sCost, lvl, material);
            lic.setDescription(desc);
            lic.setRequirements(req);

            loadedLicences.put(Integer.parseInt(s), lic);
        });
    }

    private List<Licence> getOwnedCompanyLicences(Company comp) {
        List<Licence> ownedLic = new ArrayList<>();
        if (!comp.getLicences().isEmpty()) {
            for (Integer lic : comp.getLicences()) {
                ownedLic.add(loadedLicences.get(lic));
            }
        }
        return ownedLic;
    }

    /**
     *
     * @param comp a Company object
     * @return A list which contains additional licence data: isOwned? TODO: expiry (future)
     */

    public List<Licence.Properties> manageLicencesForComp(Company comp) {
        List<Licence.Properties> allLicences = new ArrayList<>();

        List<Integer> ownedLic;
        if (!comp.getLicences().isEmpty()) {
            ownedLic = comp.getLicences();
        } else {
            ownedLic = Collections.emptyList();
        }

        loadedLicences.forEach((key, value) -> {
            if (ownedLic.contains(key)) {
                allLicences.add(new Licence.Properties(value, key, true));
            } else {
                allLicences.add(new Licence.Properties(value, key, false));
            }
        });

        return allLicences;
    }


    private boolean isPassingRequirements(Licence lic, List<Integer> owned) {
        if (lic.getRequirements().isEmpty()) return true;
        for (Integer id : lic.getRequirements()) {
            if (!owned.contains(id)) return false;
        }
        return true;
    }

    public void sellLic(Player p, long compId, int licId) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            Company company = session.load(Company.class, compId);

            List<Licence.Properties> compLicences = manageLicencesForComp(company);
            Licence.Properties selectedLic = compLicences.stream().filter((lic) -> lic.id == licId).findAny().get();

            CompaniesEmployees compEmp =
                    session.load(CompaniesEmployees.class, new CompaniesEmployeesKeys(p.getUniqueId(), compId));
            if (!compEmp.getDuties().getPermission(6)) {
                p.sendMessage("Neturi teisės pardavinėti licencijų.");
                return;
            }

            if (!selectedLic.isOwned) {
                p.sendMessage("Neturi sios licencijos.");
                return;
            }
            //TODO ECO CHECK;
            // ECO+++ COST

            Transaction tx = session.beginTransaction();
            company.removeLicence(licId);
            tx.commit();

            compLicMenu gui = new compLicMenu(p, compId, manageLicencesForComp(company));
            Bukkit.getScheduler().runTask(plugin, gui::open);

            session.close();
            p.sendMessage("Licencija parduota.");
        });
    }

    public void purchaseLic(Player p, long compId, int licId) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Session session = hibernateUtil.getSessionFactory().openSession();
            Company company = session.load(Company.class, compId);

            CompaniesEmployees compEmp =
                    session.load(CompaniesEmployees.class, new CompaniesEmployeesKeys(p.getUniqueId(), compId));
            if (!compEmp.getDuties().getPermission(7)) {
                p.sendMessage("Neturi teisės pirkti licencijų.");
                return;
            }

            List<Licence.Properties> compLicences = manageLicencesForComp(company);
            Licence.Properties selectedLic = compLicences.stream().filter((lic) -> lic.id == licId).findAny().get();
            if (selectedLic.isOwned) {
                p.sendMessage("Jau turi šią licenciją.");
                return;
            }

            List<Integer> ownedLicIds = compLicences.stream().filter((lic) -> lic.isOwned)
                    .map(Licence.Properties::getId).collect(Collectors.toList());

            if (!isPassingRequirements(selectedLic.getLic(), ownedLicIds)) {
                p.sendMessage("Neatitinka reikalavimu.");
                return;
            }
            //TODO ECO CHECK;
            // ECO --- COST

            Transaction tx = session.beginTransaction();
            company.addLicence(licId);
            tx.commit();

            compLicMenu gui = new compLicMenu(p, compId, manageLicencesForComp(company));
            Bukkit.getScheduler().runTask(plugin, gui::open);

            session.close();
            p.sendMessage("Nupirkai licencija: "+selectedLic.getLic().getName());
        });
    }


}
