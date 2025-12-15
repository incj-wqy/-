package com.mall.model;

public class Product {
    private int id;
    private String name;
    private int categoryId;
    private double price;
    private int stock;
    private int status;
    private String image;
    private String images;
    private String description;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
