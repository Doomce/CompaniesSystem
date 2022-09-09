package net.dom.companies.database;

import net.dom.companies.objects.duty;
import org.bukkit.Location;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "comp_contracts")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Contract implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "offer")
    private Company offer;

    @Column(name = "buyer_comp")
    private Company buyer;

    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "buyer_uuid", columnDefinition = "CHAR(38)")
    private UUID uuid;

    @Column(name = "price", precision = 10, scale = 2)
    private Double price;

    @Column(name = "description")
    private String desc;

    @Column(name = "state")
    private int state;//state: waiting signature, in progress, waiting money, completed, not done, rejected.

    @Column(name = "bank_iban")
    Long bankIban;

    @Column(name = "init_contribution", precision = 10, scale = 2)
    Double initContribution;

    @CreationTimestamp
    @Column(name = "creation_date")
    Timestamp creationDate;

    @Column(name = "deadline_date")
    Timestamp deadlineDate;

    @Column(name = "other_contracts")
    String other; // OR STATS ----- > >>>>> JSON

    public Contract() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getOffer() {
        return offer;
    }

    public void setOffer(Company offer) {
        this.offer = offer;
    }

    public Company getBuyer() {
        return buyer;
    }

    public void setBuyer(Company buyer) {
        this.buyer = buyer;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Long getBankIban() {
        return bankIban;
    }

    public void setBankIban(Long bankIban) {
        this.bankIban = bankIban;
    }

    public Double getInitContribution() {
        return initContribution;
    }

    public void setInitContribution(Double initContribution) {
        this.initContribution = initContribution;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Timestamp deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
