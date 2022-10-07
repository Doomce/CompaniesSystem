package net.dom.companies.functions;

import net.dom.companies.Companies;
import net.dom.companies.database.CompaniesEmployees;
import net.dom.companies.database.Company;
import net.dom.companies.database.Employee;
import net.dom.companies.database.hibernateUtil;
import net.dom.companies.economy.Eco;
import net.dom.companies.objects.duty;
import org.bukkit.Bukkit;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;

public class companyManager {

    private final Companies plugin;

    public companyManager(Companies pl) {
        plugin = pl;
    }

    public void createCompany(UUID uuid, Map<Object, Object> data) {
        double init = (double) data.get("init");
        String name = (String) data.get("name");

        if (init < Eco.INIT_CONTRIBUTION.cost()) return;
        if (name == null || name.isEmpty()) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Session session  = hibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();

            Company comp = new Company();
            comp.setName(name);
            comp.setInitContribution(init);
            session.save(comp);

            plugin.getLogger().log(Level.INFO, "Company created (id: "+comp.getCompId()+")");

            if (uuid != null) {
                Employee ceo = session.load(Employee.class, uuid);
                if (ceo == null) {
                    ceo = new Employee();
                    ceo.setUuid(uuid);
                    session.save(ceo);
                }

                CompaniesEmployees Ce = new CompaniesEmployees();
                Ce.setCompany(comp);
                Ce.setEmployee(ceo);
                Ce.setDuties(duty.OWNER);
                Ce.setWorkingFrom(LocalDate.now());
                session.save(Ce);

            }
            tx.commit();
            session.close();
        });
    }

    /***
     * Returns Company payout taxes
     * @param compId A company ID
     * @return Masyvas: 0- Algu suma; 1- GPMAS; 2- PSD;
     */
    public Double[] calcWage(Long compId) {
        Double[] wageData = (Double[]) List.of(0.00,0.00,0.00).toArray();
        Session session  = hibernateUtil.getSessionFactory().openSession();
        Company comp = session.load(Company.class, compId);

        for (CompaniesEmployees emp : comp.getCE()) {
            Double[] empData = Eco.calcWageTax(emp.getSalary());
            wageData[0] += empData[0];
            wageData[1] += empData[1];
            wageData[2] += empData[2];
        }

        session.close();
        return wageData;
    }


    int getMaxWorkers() {
        //TODO MAX WORKERS;
        return 8;
    }

}
