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
import com.example.gerenciadordecustos.model.Persistence;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUser, editTextPassword;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Persistence persistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUser = findViewById(R.id.MainTextUsername);
        editTextPassword = findViewById(R.id.MainTextPassword);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        loadLoginData();
    }
    public void onCreateAccount(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
    public void onLocalLogin(View view) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);

        try {
            persistence = new Persistence("local", "local", getApplicationContext());
            persistence.generateFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("userId", (long) 0);
        startActivity(intent);
        finish();
    }
    public void login(View view) {
        boolean valid = false;

        String[] projection = {"id", "username", "password"};
        Cursor cursor = db.query("User", projection, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            Long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));

            if (username.equals(editTextUser.getText().toString()) && password.equals(editTextPassword.getText().toString())) {
                Toast.makeText(
                        getApplicationContext(),
                        "Logged!",
                        Toast.LENGTH_SHORT
                ).show();

                valid = true;

                db.close();
                dbHelper.close();

                try {
                    persistence = new Persistence(username, password, getApplicationContext());
                    persistence.generateFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("userId", id);
                startActivity(intent);
                finish();
            }
        }

        cursor.close();

        if (!valid) {
            Toast.makeText(
                    getApplicationContext(),
                    "Usuário ou senha incorretos",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
    public void loadLoginData() {
        persistence = Persistence.getInstance();
        persistence.setContext(getApplicationContext());

        String[] data = persistence.returnValues();

        if(data != null) {
            editTextUser.setText(data[0]);
            editTextPassword.setText(data[1]);
            login(findViewById(R.id.MainButtonLogin));
        }
    }


}