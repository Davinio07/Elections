package nl.hva.elections.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Objects;

@Entity
@Table(name = "KIESKRING")
@XmlRootElement(name = "Kieskring")
@XmlAccessorType(XmlAccessType.FIELD)
public class Kieskring {

    @Id
    @Column(name = "kieskring_id")
    private Integer kieskring_id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * JPA requires a no-argument constructor.
     */
    public Kieskring() {
    }

    /**
     * Constructor used for seeding the data.
     * @param kieskring_id The manual ID
     * @param name The name of the kieskring
     */
    public Kieskring(Integer kieskring_id, String name) {
        this.kieskring_id = kieskring_id;
        this.name = name;
    }

    // Getters and Setters

    public Integer getKieskring_id() {
        return kieskring_id;
    }

    public void setKieskring_id(Integer kieskring_id) {
        this.kieskring_id = kieskring_id;
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
        return "Kieskring{" +
                "kieskring_id=" + kieskring_id +
                ", name='" + name + '\'' +
                '}';
    }
}