package net.dom.companies.database;

import net.dom.companies.objects.duty;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "comp_comps_emps")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CompaniesEmployees {

    @EmbeddedId
    private CompaniesEmployeesKeys id = new CompaniesEmployeesKeys();

    @ManyToOne
    @MapsId("employeeId")
    private Employee employee;

    @ManyToOne
    @MapsId("companyId")
    private Company company;

    @Column(name = "salary")
    Double salary;

    @Column(name = "duties")
    @Enumerated(EnumType.STRING)
    duty duties;

    @Column(name = "started_work")
    LocalDate workingFrom;


    public CompaniesEmployeesKeys getId() {
        return id;
    }

    public void setId(CompaniesEmployeesKeys id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public duty getDuties() {
        return duties;
    }

    public void setDuties(duty duties) {
        this.duties = duties;
    }

    public LocalDate getWorkingFrom() {
        return workingFrom;
    }

    public void setWorkingFrom(LocalDate workingFrom) {
        this.workingFrom = workingFrom;
    }
}
