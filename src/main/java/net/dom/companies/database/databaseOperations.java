package net.dom.companies.database;

import net.dom.companies.Companies;
import net.dom.companies.provisions.businessForms;
import net.dom.companies.objects.duty;
import org.bukkit.Bukkit;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;

public class databaseOperations {
    private final Companies plugin;

    public databaseOperations(Companies pl) {
        this.plugin = pl;
        this.setup();
    }


    public void createCompany(UUID uuid, Map<Object, Object> data) {
        int scope = (int) data.get("scope");
        int form = (int) data.get("form");
        double init = (double) data.get("init");
        String name = (String) data.get("name");
        businessForms bf = businessForms.values()[form];

        if (init < bf.getClassByName().initContribution()) return;
        if (name == null || name.isEmpty()) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Session session  = hibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();

            Company comp = new Company();

            comp.setName(name);
            comp.setBusinessForm(bf);
            comp.setInitContribution(init);

            session.save(comp);

            plugin.getLogger().log(Level.INFO, "Company created (id: "+comp.getCompId()+")");

            if (uuid != null) {
                Employee ceo = new Employee();
                ceo.setUuid(uuid);
                session.save(ceo);

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

    public Long getNonPlayerIban(String name) {
        Session session  = hibernateUtil.getSessionFactory().openSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Company> root = criteriaQuery.from(Company.class);
        criteriaQuery.select(root.get("IBAN"));
        criteriaQuery.where(criteriaBuilder.like(root.get("settings"), "%\"Name\": \""+name+"\"%"));
        try {
            return session.createQuery(criteriaQuery).setCacheable(false).getSingleResult();
        } catch (NoResultException ignore) {
            return null;
        }

    }


    private void setup() {
        Session session = hibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        String SQL = "CREATE TABLE IF NOT EXISTS `comp_comps_emps` (`started_work` datetime)";
        session.createNativeQuery(SQL).setCacheable(false).executeUpdate();
        SQL = "CREATE TABLE IF NOT EXISTS `comp_companies` (`creation_date` datetime)";
        session.createNativeQuery(SQL).setCacheable(false).executeUpdate();
        tx.commit();
        session.getSessionFactory().getCache().evictEntityData(Company.class);
        session.close();


    }
}

