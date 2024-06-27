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
import com.example.gerenciadordecustos.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListProductsActivity extends AppCompatActivity {
    private long userId;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_products);

        Intent intent = getIntent();
        userId = intent.getLongExtra("userId", -1);

        dbHelper = new DatabaseHelper(getApplicationContext());

        Spinner spinnerSort = findViewById(R.id.ListProducstSortSpinner);
        String[] sortingOptions = {"Ascendente", "Descendente", "Menor preço", "Maior preço"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortingOptions);
        spinnerSort.setAdapter(spinnerAdapter);
        spinnerSort.setSelection(0);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = sortingOptions[position];
                updateItems(id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        updateItems(0);
    }

    private void updateItems(long id) {
        ListView listView = findViewById(R.id.ListProductsItems);
        List<Product> productList = getProducts(id);
        ProductAdapter adapter = new ProductAdapter(this, productList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product selectedIngredient = (Product) adapter.getItem(position);
                Intent intent = new Intent(view.getContext(), ProductViewActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("productId", selectedIngredient.id);
                startActivity(intent);
                finish();
            }
        });
    }

    private List<Product> getProducts(long sortingType) {
        List<Product> products = new ArrayList<>();

        db = dbHelper.getWritableDatabase();

        String[] projection = {"id", "name", "price", "user_id"};
        String selection = "user_id = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query("Product", projection, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double itemPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

            products.add(new Product(itemId, itemName, itemPrice, userId));
        }
        db.close();

        if (sortingType == 0) {
            Collections.sort(products, new Comparator<Product>() {
                @Override
                public int compare(Product a, Product b) {
                    return a.name.compareTo(b.name);
                }
            });
        } else if (sortingType == 1) {
            Collections.sort(products, new Comparator<Product>() {
                @Override
                public int compare(Product a, Product b) {
                    return b.name.compareTo(a.name);
                }
            });
        } else if (sortingType == 2) {
            Collections.sort(products, new Comparator<Product>() {
                @Override
                public int compare(Product a, Product b) {
                    return (int) (a.price - b.price);
                }
            });
        } else {
            Collections.sort(products, new Comparator<Product>() {
                @Override
                public int compare(Product a, Product b) {
                    return (int) (b.price - a.price);
                }
            });
        }

        return products;
    }

    public void onAddProduct(View view) {
        Intent intent = new Intent(this, AddProductActivity.class);
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