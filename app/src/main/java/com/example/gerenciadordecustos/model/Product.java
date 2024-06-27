package com.example.gerenciadordecustos.model;

public class Product {
    public long id;
    public String name;
    public double price;
    public long user_id;

    public Product(long id, String name, double price, long user_id) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.user_id = user_id;
    }

}
