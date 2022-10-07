package net.dom.companies.database;

import net.dom.companies.objects.duty;
import org.bukkit.Location;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "comp_companies")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Company implements Serializable {

    @Id
    @Column(name = "company_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long compId;

    @OneToMany(mappedBy = "company")
    private Set<CompaniesEmployees> cE = new HashSet<>();

    @Column(name = "name")
    String name;

    @Column(name = "level")
    Integer level = 0;

    @Column(name = "sales")
    Double sales;

    @Column(name = "tag")
    String tag;

    @Column(name = "licences")
    @Convert(converter = licenceConverter.class)
    List<Integer> licences;

    @Column(name = "home_loc")
    @Convert(converter = locationConverter.class)
    Location loc;

    @Column(name = "balance")
    Double balance;

    @Column(name = "init_contribution", precision = 10, scale = 2)
    Double initContribution;

    @CreationTimestamp
    @Column(name = "creation_date")
    Timestamp creationDate;

    @Column(name = "preferences")
    String prefs; // OR STATS ----- > >>>>> JSON

    public Company() {}

    public Long getCompId() {
        return compId;
    }

    public void setCompId(Long compId) {
        this.compId = compId;
    }

    public Set<CompaniesEmployees> getCE() {
        return cE;
    }

    public CompaniesEmployees getOwner() {
        for (CompaniesEmployees emp : cE) {
            if (emp.getDuties().equals(duty.OWNER)) return emp;
        }
        return null;
    }

    public CompaniesEmployees getCoOwner() {
        for (CompaniesEmployees emp : cE) {
            if (emp.getDuties().equals(duty.CO_OWNER)) return emp;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /*public Long getBankIban() {
        return bankIban;
    }

    public void setBankIban(Long bankIban) {
        this.bankIban = bankIban;
    }*/

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getPrefs() {
        return prefs;
    }

    public void setPrefs(String prefs) {
        this.prefs = prefs;
    }

    public void setInitContribution(Double initContribution) {
        this.initContribution = initContribution;
    }

    public Double getInitContribution() {
        return initContribution;
    }

    public List<Integer> getLicences() {
        return licences;
    }

    public void addLicence(int licence) {
        this.licences.add(licence);
    }

    public void removeLicence(int licence) {
        for (int i = 0; i < this.licences.size(); i++) {
            if (licence == this.licences.get(i)) {
                this.licences.remove(i);
                return;
            }
        }
    }

}
