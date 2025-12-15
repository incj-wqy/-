package com.mall.model;

public class Category {
    private int id;
    private String name;
    private int parentId;
    private int sort;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }

    public int getSort() { return sort; }
    public void setSort(int sort) { this.sort = sort; }
}
