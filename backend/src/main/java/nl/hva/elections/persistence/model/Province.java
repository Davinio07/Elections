package nl.hva.elections.persistence.model;

import com.fasterxml.jackson.annotation.JsonManagedReference; // <--- Import this
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROVINCE")
@XmlRootElement(name = "Province")
@XmlAccessorType(XmlAccessType.FIELD)
public class Province {

    @Id
    @Column(name = "province_id")
    private Integer province_id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // --- RELATIONSHIP ---
    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // <--- ADD THIS ANNOTATION
    private List<Kieskring> kieskringen = new ArrayList<>();
    // --- END RELATIONSHIP ---

    public Province() {
    }

    public Province(Integer province_id, String name) {
        this.province_id = province_id;
        this.name = name;
    }

    // ... Getters and Setters ...

    public Integer getProvince_id() { return province_id; }
    public void setProvince_id(Integer province_id) { this.province_id = province_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Kieskring> getKieskringen() { return kieskringen; }
    public void setKieskringen(List<Kieskring> kieskringen) { this.kieskringen = kieskringen; }

    @Override
    public String toString() {
        return "Province{province_id=" + province_id + ", name='" + name + '\'' + '}';
    }
}