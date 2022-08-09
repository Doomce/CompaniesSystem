package net.dom.companies.database;

import net.dom.companies.database.CompaniesEmployees;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "comp_employees")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Employee implements Serializable {

    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "uuid", nullable = false, columnDefinition = "CHAR(38)")
    UUID uuid;

    @OneToMany(mappedBy = "employee")
    private Set<CompaniesEmployees> employees = new HashSet<>();

    @Column(name = "stats")
    String stats; //TODO: JSON


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

}
