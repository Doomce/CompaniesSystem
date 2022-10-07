package net.dom.companies.database;

import net.dom.companies.database.CompaniesEmployees;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "comp_employees")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Employee implements Serializable {

    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "uuid", nullable = false, columnDefinition = "CHAR(38)")
    private UUID uuid;

    @OneToMany(mappedBy = "employee")
    private Set<CompaniesEmployees> employees = new HashSet<>();

    @Column(name = "wage_sum")
    private double allWages; //TODO Algu suma (visu laiku)

    @Column(name = "taxes_paid")
    private double taxesPaid; //TODO GPM imokos

    @Column(name = "insurance_paid")
    private double insurancePaid; //TODO Sveikatos draudimo imokos

    @Column(name = "support_got")
    private double gotSupport; //TODO Pasalpa

    @Column(name = "debt")
    @Convert(converter = debtConverter.class)
    private List<Double> debt; //TODO skolos/permokos

    @Column(name = "stats")
    private String stats; //TODO: JSON

    @Column(name = "comp_history")
    private String compHistory; //TODO: JSON

    public Set<CompaniesEmployees> getCE() {
        return employees;
    }

    public Employee() {};

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public double getAllWages() {
        return allWages;
    }

    public void setAllWages(double allWages) {
        this.allWages = allWages;
    }

    public double getTaxesPaid() {
        return taxesPaid;
    }

    public void setTaxesPaid(double taxesPaid) {
        this.taxesPaid = taxesPaid;
    }

    public double getInsurancePaid() {
        return insurancePaid;
    }

    public void setInsurancePaid(double insurancePaid) {
        this.insurancePaid = insurancePaid;
    }

    public double getGotSupport() {
        return gotSupport;
    }

    public void setGotSupport(double gotSupport) {
        this.gotSupport = gotSupport;
    }

    public List<Double> getDebt() {
        return debt;
    }

    public void setDebt(List<Double> debt) {
        this.debt = debt;
    }

    public String getStats() {
        return stats;
    }

    public void setStats(String stats) {
        this.stats = stats;
    }

    public String getCompHistory() {
        return compHistory;
    }

    public void setCompHistory(String compHistory) {
        this.compHistory = compHistory;
    }
}
