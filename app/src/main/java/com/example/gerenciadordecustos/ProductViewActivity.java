package com.example.gerenciadordecustos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciadordecustos.model.DatabaseHelper;
import com.example.gerenciadordecustos.model.Ingredient;
import com.example.gerenciadordecustos.model.ProductIngredient;

import java.util.ArrayList;
import java.util.List;

public class ProductViewActivity extends AppCompatActivity {
    private long productId, userId;
    private EditText name, price, cost, profit;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private double productPrice = 0.0;

    private List<Ingredient> ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        Intent intent = getIntent();
        userId = intent.getLongExtra("userId", -1);
        productId = intent.getLongExtra("productId", -1);

        name = findViewById(R.id.ProductViewEditName);
        price = findViewById(R.id.ProductViewEditPrice);
        cost = findViewById(R.id.ProductViewEditCost);
        profit = findViewById(R.id.ProductViewEditProfit);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        String[] projection = {"id", "name", "price"};
        String[] selectionArgs = {String.valueOf(productId)};
        Cursor cursor = db.query("Product", projection, "id = ?", selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            String productName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            productPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            String formattedPrice = String.format("R$%.2f", productPrice);

            name.setText(productName);
            price.setText(formattedPrice);
        }

        // query the products
        projection = new String[]{"product_id", "ingredient_id", "amount"};
        selectionArgs = new String[]{String.valueOf(productId)};
        cursor = db.query("ProductIngredient", projection, "product_id = ?", selectionArgs, null, null, null);
        List<ProductIngredient> productIngredient = new ArrayList<ProductIngredient>();

        while (cursor.moveToNext()) {
            long ingredientId = cursor.getLong(cursor.getColumnIndexOrThrow("ingredient_id"));
            double ingredientAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            productIngredient.add(new ProductIngredient(productId, ingredientId, ingredientAmount));
        }

        ListView listView = findViewById(R.id.ProductViewListItems);
        List<Ingredient> ingredientList = getIngredients(productIngredient);
        SubIngredientAdapter adapter = new SubIngredientAdapter(this, ingredientList);
        listView.setAdapter(adapter);


        db.close();
        dbHelper.close();

    }

    private List<Ingredient> getIngredients(List<ProductIngredient> productIngredients) {
        if (!db.isOpen()) {
            db = dbHelper.getReadableDatabase();
        }
        List<Ingredient> ingredients = new ArrayList<>();

        double totalCost = 0.0;
        for (ProductIngredient productIngredient : productIngredients) {
            long ingredientId = productIngredient.ingredient_id;

            Cursor cursor = db.query(
                    "Ingredient",
                    new String[]{"id", "name", "cost", "amount"}, // Replace with your column names
                    "id = ?",
                    new String[]{String.valueOf(ingredientId)},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
//                long ingredientId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String ingredientName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double ingredientCost = cursor.getDouble(cursor.getColumnIndexOrThrow("cost"));
                double ingredientAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

                double displayAmount = productIngredient.amount;
                double displayCost = (displayAmount * ingredientCost) / ingredientAmount;
                totalCost += displayCost;

                ingredients.add(new Ingredient(ingredientId, ingredientName, displayCost, displayAmount, userId));
            }

            cursor.close();
        }

        cost.setText(String.format("R$%.2f", totalCost));
        profit.setText(String.format("R$%.2f", productPrice - totalCost));
        return ingredients;
    }

    public void onRemove(View view) {
        db = dbHelper.getWritableDatabase();

        String[] selectionArgs = {String.valueOf(productId)};
        db.delete("ProductIngredient", "product_id = ?", selectionArgs);
        int deleted = db.delete("Product", "id = ?", selectionArgs);


        if (deleted > 0) {
            Toast.makeText(
                    getApplicationContext(),
                    "Produto Removido!",
                    Toast.LENGTH_SHORT).show();

            db.close();
            dbHelper.close();

            Intent intent = new Intent(getApplicationContext(), ListProductsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Não é possível apagar!",
                    Toast.LENGTH_SHORT).show();
        }
        db.close();
        dbHelper.close();
    }


    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ListProductsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }
}