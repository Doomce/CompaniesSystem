package net.dom.companies.database;

import net.dom.companies.Companies;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class databaseOperations {

    public databaseOperations(Companies pl) {
        this.setup();
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

