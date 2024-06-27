package com.example.gerenciadordecustos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciadordecustos.model.DatabaseHelper;

public class IngredientViewActivity extends AppCompatActivity {

    private long userId;
    private long ingredientId;
    private String ingredientName;
    private double ingredientCost;
    private double ingredientAmount;

    private EditText editName, editCost;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_view);

        editName = findViewById(R.id.ingredientViewName);
        editCost = findViewById(R.id.ingredientViewCost);

        Intent intent = getIntent();
        userId = intent.getLongExtra("userId", -1);
        ingredientId = intent.getLongExtra("ingredientId", -1);
        ingredientName = intent.getStringExtra("ingredientName");
        ingredientCost = intent.getDoubleExtra("ingredientCost", -1.0);
        ingredientAmount = intent.getDoubleExtra("ingredientAmount", -1.0);

        editName.setText(ingredientName);
        String costAmountText = String.format("R$%.2f por %.2f unidade(s)", ingredientCost, ingredientAmount);
        editCost.setText(costAmountText);

        dbHelper = new DatabaseHelper(getApplicationContext());

    }

    public void onRemove(View view) {
        db = dbHelper.getReadableDatabase();

        String[] selectionArgs = {String.valueOf(ingredientId)};
        Cursor cursor = db.query(
                "ProductIngredient",
                new String[]{"ingredient_id"},
                "ingredient_id = ?", selectionArgs,
                null, null, null);
        if (cursor.moveToFirst()) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(200);
            Toast.makeText(
                    getApplicationContext(),
                    "O ingrediente é usado por algum produto!",
                    Toast.LENGTH_SHORT).show();
            db.close();
        } else {
            db.close();
            db = dbHelper.getWritableDatabase();

            selectionArgs = new String[]{String.valueOf(ingredientId)};
            int deleted = db.delete("Ingredient", "id = ?", selectionArgs);

            if (deleted > 0) {
                Toast.makeText(
                        getApplicationContext(),
                        "Ingrediente Removido!",
                        Toast.LENGTH_SHORT).show();

                db.close();
                dbHelper.close();

                Intent intent = new Intent(getApplicationContext(), ListIngredientsActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Não é possível apagar!",
                        Toast.LENGTH_SHORT).show();
            }
        }

        db.close();


    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ListIngredientsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }
}