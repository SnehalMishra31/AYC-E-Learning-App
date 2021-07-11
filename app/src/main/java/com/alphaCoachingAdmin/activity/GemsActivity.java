package com.alphaCoachingAdmin.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.fragment.ScreenSlidePageFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.alphaCoachingAdmin.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static com.alphaCoachingAdmin.AlphaApplication.getAppContext;

public class GemsActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private FirebaseFirestore mFireStore;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;
    private Context mContext;
    private Uri mImageHolder;
    private ProgressDialog mProgressDialog;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gems);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = getAppContext();
        fab = findViewById(R.id.fab);
        mFireStore = FirebaseFirestore.getInstance();
        mProgressDialog = new ProgressDialog(this);
        mStorageReference = FirebaseStorage.getInstance().getReference();


        getAllGemsStudents();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageHolder = data.getData();
            storeImage();
        }
    }

    private void storeImage() {
        showProgressDialogWithTitle("Data is uploading, don't press back button");
        final String imageName = String.valueOf(System.currentTimeMillis());
        StorageReference reference = mStorageReference.child(Constant.PROFIL_IMAGE_FOLDER + "/" + imageName);
        reference.putFile(mImageHolder)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storeImageIntoDB(imageName);
//                        Intent intent = new Intent(mContext, GemsActivity.class);
//                        mContext.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Error occur, please try again later.", Toast.LENGTH_SHORT).show();
                        hideProgressDialogWithTitle();
                    }
                });
    }

    private void storeImageIntoDB(String imageUrl) {
        final HashMap<String, Object> dataToStore = new HashMap<>();
        dataToStore.put("imageUrl", imageUrl);
        mFireStore.collection(Constant.GEMS_LIST_COLLECTION)
                .add(dataToStore)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(mContext, GemsActivity.class);
                        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        mContext.startActivity(intent);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void getAllGemsStudents() {
        ArrayList<String> allGems = new ArrayList<>();
        mFireStore.collection(Constant.GEMS_LIST_COLLECTION)
//                .orderBy("priority", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                            allGems.add(snapshot.get("imageUrl").toString());
                        }
                        mPager = findViewById(R.id.pager);
                        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), allGems);
                        mPager.setAdapter(pagerAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Please try again!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<String> tutors;
        public ScreenSlidePagerAdapter(FragmentManager fm, ArrayList<String> list) {
            super(fm);
            tutors = list;
        }

        @Override
        public Fragment getItem(int position) {
            return new ScreenSlidePageFragment(tutors.get(position));
        }

        @Override
        public int getCount() {
            return tutors.size();
        }
    }

    private void showProgressDialogWithTitle(String substring) {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(substring);
        mProgressDialog.show();
    }

    private void hideProgressDialogWithTitle() {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.dismiss();
    }

}