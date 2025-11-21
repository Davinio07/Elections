package nl.hva.elections.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "GEMEENTE")
public class Gemeente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gemeente_id")
    private Integer id;

    @Column(name = "name", unique = true)
    private String name;

    // Relationship to Kieskring
    @ManyToOne
    @JoinColumn(name = "kieskring_id")
    @JsonBackReference
    private Kieskring kieskring;

    // Relationship to Province (can be inferred from Kieskring, but useful to have)
    @ManyToOne
    @JoinColumn(name = "province_id")
    @JsonBackReference
    private Province province;

    public Gemeente() {
    }

    public Gemeente(String name) {
        this.name = name;
    }

    // --- Getters and Setters ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Kieskring getKieskring() { return kieskring; }
    public void setKieskring(Kieskring kieskring) { this.kieskring = kieskring; }

    public Province getProvince() { return province; }
    public void setProvince(Province province) { this.province = province; }
}