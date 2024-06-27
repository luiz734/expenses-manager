package com.example.gerenciadordecustos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gerenciadordecustos.model.DatabaseHelper;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUser, editTextPassword, editTextRepassword;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ImageView profilePicture;

    private Bitmap profilePictureScaled;

    private static final int REQUEST_CODE_PERMISSIONS = 10;
//    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acticity);

        editTextUser = findViewById(R.id.RegisterTextUsername);
        editTextPassword = findViewById(R.id.RegisterTextPassword);
        editTextRepassword = findViewById(R.id.RegisterTextRepassword);


        dbHelper = new DatabaseHelper(getApplicationContext());

        profilePicture = findViewById(R.id.RegisterPicture);
        profilePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE_PERMISSIONS);

            }
        });


    }
    private Bitmap scaleBitmap(Bitmap bitmap, float scale) {
        int width = Math.round(bitmap.getWidth() * scale);
        int height = Math.round(bitmap.getHeight() * scale);
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_PERMISSIONS && resultCode == RESULT_OK) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            profilePictureScaled = scaleBitmap(thumbnail, 4.0f); // Scale factor can be adjusted
            profilePicture.setImageBitmap(profilePictureScaled);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onRegister(View view) {
        db = dbHelper.getReadableDatabase();
        String[] projection = {"id", "username"};
        String selection = "username = ?";
        String[] selectionArgs = {String.valueOf(editTextUser.getText())};
        Cursor cursor = db.query("User", projection, selection, selectionArgs, null, null, null);

        if (editTextUser.getText().length() < 1 || editTextPassword.getText().length() < 1 || editTextRepassword.getText().length() < 1) {
            Toast.makeText(
                    getApplicationContext(),
                    "Preencha todos os campos!",
                    Toast.LENGTH_SHORT
            ).show();
        } else if (editTextPassword.getText().toString().length() < 3 || editTextRepassword.getText().toString().length() < 3) {
            Toast.makeText(
                    getApplicationContext(),
                    "A senha deve ter pelo menos 3 dígitos!",
                    Toast.LENGTH_SHORT
            ).show();
        } else if (!editTextPassword.getText().toString().equals(editTextRepassword.getText().toString())) {
            Toast.makeText(
                    getApplicationContext(),
                    "As senhas não correspondem!",
                    Toast.LENGTH_SHORT
            ).show();
        } else if (cursor.moveToFirst()) {
            Toast.makeText(
                    getApplicationContext(),
                    "Usuário já cadastrado!",
                    Toast.LENGTH_SHORT
            ).show();
            cursor.close();
        } else {
            if (db.isOpen()) db.close();

            db = dbHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            if (profilePictureScaled != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                profilePictureScaled.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                contentValues.put("profile_picture", byteArray);
            }


            contentValues.put("username", editTextUser.getText().toString());
            contentValues.put("password", editTextPassword.getText().toString());
            db.insert("User", null, contentValues);
            db.close();
            dbHelper.close();

            Toast.makeText(
                    getApplicationContext(),
                    "Usuário criado!",
                    Toast.LENGTH_SHORT
            ).show();
            onBackPressed();


        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}