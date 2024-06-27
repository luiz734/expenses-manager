package com.example.gerenciadordecustos.model;

public class ProductIngredient {
    public long product_id, ingredient_id;
    public double amount;


    public ProductIngredient(long product_id, long ingredient_id, double amount) {
        this.product_id = product_id;
        this.ingredient_id = ingredient_id;
        this.amount = amount;
    }
}
