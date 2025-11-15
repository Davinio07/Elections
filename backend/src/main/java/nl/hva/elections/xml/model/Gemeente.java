package nl.hva.elections.xml.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Objects;

@Entity
@Table(name = "GEMEENTE")
@XmlRootElement(name = "Gemeente")
@XmlAccessorType(XmlAccessType.FIELD)
public class Gemeente {

    @Id // primary key
    @Column(name = "gemeente_id")
    private Integer gemeente_id;

    @Column(name = "name")
    private String name;

    // foreign key to the KIESKRING table
    // For a more advanced setup, you could use @ManyToOne with a Kieskring entity
    @Column(name = "kieskring_id")
    private Integer kieskring_id;

    // This is the foreign key to the PROVINCE table
    @Column(name = "province_id")
    private Integer province_id;

    /**
     * JPA requires a no-argument constructor. (dont forget vro)
     */
    public Gemeente() {
    }

    public Gemeente(String name, Integer gemeente_id, Integer kieskring_id, Integer province_id) {
        this.name = name;
        this.gemeente_id = gemeente_id;
        this.kieskring_id = kieskring_id;
        this.province_id = province_id;
    }

    // Getters and Setters
    // TODO: Split the getters and setters into a seperate file

    public Integer getGemeente_id() {
        return gemeente_id;
    }

    public void setGemeente_id(Integer gemeente_id) {
        this.gemeente_id = gemeente_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getKieskring_id() {
        return kieskring_id;
    }

    public void setKieskring_id(Integer kieskring_id) {
        this.kieskring_id = kieskring_id;
    }

    public Integer getProvince_id() {
        return province_id;
    }

    public void setProvince_id(Integer province_id) {
        this.province_id = province_id;
    }


    // ToString

    @Override
    public String toString() {
        return "Gemeente{" +
                "gemeente_id=" + gemeente_id +
                ", name='" + name + '\'' +
                ", kieskring_id=" + kieskring_id +
                ", province_id=" + province_id +
                '}';
    }
}