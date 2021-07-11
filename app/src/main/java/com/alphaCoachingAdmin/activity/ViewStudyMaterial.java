package com.alphaCoachingAdmin.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.LocalDB.DbHelper;
import com.alphaCoachingAdmin.ModelClass.Standard;
import com.alphaCoachingAdmin.ModelClass.Subject;
import com.alphaCoachingAdmin.fragment.PDFfragment;
import com.alphaCoachingAdmin.fragment.QuizzesFragment;
import com.alphaCoachingAdmin.fragment.VideosFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.alphaCoachingAdmin.R;

import static com.alphaCoachingAdmin.AlphaApplication.getAppContext;

public class ViewStudyMaterial extends AppCompatActivity {
    private androidx.appcompat.widget.Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String mClass, mSubject;
    private String title, title2;
    // String Chapter[]=new String[20],Subtopic[]=new String[20];
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog mProgressBar;
    private Standard mStandard;
    private Subject mSubjectClass;
    private Context mContext;
    private DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_study_material);

        Intent intent = getIntent();
        mClass = intent.getStringExtra("class");
        mSubject = intent.getStringExtra("subject");


        toolbar = (Toolbar) findViewById(R.id.myToolBar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.myViewPager);
        mProgressBar = new ProgressDialog(this);
        mContext = getAppContext();
        mDbHelper = DbHelper.getInstance(mContext);

        mStandard = mDbHelper.getStandard(mClass);
        mSubjectClass  = mDbHelper.getSubject(mSubject);

//        db.collection(Constant.STANDARD_COLLECTION).document(mClass).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                title = String.valueOf(documentSnapshot.get(Constant.StandardCollectionFields.STANDARD));
//
//                db.collection(Constant.SUBJECT_COLLECTION).document(mSubject).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        title2 = String.valueOf(documentSnapshot.get(Constant.SubjectCollectionFields.NAME));
//                        toolbar.setTitle(title + "th " + title2);
//                    }
//                });
//            }
//        });
        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new PDFfragment(mClass, mSubject), "PDFs");
        viewPagerAdapter.addFragment(new VideosFragment(mClass, mSubject), "VIDEOS");
        viewPagerAdapter.addFragment(new QuizzesFragment(mClass, mSubject), "QUIZ");
        viewPager.setAdapter(viewPagerAdapter);
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

}
