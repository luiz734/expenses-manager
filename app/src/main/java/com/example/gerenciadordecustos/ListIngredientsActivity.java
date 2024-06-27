package com.example.gerenciadordecustos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciadordecustos.model.DatabaseHelper;
import com.example.gerenciadordecustos.model.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListIngredientsActivity extends AppCompatActivity {

    private long userId;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ingredients);

        Intent intent = getIntent();
        userId = intent.getLongExtra("userId", -1);

        dbHelper = new DatabaseHelper(getApplicationContext());

        Spinner spinnerSort = findViewById(R.id.ListProducstSortSpinner);
        String[] sortingOptions = {"Ascendente", "Descendente", "Menor custo", "Maior custo"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortingOptions);
        spinnerSort.setAdapter(spinnerAdapter);
        spinnerSort.setSelection(0);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                String selectedOption = sortingOptions[position];
                updateItems(id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        updateItems(0);
    }

    private void updateItems(long id) {
        ListView listView = findViewById(R.id.ListIngredientsItems);
        List<Ingredient> ingredientList = getIngredients(id);
        IngredientAdapter adapter = new IngredientAdapter(this, ingredientList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected ingredient
                Ingredient selectedIngredient = (Ingredient) adapter.getItem(position);

                // Pass relevant data to the next activity (replace NextActivity.class with your actual activity)
                Intent intent = new Intent(view.getContext(), IngredientViewActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("ingredientId", selectedIngredient.id);
                intent.putExtra("ingredientName", selectedIngredient.name);
                intent.putExtra("ingredientCost", selectedIngredient.cost);
                intent.putExtra("ingredientAmount", selectedIngredient.amount);
                startActivity(intent);
                finish();
            }
        });
    }

    private List<Ingredient> getIngredients(long sortingType) {
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

            ingredients.add(new Ingredient(itemId,itemName, itemCost, itemAmount, userId));
        }
        cursor.close();
        db.close();

        if (sortingType == 0) {
            Collections.sort(ingredients, new Comparator<Ingredient>() {
                @Override
                public int compare(Ingredient a, Ingredient b) {
                    return a.name.compareTo(b.name);
                }
            });
        } else if (sortingType == 1) {
            Collections.sort(ingredients, new Comparator<Ingredient>() {
                @Override
                public int compare(Ingredient a, Ingredient b) {
                    return b.name.compareTo(a.name);
                }
            });
        } else if (sortingType == 2) {
            Collections.sort(ingredients, new Comparator<Ingredient>() {
                @Override
                public int compare(Ingredient a, Ingredient b) {
                    double constPerUnit = (a.cost/a.amount - b.cost/b.amount);
                    return constPerUnit >= 0.0 ? 1 : -1;
                }
            });
        } else {
            Collections.sort(ingredients, new Comparator<Ingredient>() {
                @Override
                public int compare(Ingredient a, Ingredient b) {
                    double constPerUnit = (b.cost/b.amount - a.cost/a.amount);
                    return constPerUnit >= 0.0 ? 1 : -1;
                }
            });
        }

        return ingredients;
    }
    public void onAddIngredient(View view) {
        Intent intent = new Intent(this, AddIngredientActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }
}