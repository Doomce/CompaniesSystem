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
    private Double salary;

    @Column(name = "duties")
    @Enumerated(EnumType.STRING)
    private duty duties;

    @Column(name = "started_work")
    private LocalDate workingFrom;

    @Column(name = "is_shareholder")
    private boolean isShareholder;

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

    public boolean isShareholder() {
        return isShareholder;
    }

    public void setShareholder(boolean shareholder) {
        isShareholder = shareholder;
    }
}
