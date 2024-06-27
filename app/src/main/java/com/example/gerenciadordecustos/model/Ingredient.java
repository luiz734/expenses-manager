package com.example.gerenciadordecustos.model;

public class Ingredient {
    public long id;
    public String name;
    public double cost;
    public double amount;
    public long user_id;

    public Ingredient(long id, String name, double cost, double amount, long user_id) {
        this.user_id = user_id;
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.amount = amount;
    }
    @Override
    public String toString() {
        return name;
    }

}
