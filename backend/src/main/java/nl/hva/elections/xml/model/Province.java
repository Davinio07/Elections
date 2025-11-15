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
@Table(name = "PROVINCE")
@XmlRootElement(name = "Province")
@XmlAccessorType(XmlAccessType.FIELD)
public class Province {

    @Id // Marks this field as the primary key
    @Column(name = "province_id")
    private Integer province_id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * JPA requires a no-argument constructor.
     */
    public Province() {
    }

    /**
     * Constructor used for seeding the data.
     * @param province_id The manual ID (1-12)
     * @param name The name of the province
     */
    public Province(Integer province_id, String name) {
        this.province_id = province_id;
        this.name = name;
    }

    // Getters and Setters
    // TODO: Split the getters and setters into a seperate file

    public Integer getProvince_id() {
        return province_id;
    }

    public void setProvince_id(Integer province_id) {
        this.province_id = province_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // toString

    @Override
    public String toString() {
        return "Province{" +
                "province_id=" + province_id +
                ", name='" + name + '\'' +
                '}';
    }
}