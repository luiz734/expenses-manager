package com.example.gerenciadordecustos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import com.example.gerenciadordecustos.HomeActivity;
import com.example.gerenciadordecustos.R;
import com.example.gerenciadordecustos.model.DatabaseHelper;

import java.io.FileNotFoundException;
import java.io.OutputStream;

public class ExportActivity extends AppCompatActivity {
    private long userId;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private TextView exportPreview;

    private static final int REQUEST_CODE_PICK_DIRECTORY = 123;

    private String csvContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        Intent intent = getIntent();
        userId = intent.getLongExtra("userId", -1);
        exportPreview = findViewById(R.id.ExportPreview);
        dbHelper = new DatabaseHelper(getApplicationContext());

        generatePreview();
    }

    private void generatePreview() {
        db = dbHelper.getReadableDatabase();

        // product
        String[] projection = {"name", "price"};
        String selection = "user_id = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query("Product", projection, selection, selectionArgs, null, null, null);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Product").append("\n");
        stringBuilder
                .append("name").append(",")
                .append("price").append("\n");
        while (cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double itemPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

            stringBuilder
                    .append(itemName).append(",")
                    .append(itemPrice).append("\n");
        }
        stringBuilder.append("\n");

        // ingredient
        projection = new String[]{"name", "cost", "amount"};
        selection = "user_id = ?";
        selectionArgs = new String[]{String.valueOf(userId)};
        cursor = db.query("Ingredient", projection, selection, selectionArgs, null, null, null);

        stringBuilder.append("Ingredient").append("\n");
        stringBuilder.append("name").append(",").append("cost").append(",").append("amount").append("\n");
        while (cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double itemCost = cursor.getDouble(cursor.getColumnIndexOrThrow("cost"));
            double itemAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

            stringBuilder
                    .append(itemName).append(",")
                    .append(itemCost).append(",")
                    .append(itemAmount).append("\n");
        }
        stringBuilder.append("\n");

        String csv = stringBuilder.toString();
        csvContent = csv;
        exportPreview.setText(csv);
        db.close();

    }

    public void onExportCsv(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_PICK_DIRECTORY);
    }

    private void saveFile(Uri treeUri) {
        String fileName = "GdC_export.csv";

        try {
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
            DocumentFile newFile = pickedDir.createFile("text/csv", fileName);
            OutputStream outputStream = getContentResolver().openOutputStream(newFile.getUri());

            outputStream.write(csvContent.getBytes());
            outputStream.close();

            Toast.makeText(this, "File saved to " + newFile.getUri().toString(), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_DIRECTORY && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri treeUri = data.getData();
                saveFile(treeUri);
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