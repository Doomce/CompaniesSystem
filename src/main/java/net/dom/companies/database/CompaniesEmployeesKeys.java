package net.dom.companies.database;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CompaniesEmployeesKeys implements Serializable {

    private static final long serialVersionUID = 1L;

    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(columnDefinition = "CHAR(38)")
    private UUID employeeId;

    private Long companyId;

    public CompaniesEmployeesKeys(){}

    public CompaniesEmployeesKeys(UUID employeeId, Long companyId){}

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompaniesEmployeesKeys that = (CompaniesEmployeesKeys) o;
        return Objects.equals(employeeId, that.employeeId) && Objects.equals(companyId, that.companyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, companyId);
    }
}
