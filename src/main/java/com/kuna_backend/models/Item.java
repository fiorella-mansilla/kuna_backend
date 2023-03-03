package com.kuna_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kuna_backend.enums.Category;
import com.kuna_backend.enums.Color;
import com.kuna_backend.enums.Size;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table (name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column (name = "name_item", length = 128, nullable = false)
    private String name;
    @Column (name = "desc_item", length = 2048, nullable = false)
    private String desc;
    @Column (name = "category", length = 64, nullable = false)
    @Enumerated (EnumType.STRING)
    private Category category;
    @Column (name = "size", length = 64, nullable = false)
    @Enumerated (EnumType.STRING)
    private Size size;
    @Column (name = "color", length = 64, nullable = false)
    @Enumerated (EnumType.STRING)
    private Color color;
    @Column (name = "price", nullable = false)
    private Float price = 0.0f;
    @Column (name = "discount", nullable = false)
    private Float discount = 0.0f;
    @Column (name = "sku", length = 128, nullable = false)
    private String SKU;
    @Column (name = "quantity_stock", nullable = false)
    private Integer quantityStock = 0;
    @Column (name = "image_path", length = 256, nullable = false)
    private String imagePath;
    @Column (name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column (name = "modified_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime modifiedAt;
    @Column (name = "deleted_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime deletedAt;

    @ManyToMany (fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable (
            name = "order_item",
            joinColumns = {
                    @JoinColumn (name = "item_id")
            },
            inverseJoinColumns = {
                    @JoinColumn (name = "order_id")
            }
    )
    @JsonIgnore
    private Set<Order> orders = new HashSet<Order>();

    public Item() {
    }

    public Item(Integer id, String name, String desc, Category category, Size size, Color color, Float price, Float discount, String SKU, Integer quantityStock, String imagePath, LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt, Set<Order> orders) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.category = category;
        this.size = size;
        this.color = color;
        this.price = price;
        this.discount = discount;
        this.SKU = SKU;
        this.quantityStock = quantityStock;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.deletedAt = deletedAt;
        this.orders = orders;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public Integer getQuantityStock() {
        return quantityStock;
    }

    public void setQuantityStock(Integer quantityStock) {
        this.quantityStock = quantityStock;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }
}

