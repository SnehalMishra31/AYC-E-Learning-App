package com.alphaCoachingAdmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.LocalDB.DbHelper;
import com.alphaCoachingAdmin.ModelClass.Standard;
import com.alphaCoachingAdmin.ModelClass.Subject;
import com.alphaCoachingAdmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.alphaCoachingAdmin.AlphaApplication.getAppContext;

public class SelectClass extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();  //Firestore Reference
    private Spinner spinnerClass, spinnerSubject;                    //Spinners for class and subjects
    private Button submitClass;                                      //Submit button
    private List<String> standard;                   //Stores name of Standards
    private List<String> standardID;                  //Stores ID of the standard documents
    private List<String> subject;                    //Stores name of Subjects
    private List<String> subjectID;                  //Stores the id of standard which is present as a key in the subject document
    private List<String> docsubjectID;               //stores the document id of the subject document
    private List<String> specificsubject;           //Stores the name of the sorted subjects
    private List<String> subjectIDtracker;            //Stores the document id of the sorted subjects
    private String mClass, mSubject;   //Strings which contains selected Class/Standard and subjects iD
    private ArrayAdapter<String> adapter, adapter2;
    private ProgressDialog mProgressBar;
    private Context mContext;
    private ArrayList<Standard> mStandardList;
    private ArrayList<Subject> mSubjectList;
    private DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class);

        submitClass = findViewById(R.id.submitClass);
        spinnerClass = findViewById(R.id.spinnerClass);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        standard = new ArrayList<String>();
        standardID = new ArrayList<String>();
        subject = new ArrayList<String>();
        subjectID = new ArrayList<String>();
        specificsubject = new ArrayList<String>();
        subjectIDtracker = new ArrayList<String>();
        docsubjectID = new ArrayList<String>();
        mProgressBar = new ProgressDialog(this);
        mContext = getAppContext();
        mDbHelper = DbHelper.getInstance(mContext);

        adapter = new ArrayAdapter<String>(SelectClass.this, android.R.layout.simple_spinner_dropdown_item, standard);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);

        adapter2 = new ArrayAdapter<String>(SelectClass.this, android.R.layout.simple_spinner_dropdown_item, specificsubject);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapter2);

        getClassAndSubjectData();
        submitClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectClass.this, ViewStudyMaterial.class);
                intent.putExtra("class", mClass);
                intent.putExtra("subject", mSubject);
                startActivity(intent);
            }
        });
    }

    private void getClassAndSubjectData() {
        mStandardList = mDbHelper.getAllStandards();
        if (mStandardList == null || mStandardList.isEmpty()) {
            showProgressDialogWithTitle("Feting Your Data");
            db.collection(Constant.STANDARD_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Standard model = document.toObject(Standard.class);
                            model.setId(document.getId());
                            standard.add(document.get(Constant.StandardCollectionFields.STANDARD).toString());
                            standardID.add(document.getId());
                            mDbHelper.addStandard(model.getId(), model.getStandard());
                        }
                    }
                    mSubjectList = mDbHelper.getAllSubjects();
                    if (mSubjectList == null || mSubjectList.isEmpty()) {
                        getAllSubjects();
                    } else {
                        for (Subject model : mSubjectList) {
                            subject.add(model.getName());
                            subjectID.add(model.getStandard());
                            docsubjectID.add(model.getId());
                            adapter.notifyDataSetChanged();
                        }
                        hideProgressDialogWithTitle();
                    }
//                    db.collection(Constant.SUBJECT_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    subject.add(document.get(Constant.SubjectCollectionFields.NAME).toString());
//                                    subjectID.add(document.get(Constant.SubjectCollectionFields.STANDARD).toString());
//                                    docsubjectID.add(document.getId());
//                                }
//                                hideProgressDialogWithTitle();
////                            Toast.makeText(SelectClass.this,"All data is fetched!",Toast.LENGTH_LONG).show();
//                            } else {
//                                Toast.makeText(SelectClass.this, "Something went wrong,please try again", Toast.LENGTH_LONG).show();
//                                hideProgressDialogWithTitle();
//                            }
//                            adapter.notifyDataSetChanged();
//                        }
//                    });
                }
            });
        } else {
            for (Standard model : mStandardList) {
                standard.add(String.valueOf(model.getStandard()));
                standardID.add(model.getId());
            }


            mSubjectList = mDbHelper.getAllSubjects();
            if (mSubjectList == null || mSubjectList.isEmpty()) {
                getAllSubjects();
            } else {
                for (Subject model : mSubjectList) {
                    subject.add(model.getName());
                    subjectID.add(model.getStandard());
                    docsubjectID.add(model.getId());
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * This function gets all the subjects and store into the local database.
     */
    private void getAllSubjects() {
        db.collection(Constant.SUBJECT_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        subject.add(document.get(Constant.SubjectCollectionFields.NAME).toString());
                        subjectID.add(document.get(Constant.SubjectCollectionFields.STANDARD).toString());
                        docsubjectID.add(document.getId());
                        mDbHelper.addSubject(document.getId(), document.get(Constant.SubjectCollectionFields.STANDARD).toString(), document.get(Constant.SubjectCollectionFields.NAME).toString());
                    }
//                            Toast.makeText(StoreRecentLectureActivity.this, "All data is fetched!", Toast.LENGTH_LONG).show();
                    hideProgressDialogWithTitle();
                } else {
                    Toast.makeText(SelectClass.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                    hideProgressDialogWithTitle();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSubject = "";
        mClass = "";
        specificsubject.clear();
        subjectIDtracker.clear();

        adapter = new ArrayAdapter<String>(SelectClass.this, android.R.layout.simple_spinner_dropdown_item, standard);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);

        adapter2 = new ArrayAdapter<String>(SelectClass.this, android.R.layout.simple_spinner_dropdown_item, specificsubject);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapter2);
        setListeners();
    }

    private void setListeners() {
        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mClass = standardID.get(position);
                specificsubject.clear();
                subjectIDtracker.clear();
                String string = standardID.get(position);
                for (int i = 0; i < subject.size(); i++) {
                    if (subjectID.get(i).equals(string)) {
                        subjectIDtracker.add(docsubjectID.get(i));
                        specificsubject.add(subject.get(i));
                    }
                }
                mSubject = subjectIDtracker.get(0);
                adapter2.notifyDataSetChanged();
                spinnerSubject.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSubject = subjectIDtracker.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String TAG = "Resume";
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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

