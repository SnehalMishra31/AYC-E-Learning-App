package com.alphaCoachingAdmin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alphaCoachingAdmin.AlphaApplication;
import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.FcmConnection.FCMTokenReceiver;
import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.Utils.UserSharedPreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.Objects;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {
    private RelativeLayout mLoginLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText email;
    private EditText password;
    private static FirebaseAuth fireAuth;
    private Handler handler = new Handler();
    private static FirebaseFirestore mFireBaseDB;
    private CircularProgressButton mLoginBtn;
    private Context mContext;
    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginLayout = findViewById(R.id.loginScreen);
        email = findViewById(R.id.username);
        password = findViewById(R.id.pass);
        mLoginBtn = findViewById(R.id.loginButton);
        mContext = AlphaApplication.getAppContext();

        handler.postDelayed(() -> {
            mLoginLayout.setVisibility(View.VISIBLE);
        }, 3000);

        fireAuth = FirebaseAuth.getInstance();
        mLoginBtn.setOnClickListener(v -> userLogin());

    }

    /**
     * Function used to authenticate the user input.
     */
    private void userLogin() {
        mLoginBtn.startMorphAnimation();
        final String sEmail = email.getText().toString().trim();
        final String sPassword = password.getText().toString().trim();
        if (sEmail.isEmpty()) {
            email.setError("Email Required");
            email.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Enter a Valid Email");
            email.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        if (sPassword.isEmpty()) {
            password.setError("Password is Required ");
            password.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        if (sPassword.length() < 6) {
            password.setError("password should be at least 6 character");
            password.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        fireAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mFireBaseDB = FirebaseFirestore.getInstance();
                FirebaseUser currentUser = fireAuth.getCurrentUser();
                assert currentUser != null;
                String user_Uuid = currentUser.getUid();
                DocumentReference documentReference = mFireBaseDB.collection
                        (Constant.ADMIN_USSERS_COLLECTION).document(user_Uuid);
                documentReference.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task1.getResult();
                        assert documentSnapshot != null;
                        if (documentSnapshot.exists()) {
                            Boolean loginStatus = (Boolean) documentSnapshot.get(Constant.AdminUserCollectionFields.LOGIN_STATUS);
                            String role = documentSnapshot.get(Constant.AdminUserCollectionFields.ADMIN_ROLE).toString();
                            if (false/*loginStatus && !role.equalsIgnoreCase(Constant.UserRoles.MASTER_USER)*/) {
                                Toast.makeText(getApplicationContext(), "You are already logged in somewhere, logout from that device first!", Toast.LENGTH_LONG).show();

                                fireAuth.signOut();
                                Intent intent = new Intent(this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                finish();
                            } else {
//                                updateLoginStatus(user_Uuid, true);
                                String userFirstName = (String) documentSnapshot.get(Constant.AdminUserCollectionFields.FIRST_NAME);
                                String userLastName = (String) documentSnapshot.get(Constant.AdminUserCollectionFields.LAST_NAME);
                                String userStandard = (String) documentSnapshot.get(Constant.AdminUserCollectionFields.STANDARD);
                                String dateOfBirth = (String) documentSnapshot.get(Constant.AdminUserCollectionFields.DOB);
                                String userEmail = (String) documentSnapshot.get(Constant.AdminUserCollectionFields.EMAIL);
                                String userRole = (String) documentSnapshot.get(Constant.AdminUserCollectionFields.ADMIN_ROLE);
                                UserSharedPreferenceManager.storeUserDetail(mContext, user_Uuid, userFirstName, userLastName, userStandard, dateOfBirth, userEmail, userRole);
                                UserSharedPreferenceManager.storeIsMasterRole(userRole, mContext);
                                storeSubjects();
                                openMainActivity();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "User Not found in our application, try different account.", Toast.LENGTH_SHORT).show();
                            fireAuth.signOut();
                        }
                    }
                });
                mLoginBtn.dispose();
            } else {
                mLoginBtn.startMorphRevertAnimation();
                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMainActivity() {
//        overridePendingTransition(null, null);
        Intent intent = new Intent(this, FCMTokenReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startService(intent);
        finish();
    }

    private void updateLoginStatus(String userId, Boolean status) {
        if (userId == null)
            return;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference contact = firestore.collection(Constant.ADMIN_USSERS_COLLECTION).document(userId);
        contact.update(Constant.AdminUserCollectionFields.LOGIN_STATUS, status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "updateLoginStatus: onFailure: " + e.getMessage());
                    }
                });
    }

    private void storeSubjects() {
        mFireBaseDB.collection(Constant.SUBJECT_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            UserSharedPreferenceManager.storeUserSubjects(getApplicationContext(), snapshot.getId(), Objects.requireNonNull(snapshot.get("name")).toString());
                        }
                    }
                });
    }

    public void logoutUser(String userId) {
//        Intent intent = new Intent(getAppContext(), LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        FirebaseAuth.getInstance().signOut();
//        UserSharedPreferenceManager.removeUserData(getAppContext());
//        startActivity(intent);
//        finish();
        updateLoginStatus(userId, false);
    }

}