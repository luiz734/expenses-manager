package com.example.gerenciadordecustos.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Database.db";
    public static final String TABLE_NAME_USER = "User";
    public static final String TABLE_NAME_INGREDIENT = "Ingredient";
    public static final String TABLE_NAME_PRODUCT = "Product";
    public static final String TABLE_NAME_PRODUCT_INGREDIENT = "ProductIngredient";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableUser());
        insertLocalUser(db);

        db.execSQL(createTableIngredient());
        db.execSQL(createTableProduct());
        db.execSQL(createTableProductIngredient());
    }

    private String createTableUser() {
        return "CREATE TABLE " + TABLE_NAME_USER + " (" +
                "id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username " + " VARCHAR(50) , " +
                "password" + " VARCHAR(50), " +
                "profile_picture" + " BLOB);";
    }

    private String createTableIngredient() {
        return "CREATE TABLE " + TABLE_NAME_INGREDIENT + " (" +
                "id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name " + " VARCHAR(50) , " +
                "cost" + " REAL , " +
                "amount" + " REAL , " +
                "user_id" + " INTEGER);";
    }
    private String createTableProduct() {
        return "CREATE TABLE " + TABLE_NAME_PRODUCT + " (" +
                "id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name " + " VARCHAR(50) , " +
                "price" + " REAL , " +
                "user_id" + " INTEGER);";
    }
    private String createTableProductIngredient() {
        return "CREATE TABLE " + TABLE_NAME_PRODUCT_INGREDIENT + " (" +
                "product_id" + " INTEGER, " +
                "ingredient_id" + " INTEGER, " +
                "amount" + " REAL, " +
                "PRIMARY KEY (product_id, ingredient_id), " +
                "FOREIGN KEY (product_id) REFERENCES " + TABLE_NAME_PRODUCT + "(id), " +
                "FOREIGN KEY (ingredient_id) REFERENCES " + TABLE_NAME_INGREDIENT + "(id)" +
                ");";
    }

    private void insertLocalUser(SQLiteDatabase db) {
        String insertQuery = "INSERT INTO " + TABLE_NAME_USER + " (id, username, password) VALUES (0, 'local', 'local');";
        db.execSQL(insertQuery);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER);
    }
}
