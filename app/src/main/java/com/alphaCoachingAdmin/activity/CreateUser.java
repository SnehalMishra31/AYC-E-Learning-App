package com.alphaCoachingAdmin.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.LocalDB.DbHelper;
import com.alphaCoachingAdmin.ModelClass.Standard;
import com.alphaCoachingAdmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.alphaCoachingAdmin.AlphaApplication.getAppContext;

public class CreateUser extends AppCompatActivity {

    private CardView cardView;
    private String mFirstName, mLastName, mDOB, mEmail, mFacultySubjects, mFacultyMobileNumber, mFacultyExperience, mFacultyAchievements, mRollNum, mPasswordVal, mQualificationVal, mQuoteVal;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText etfirstName, etlastName, etDOB, etEmail;
    private boolean firstTimeLogin, loginStatus;
    private Button createUserButton, viewUsersButton;
    private List<String> standardList;
    private List<String> standardIdList;
    private List<Standard> mStandardList;
    private Spinner spinnerClass;
    private ArrayAdapter<String> adapter;
    private String classID;
    private ProgressDialog mProgressBar;
    private Button mCreateFaculty;
    private String mUserRole;
    private LinearLayout mUserClassSelectionLL;
    private LinearLayout mAdminSubjectsLL;
    private LinearLayout mAdminMobileNumberLL;
    private LinearLayout mAdminExperienceLL;
    private LinearLayout mAdminAchievementsLL;
    private EditText mAdminSubjects;
    private EditText mAdminMobile;
    private EditText mAdminExperience;
    private EditText mAdminAchievement;
    private LinearLayout mUserRollNumberLL;
    private EditText mUserRollNumber;
    private EditText mUserPassword;
    private LinearLayout mFacultyQualificationLL;
    private EditText mFacultyQualification;
    private Button mSelectProfileImage;
    private Uri mImageHolder = null;
    private ImageView mProfileImage;
    private StorageReference mStorageReference;
    private LinearLayout mFacultyQuoteLL;
    private EditText mFacultyQuote;
    private DbHelper mDbHelper;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        etfirstName = findViewById(R.id.firstName);
        etlastName = findViewById(R.id.lastName);
        etDOB = findViewById(R.id.dateOfBirth);
        etEmail = findViewById(R.id.email);
        createUserButton = findViewById(R.id.createUser);
        viewUsersButton = findViewById(R.id.viewUsersbtn);
        cardView = findViewById(R.id.usercardView);
        spinnerClass = findViewById(R.id.userClass);
        standardList = new ArrayList<String>();
        standardIdList = new ArrayList<String>();
        mUserClassSelectionLL = findViewById(R.id.userClassSelection);
        mAdminSubjectsLL = findViewById(R.id.adminSubjectsLL);
        mAdminMobileNumberLL = findViewById(R.id.adminMobileNumberLL);
        mAdminExperienceLL = findViewById(R.id.adminExperienceLL);
        mAdminAchievementsLL = findViewById(R.id.adminAchievementLL);
        mCreateFaculty = findViewById(R.id.createFaculty);
        mUserPassword = findViewById(R.id.password);

        mAdminSubjects = findViewById(R.id.adminSubjects);
        mAdminMobile = findViewById(R.id.adminMobileNumber);
        mAdminExperience = findViewById(R.id.facultyExperience);
        mAdminAchievement = findViewById(R.id.adminAchievement);
        mUserRollNumberLL = findViewById(R.id.studentRollNumberLL);
        mUserRollNumber = findViewById(R.id.studentRollNumber);
        mFacultyQualificationLL = findViewById(R.id.facultyQualificationLL);
        mFacultyQualification = findViewById(R.id.facultyQualification);
        mSelectProfileImage = findViewById(R.id.selectProfileImage);
        mProfileImage = findViewById(R.id.profileImage);
        mFacultyQuoteLL = findViewById(R.id.facultyQuoteLL);
        mFacultyQuote = findViewById(R.id.facultyQuote);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = getAppContext();
        mDbHelper = DbHelper.getInstance(mContext);

        mUserRole = getIntent().getStringExtra("userRole");
        setupUIForUserRole(mUserRole);

        mProgressBar = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firstTimeLogin = true;
        loginStatus = false;

        getclassList();
        adapter = new ArrayAdapter<String>(CreateUser.this, android.R.layout.simple_spinner_dropdown_item, standardList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);
        setListeners();
    }

    private void setupUIForUserRole(String role) {
        if (role.equalsIgnoreCase(Constant.UserRoles.FACULTY) || role.equalsIgnoreCase(Constant.UserRoles.MASTER_USER)) {
            mUserClassSelectionLL.setVisibility(View.GONE);
            mAdminSubjectsLL.setVisibility(View.VISIBLE);
            mAdminMobileNumberLL.setVisibility(View.VISIBLE);
            mAdminExperienceLL.setVisibility(View.VISIBLE);
            mAdminAchievementsLL.setVisibility(View.VISIBLE);
            mUserRollNumberLL.setVisibility(View.GONE);
            createUserButton.setVisibility(View.GONE);
            mCreateFaculty.setVisibility(View.VISIBLE);
            mFacultyQualificationLL.setVisibility(View.VISIBLE);
            mSelectProfileImage.setVisibility(View.VISIBLE);
            mFacultyQuoteLL.setVisibility(View.VISIBLE);
        } else {
            mUserClassSelectionLL.setVisibility(View.VISIBLE);
            mAdminSubjectsLL.setVisibility(View.GONE);
            mAdminMobileNumberLL.setVisibility(View.VISIBLE);
            mAdminExperienceLL.setVisibility(View.GONE);
            mAdminAchievementsLL.setVisibility(View.GONE);
            createUserButton.setVisibility(View.VISIBLE);
            mCreateFaculty.setVisibility(View.GONE);
            mUserRollNumberLL.setVisibility(View.VISIBLE);
            mFacultyQualificationLL.setVisibility(View.GONE);
            mSelectProfileImage.setVisibility(View.GONE);
            mFacultyQuoteLL.setVisibility(View.GONE);
        }
    }

    //    This function sets the listeners for the different buttons on screen.
    private void setListeners() {

        mSelectProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        viewUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateUser.this, ViewUsers.class);
                if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
            }
        });

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirstName = etfirstName.getText().toString();
                mLastName = etlastName.getText().toString();
                mDOB = etDOB.getText().toString();
                mEmail = etEmail.getText().toString();
                mRollNum = mUserRollNumber.getText().toString();
                mPasswordVal = mUserPassword.getText().toString();
//                mFacultySubjects = mAdminSubjects.getText().toString();
                mFacultyMobileNumber = mAdminMobile.getText().toString();
//                mFacultyExperience = mAdminExperience.getText().toString();
//                mFacultyAchievements = mAdminAchievement.getText().toString();

                if (mFirstName.isEmpty() || mLastName.isEmpty() || mEmail.isEmpty()
                        || mDOB.isEmpty() || classID.isEmpty() || mFacultyMobileNumber.isEmpty() || mRollNum.isEmpty() || mPasswordVal.isEmpty()) {
                    Toast.makeText(CreateUser.this, "Please Enter All Details", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mPasswordVal.length() <= 6) {
                    Toast.makeText(CreateUser.this, "Password Should be more than 6 letters.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    cardView.setVisibility(View.VISIBLE);
                    createUser(mEmail, mPasswordVal, null);
                }
            }
        });

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classID = standardIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mCreateFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirstName = etfirstName.getText().toString();
                mLastName = etlastName.getText().toString();
                mDOB = etDOB.getText().toString();
                mEmail = etEmail.getText().toString();
                mFacultySubjects = mAdminSubjects.getText().toString();
                mFacultyMobileNumber = mAdminMobile.getText().toString();
                mFacultyExperience = mAdminExperience.getText().toString();
                mFacultyAchievements = mAdminAchievement.getText().toString();
                mPasswordVal = mUserPassword.getText().toString();
                mQualificationVal = mFacultyQualification.getText().toString();
                mQuoteVal = mFacultyQuote.getText().toString();

                if (mFirstName.isEmpty() || mLastName.isEmpty() || mEmail.isEmpty() || mDOB.isEmpty() || mPasswordVal.isEmpty()
                        || mFacultySubjects.isEmpty() || mFacultyMobileNumber.isEmpty() || mFacultyExperience.isEmpty()
                        || mFacultyAchievements.isEmpty() || mQualificationVal.isEmpty() || mQuoteVal.isEmpty()) {
                    Toast.makeText(CreateUser.this, "Please Enter All Details", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mImageHolder == null) {
                    Toast.makeText(getApplicationContext(), "Select profile image", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    cardView.setVisibility(View.VISIBLE);
                    storeImage(mEmail, mPasswordVal);
//                    createUser(mEmail, mPasswordVal);
                }
            }
        });
    }

    private void storeImage(String mEmail, String mPasswordVal) {
        showProgressDialogWithTitle("Data is uploading, don't press back button");
        final String imageName = String.valueOf(System.currentTimeMillis());
        StorageReference reference = mStorageReference.child(Constant.PROFIL_IMAGE_FOLDER + "/" + imageName);
        reference.putFile(mImageHolder)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        createUser(mEmail, mPasswordVal, imageName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error occur, please try again later.", Toast.LENGTH_SHORT).show();
                        hideProgressDialogWithTitle();
                    }
                });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    //    This function fetch the list of classes.
    private void getclassList() {
        mStandardList = mDbHelper.getAllStandards();
        if (mStandardList == null || mStandardList.isEmpty()) {
            mStandardList = new ArrayList<>();
            showProgressDialogWithTitle("Feting Your Data.");
            db.collection(Constant.STANDARD_COLLECTION)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    standardList.add(document.get(Constant.StandardCollectionFields.STANDARD).toString());
                                    standardIdList.add(document.getId());
                                    Standard standard = document.toObject(Standard.class);
                                    standard.setId(document.getId());
                                    mStandardList.add(standard);
                                    mDbHelper.addStandard(standard.getId(), standard.getStandard());
                                }
                            }
                            adapter.notifyDataSetChanged();
                            hideProgressDialogWithTitle();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Please try again later.", Toast.LENGTH_LONG).show();
                            hideProgressDialogWithTitle();
                        }
                    });
        } else {
            for (Standard standard : mStandardList) {
                standardList.add(String.valueOf(standard.getStandard()));
                standardIdList.add(standard.getId());
            }
        }
    }

    public void createUser(final String email, final String password, String imageName) {
        showProgressDialogWithTitle("Don't press back button, data is storing.");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            String UID = mAuth.getCurrentUser().getUid();
                            Map<String, Object> datatosave;// = new HashMap<>();
                            if (mUserRole.equals(Constant.UserRoles.FACULTY) || mUserRole.equals(Constant.UserRoles.MASTER_USER)) {
                                datatosave = getAdminObjectToStore(email, password, imageName);
                                storeData(datatosave, UID, Constant.ADMIN_USSERS_COLLECTION);
                            } else {
                                datatosave = getStudentObjectToStore(email, password);
                                storeData(datatosave, UID, Constant.USER_COLLECTION);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            cardView.setVisibility(View.GONE);
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            if (task.getException() != null)
                                Toast.makeText(CreateUser.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(CreateUser.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            hideProgressDialogWithTitle();
                        }
                    }
                });


    }

    private void storeData(Map<String, Object> datatosave, String UID, String collection) {

//                            Toast.makeText(CreateUser.this, "User account is created successfully!", Toast.LENGTH_SHORT).show();
        db.collection(collection).document(UID).set(datatosave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                cardView.setVisibility(View.GONE);
                Toast.makeText(CreateUser.this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show();
                hideProgressDialogWithTitle();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                cardView.setVisibility(View.GONE);
                Toast.makeText(CreateUser.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
                hideProgressDialogWithTitle();
            }
        });
    }

    private Map<String, Object> getAdminObjectToStore(String email, String password, String imageName) {
        Map<String, Object> datatosave = new HashMap<>();
        datatosave.put(Constant.AdminUserCollectionFields.FIRST_NAME, mFirstName);
        datatosave.put(Constant.AdminUserCollectionFields.LAST_NAME, mLastName);
        datatosave.put(Constant.AdminUserCollectionFields.DOB, mDOB);
        datatosave.put(Constant.AdminUserCollectionFields.EMAIL, email);
        datatosave.put(Constant.AdminUserCollectionFields.PASSWORD, password);
        datatosave.put(Constant.AdminUserCollectionFields.FIRST_TIME_LOGIN, firstTimeLogin);
        datatosave.put(Constant.AdminUserCollectionFields.LOGIN_STATUS, loginStatus);
        datatosave.put(Constant.AdminUserCollectionFields.STANDARD, classID);
        datatosave.put(Constant.AdminUserCollectionFields.ADMIN_ROLE, mUserRole);
        datatosave.put(Constant.AdminUserCollectionFields.MOBILE_NUMBER, mFacultyMobileNumber);
        datatosave.put(Constant.AdminUserCollectionFields.ADMIT_DATE, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        datatosave.put(Constant.AdminUserCollectionFields.ADMIT_TIME_IN_MILLIS, System.currentTimeMillis());
        datatosave.put(Constant.AdminUserCollectionFields.SUBJECTS, mFacultySubjects);
        datatosave.put(Constant.AdminUserCollectionFields.QUZLIFICATION, mQualificationVal);
        datatosave.put(Constant.AdminUserCollectionFields.EXPERIENCE, mFacultyExperience);
        datatosave.put(Constant.AdminUserCollectionFields.ACHIEVEMENT, mFacultyAchievements);
        datatosave.put(Constant.AdminUserCollectionFields.IMAGE, imageName);
        datatosave.put(Constant.AdminUserCollectionFields.QUOTE, mQuoteVal);

        return datatosave;
    }

    private Map<String, Object> getStudentObjectToStore(String email, String password) {
        Map<String, Object> datatosave = new HashMap<>();
        datatosave.put(Constant.UserCollectionFields.FIRST_NAME, mFirstName);
        datatosave.put(Constant.UserCollectionFields.LAST_NAME, mLastName);
        datatosave.put(Constant.UserCollectionFields.DOB, mDOB);
        datatosave.put(Constant.UserCollectionFields.EMAIL, email);
        datatosave.put(Constant.UserCollectionFields.PASSWORD, password);
        datatosave.put(Constant.UserCollectionFields.FIRST_TIME_LOGIN, firstTimeLogin);
        datatosave.put(Constant.UserCollectionFields.LOGIN_STATUS, loginStatus);
        datatosave.put(Constant.UserCollectionFields.STANDARD, classID);
        datatosave.put(Constant.UserCollectionFields.MOBILE_NUMBER, mFacultyMobileNumber);
        datatosave.put(Constant.UserCollectionFields.ROLL_NUMBER, mRollNum);
        datatosave.put(Constant.UserCollectionFields.ADMIT_DATE, new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        return datatosave;
    }

    private void showProgressDialogWithTitle(String substring) {
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.setCancelable(false);
        mProgressBar.setMessage(substring);
        mProgressBar.show();
    }

    private void hideProgressDialogWithTitle() {
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.dismiss();
    }

    //    This is used to display the profile image after selecting from the user.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageHolder = data.getData();
            mProfileImage.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageHolder);
                mProfileImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
