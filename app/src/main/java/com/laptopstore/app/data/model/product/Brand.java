package com.laptopstore.app.data.model.product;

import com.google.gson.annotations.SerializedName;

public class Brand {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    @SerializedName("image")
    private String image;

    @SerializedName("description")
    private String description;

    @SerializedName("status")
    private boolean active;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setActive(boolean active) { this.active = active; }
}
