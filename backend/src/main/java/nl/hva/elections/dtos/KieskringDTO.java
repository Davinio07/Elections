package nl.hva.elections.dtos;

public class KieskringDTO {

    private Integer id;
    private String name;
    private String provinceName;

    /**
     *
     * @param id
     * @param name
     * @param provinceName
     */
    public KieskringDTO(Integer id, String name, String provinceName) {
        this.id = id;
        this.name = name;
        this.provinceName = provinceName;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProvinceName() { return provinceName; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }
}