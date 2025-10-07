package XMLParser.XMLParser.src.main.java.nl.hva.ict.sm3.backend.model;

public class Region {
    private String id;
    private String name;
    private String category;
    private String superiorCategory;

    public Region(String id, String name, String category, String superiorCategory) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.superiorCategory = superiorCategory;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSuperiorCategory() { return superiorCategory; }
    public void setSuperiorCategory(String superiorCategory) { this.superiorCategory = superiorCategory; }
}
