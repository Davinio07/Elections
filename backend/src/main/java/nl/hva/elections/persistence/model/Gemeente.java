package nl.hva.elections.persistence.model;

import java.util.Objects;

public class Gemeente {
    private String name;
    private Integer gemeente_id;
    private Integer kieskring_id;
    private Integer province_id;

    public Gemeente(String name, Integer gemeente_id, Integer kieskring_id, Integer province_id) {
        this.name = name;
        this.gemeente_id = gemeente_id;
        this.kieskring_id = kieskring_id;
        this.province_id = province_id;
    }
}


