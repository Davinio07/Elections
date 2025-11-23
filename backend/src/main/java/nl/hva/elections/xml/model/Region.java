package nl.hva.elections.xml.model;

public class Region {
    private String id;
    private String name;
    private String category;
    private String superiorCategory;
    private String superiorRegionNumber; // <--- ADD THIS FIELD

    public Region(String id, String name, String category, String superiorCategory, String superiorRegionNumber) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.superiorCategory = superiorCategory;
        this.superiorRegionNumber = superiorRegionNumber;
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

    public String getSuperiorRegionNumber() { return superiorRegionNumber; } // <--- ADD GETTER
    public void setSuperiorRegionNumber(String superiorRegionNumber) { this.superiorRegionNumber = superiorRegionNumber; }
}