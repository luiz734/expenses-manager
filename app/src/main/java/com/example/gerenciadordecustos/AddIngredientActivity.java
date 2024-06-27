package com.example.gerenciadordecustos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciadordecustos.model.DatabaseHelper;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText ingredientName, ingredientCost, ingredientAmount;
    private long userId;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        ingredientName = findViewById(R.id.AddIngredientName);
        ingredientCost = findViewById(R.id.AddIngredientCost);
        ingredientAmount = findViewById(R.id.AddIngredientAmount);

        dbHelper = new DatabaseHelper(getApplicationContext());

        Intent intent = getIntent();
        userId = intent.getLongExtra("userId", -1);

    }

    public void onRegisterIngredient(View view) {
        if (ingredientName.getText().length() < 1 || ingredientCost.getText().length() < 1 || ingredientAmount.getText().length() < 1) {
            Toast.makeText(
                    getApplicationContext(),
                    "Preencha todos os campos!",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            try {
                double cost = Double.parseDouble(ingredientCost.getText().toString());
                double amount = Double.parseDouble(ingredientAmount.getText().toString());

                db = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", ingredientName.getText().toString());
                contentValues.put("cost", cost);
                contentValues.put("amount", amount);
                contentValues.put("user_id", userId);
                db.insert("Ingredient", null, contentValues);
                db.close();
                dbHelper.close();

                Toast.makeText(
                        getApplicationContext(),
                        "Ingrediente Registrado!",
                        Toast.LENGTH_SHORT
                ).show();

                ingredientName.setText("");
                ingredientCost.setText("");
                ingredientAmount.setText("");


            } catch (NumberFormatException e) {
                Toast.makeText(
                        getApplicationContext(),
                        "Dados inválidos!",
                        Toast.LENGTH_SHORT
                ).show();
            }

        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

}