package com.alphaCoachingAdmin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alphaCoachingAdmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class SplashScreenActivity extends Activity {
    private FirebaseFirestore mFireBaseDB;
    private FirebaseAuth fireAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        setContentView(R.layout.activity_splash_screen);
        mFireBaseDB = FirebaseFirestore.getInstance();
        Log.d(TAG, "onCreate: ");
        new Handler().postDelayed(() -> {
            if (fireAuth.getCurrentUser() != null) {
                openMainActivity();
            } else {
                openLoginActivity();
            }
        }, 3000);
//        Toast.makeText(getApplication(), "Into the splashscreen", Toast.LENGTH_SHORT).show();

//        openMainActivity();
    }

    /**
     * To Open the main activity.
     */
    private void openMainActivity() {
        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

    private void openLoginActivity() {
        Intent mainActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }
}
