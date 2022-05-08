package com.example.webshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webshop.model.UserDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;
    private final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    EditText userEmailEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;
    EditText phoneEditText;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userEmailEditText = findViewById(R.id.userEmailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordAgainEditText);
        phoneEditText = findViewById(R.id.phoneEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        userEmailEditText.setText(userName);
        passwordEditText.setText(password);
        passwordConfirmEditText.setText(password);


        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {
        String email = userEmailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        if (!email.matches(EMAIL_REGEX)) {
            Toast.makeText(RegisterActivity.this, "Helytelen e-mail cím!", Toast.LENGTH_LONG).show();
            return;
        }
        if (phone.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "A telefonszám megadása kötelező!", Toast.LENGTH_LONG).show();
            return;
        }
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(RegisterActivity.this, "A két jelszó nem egyezik!", Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                UserDetail userDetail = new UserDetail();
                userDetail.setUID(mAuth.getUid());
                userDetail.setPhoneNumber(phone);
                firestore.collection("userDetails").add(userDetail);
                startShopping();
            } else {
                Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(task -> Log.e(LOG_TAG, task.getMessage()));
    }

    public void cancel(View view) {
        finish();
    }

    private void startShopping() {
        Intent intent = new Intent(this, ShopListActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        Log.i(LOG_TAG, selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
