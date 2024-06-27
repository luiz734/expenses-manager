package com.example.gerenciadordecustos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciadordecustos.model.DatabaseHelper;
import com.example.gerenciadordecustos.model.Persistence;

public class HomeActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private long userId;
    private ImageView profilePicture;

    private Button syncButton;
    private TextView welcomeMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Intent intent = getIntent();
        userId = intent.getLongExtra("userId", -1);
        profilePicture = findViewById(R.id.HomePicture);

        syncButton = findViewById(R.id.HomeButtonSync);
        welcomeMessage = findViewById(R.id.HomeWelcome);

        dbHelper = new DatabaseHelper(this);

        if (userId != 0) {
            syncButton.setEnabled(true);
            welcomeMessage.setVisibility(View.VISIBLE);
            db = dbHelper.getReadableDatabase();

            String[] projection = {"profile_picture", "username"};
            String selection = "id = ?";
            String[] selectionArgs = {String.valueOf(userId)};
            Cursor cursor = db.query("User", projection, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                byte[] byteArray = cursor.getBlob(cursor.getColumnIndexOrThrow("profile_picture"));
                String message = "Bem vindo, " + cursor.getString(cursor.getColumnIndexOrThrow("username"));
                welcomeMessage.setText(message);
                if (byteArray != null) {
                    Bitmap retrievedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    profilePicture.setImageBitmap(retrievedBitmap);
                }
            }
            cursor.close();

            db.close();
        } else {
            syncButton.setEnabled(false);
//            welcomeMessage.setVisibility(View.GONE);
            welcomeMessage.setText("USUÁRIO LOCAL");
        }
    }

    public void onExitAccount(View view) {
        Persistence persistence = Persistence.getInstance();
        persistence.deleteFile();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSync(View view) {
        Toast.makeText(
                getApplicationContext(),
                "Salvo na nuvem!",
                Toast.LENGTH_SHORT).show();
    }

    public void onExport(View view) {
        Intent intent = new Intent(this, ExportActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    public void onShowProducts(View view) {
        Intent intent = new Intent(this, ListProductsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    public void onShowIngredients(View view) {
        Intent intent = new Intent(this, ListIngredientsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    public void onAddIngredient(View view) {
        Intent intent = new Intent(this, AddIngredientActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    public void onAddProduct(View view) {
        db = dbHelper.getReadableDatabase();

        String[] projection = {"user_id"};
        String selection = "user_id = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query("Ingredient", projection, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            Intent intent = new Intent(this, AddProductActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Adicione pelo menos 1 ingrediente!",
                    Toast.LENGTH_SHORT).show();
        }
        db.close();


    }

//    public void onBackPressed() {
//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(intent);
//        finish();
//    }


}