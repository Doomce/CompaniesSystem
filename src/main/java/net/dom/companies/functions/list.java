package net.dom.companies.functions;

import net.dom.companies.Companies;
import net.dom.companies.database.CompaniesEmployees;
import net.dom.companies.database.hibernateUtil;
import net.dom.companies.database.Company;
import net.dom.companies.database.Employee;
import net.dom.companies.menus.compListMenu;
import net.dom.companies.objects.duty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class list {

    Companies comp;

    list(Companies plugin) {
        this.comp = plugin;
    }


    private void openGui(Player p, int compLimit, boolean isAdmin) {

        Bukkit.getScheduler().runTaskAsynchronously(comp, () -> {
            List<Company> compList = new ArrayList<>();
            List<duty> empDuties = new ArrayList<>();
            boolean allowCreate = true;
            Session session = hibernateUtil.getSessionFactory().openSession();

            if (isAdmin) {
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Company> criteriaQuery = criteriaBuilder.createQuery(Company.class);
                Root<Company> root = criteriaQuery.from(Company.class);
                criteriaQuery.select(root);
                compList.addAll(session.createQuery(criteriaQuery).setCacheable(false).getResultList());
            } else {
                Employee emp = session.get(Employee.class, p.getUniqueId());
                if (emp == null) {
                    p.sendMessage("Nesi registruotas tarp projekto darbuotojų.");
                    return;
                }
                if (emp.getCE().size() > compLimit) allowCreate = false;
                if (!p.hasPermission("companies.create")) allowCreate = false;
                for (CompaniesEmployees compEmp : emp.getCE()) {
                    compList.add(compEmp.getCompany());
                    empDuties.add(compEmp.getDuties());
                }

            }

            compListMenu cList = new compListMenu(p, compList, empDuties, allowCreate);
            Bukkit.getScheduler().runTask(comp, cList::open);

            session.close();
        });

    }

    public void runPlayer(Player p) {
        int maxCompanies = 1;
        //TODO PERMISSIONAI, IMONIU KURIMO LIMITAS
        if (!p.hasPermission("companies.list")) return;
//        while (true) {
//            if (!p.hasPermission("companies.has."+maxCompanies)) break;
//            else maxCompanies++;
//        }

        openGui(p, maxCompanies, false);
    }

    public void runAdmin(Player p) {
        if (!p.hasPermission("companies.admin")) return;
        openGui(p, 0, true);
    }

}
