package com.example.gerenciadordecustos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciadordecustos.model.DatabaseHelper;
import com.example.gerenciadordecustos.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private long userId;
    private EditText productName, productPrice;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private List<View> ingredientViews;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productName = findViewById(R.id.AddProductName);
        productPrice = findViewById(R.id.AddProductPrice);
        container = findViewById(R.id.container);

        dbHelper = new DatabaseHelper(getApplicationContext());

        Intent intent = getIntent();
        userId = intent.getLongExtra("userId", -1);

        ingredientViews = new ArrayList<>();
    }

    public boolean checkIngredients() {
        for (View ingredientRow : ingredientViews) {
            Spinner ingredientSpinner = ingredientRow.findViewById(R.id.ingredientSpinner);
            EditText quantityEditText = ingredientRow.findViewById(R.id.quantityEditText);

            try {
                String selectedIngredient = ingredientSpinner.getSelectedItem().toString();
                double quantity = Double.parseDouble(quantityEditText.getText().toString());

                if (selectedIngredient.isEmpty()) {
                    return false;
                }

            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private void removeAllIngredients() {
        for (View ingredientRow : ingredientViews) {
            container.removeView(ingredientRow);
        }
        ingredientViews.clear();
    }


    public void onAddRow(View view) {
        if (!checkIngredients()) {
            Toast.makeText(
                    getApplicationContext(),
                    "Preencha os ingredientes atuais primeiro!",
                    Toast.LENGTH_SHORT).show();
        } else {
            final View ingredientRow = getLayoutInflater().inflate(R.layout.ingredient_row, null);
            final Spinner ingredientSpinner = ingredientRow.findViewById(R.id.ingredientSpinner);
            final EditText quantityEditText = ingredientRow.findViewById(R.id.quantityEditText);
            final Button deleteButton = ingredientRow.findViewById(R.id.deleteButton);

            List<Ingredient> ingredientList = getIngredientList();
            ArrayAdapter<Ingredient> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ingredientList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ingredientSpinner.setAdapter(adapter);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    container.removeView(ingredientRow);
                    ingredientViews.remove(ingredientRow);
                }
            });

            container.addView(ingredientRow);
            ingredientViews.add(ingredientRow);
        }


    }

    public void onRegisterProduct(View view) {
        if (ingredientViews.size() < 1) {
            Toast.makeText(
                    getApplicationContext(),
                    "Adicione pelo menos 1 ingrediente!",
                    Toast.LENGTH_SHORT).show();
        } else if (!checkIngredients() || productName.getText().length() < 1 || productPrice.getText().length() < 1) {
            Toast.makeText(
                    getApplicationContext(),
                    "Preencha todos os campos!",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            try {

                double price = Double.parseDouble(productPrice.getText().toString());

                db = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", productName.getText().toString());
                contentValues.put("price", price);
                contentValues.put("user_id", userId);
                long productId = db.insert("Product", null, contentValues);

                for (View ingredientRow : ingredientViews) {
                    Spinner ingredientSpinner = ingredientRow.findViewById(R.id.ingredientSpinner);
                    EditText quantityEditText = ingredientRow.findViewById(R.id.quantityEditText);

                    Ingredient selectedIngredient = (Ingredient) ingredientSpinner.getSelectedItem();
                    long selectedIngredientId = selectedIngredient.id;

                    try {
                        double amount = Double.parseDouble(quantityEditText.getText().toString());
                        contentValues = new ContentValues();
                        contentValues.put("product_id", productId);
                        contentValues.put("ingredient_id", selectedIngredientId);
                        contentValues.put("amount", amount);
                        db.insert("ProductIngredient", null, contentValues);
                    } catch (NumberFormatException e) {
                    }
                }

                db.close();
                dbHelper.close();

                Toast.makeText(
                        getApplicationContext(),
                        "Produto Registrado!",
                        Toast.LENGTH_SHORT
                ).show();

                productName.setText("");
                productPrice.setText("");
                removeAllIngredients();


            } catch (NumberFormatException e) {
                Toast.makeText(
                        getApplicationContext(),
                        "Dados inválidos!",
                        Toast.LENGTH_SHORT
                ).show();
            }

        }
    }


    private List<Ingredient> getIngredientList() {
        List<Ingredient> ingredients = new ArrayList<>();
        db = dbHelper.getWritableDatabase();

        String[] projection = {"id", "name", "cost", "amount", "user_id"};
        String selection = "user_id = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query("Ingredient", projection, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double itemCost = cursor.getDouble(cursor.getColumnIndexOrThrow("cost"));
            double itemAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

            ingredients.add(new Ingredient(itemId, itemName, itemCost, itemAmount, userId));
        }
        db.close();

        return ingredients;
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }
}