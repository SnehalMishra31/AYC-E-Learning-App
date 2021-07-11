package com.alphaCoachingAdmin.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.Constant.FCMUtils;
import com.alphaCoachingAdmin.LocalDB.DbHelper;
import com.alphaCoachingAdmin.ModelClass.Standard;
import com.alphaCoachingAdmin.ModelClass.Subject;
import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.Utils.UserSharedPreferenceManager;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.alphaCoachingAdmin.AlphaApplication.getAppContext;

public class StoreRecentLectureActivity extends AppCompatActivity {
    //this is fire base reference
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mClass, mSubject;

    //views
    private EditText chaptername, description, pdfname;
    // EditText etsubject;
    private Button addbtn, addpdffile;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView checkedtv, uncheckedtv;
    private RadioGroup radioGroup;
    private RadioButton rbyes, rbno;
    private Spinner spinnerClass, spinnerSubject;
    private List<String> standard;                   //Stores name of Standards
    private List<String> standardID;
    private List<String> subject;                    //Stores name of Subjects
    private List<String> subjectID;                  //Stores the id of standard which is present as a key in the subject document
    private List<String> docsubjectID;               //stores the document id of the subject document
    private List<String> specificsubject;           //Stores the name of the sorted subjects
    private List<String> subjectIDtracker;
    private ArrayAdapter<String> adapter, adapter2;
    private boolean pdfOptionFlag = true;

    //these string will hold field values
    private String sname, ssubject, sdescription, svideo, spdfname;
    //time and date
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private String date;
    private String currentDateandTime;
    //Storage refernce of firebase
    private StorageReference storageReference;
    //this is a temporary URI object for holding the pdf files data
    private Uri pdfSelectData;
    private String mPdfname;
    private ProgressDialog mProgressBar;
    private EditText videoUrl;
    private List<Standard> mStandardList;
    private List<Subject> mSubjectsList;
    private DbHelper mDbHelper;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_data);

        // Initialization of all view components
        chaptername = (EditText) findViewById(R.id.chaptername);
        // etsubject = (EditText) findViewById(R.id.subject);
        description = (EditText) findViewById(R.id.description);
        progressBar = findViewById(R.id.pBar);
        addbtn = (Button) findViewById(R.id.addbtn);
        pdfname = (EditText) findViewById(R.id.pdfname);
        videoUrl = (EditText) findViewById(R.id.lectureVideo);
        addpdffile = (Button) findViewById(R.id.addpdffile);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        rbyes = (RadioButton) findViewById(R.id.rbyes);
        rbno = (RadioButton) findViewById(R.id.rbno);
        checkedtv = (TextView) findViewById(R.id.pdfcheckedtv);
        uncheckedtv = (TextView) findViewById(R.id.pdfnotcheckedtv);
        imageView = (ImageView) findViewById(R.id.imgchecked);
        spinnerClass = (Spinner) findViewById(R.id.spinnerLectureClass);
        standard = new ArrayList<String>();
        standardID = new ArrayList<String>();
        subject = new ArrayList<String>();
        subjectID = new ArrayList<String>();
        specificsubject = new ArrayList<String>();
        subjectIDtracker = new ArrayList<String>();
        docsubjectID = new ArrayList<String>();
        spinnerSubject = findViewById(R.id.spinnerLectureSubject);
        mProgressBar = new ProgressDialog(this);
        mContext = getAppContext();
        mDbHelper = DbHelper.getInstance(mContext);

        adapter = new ArrayAdapter<String>(StoreRecentLectureActivity.this, android.R.layout.simple_spinner_dropdown_item, standard);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);
        adapter2 = new ArrayAdapter<String>(StoreRecentLectureActivity.this, android.R.layout.simple_spinner_dropdown_item, specificsubject);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapter2);

        storageReference = FirebaseStorage.getInstance().getReference();

        rbyes.setChecked(true);
        getClassData();
        setListeners();
    }

    private void setListeners() {
        addpdffile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectpdf();
            }
        });

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mClass = standardID.get(position);
                specificsubject.clear();
                subjectIDtracker.clear();
                /*
                Will sort the subjects which matches the Standard ID
                 */
                String string = standardID.get(position);
                for (int i = 0; i < subject.size(); i++) {
                    if (subjectID.get(i).equals(string)) {
                        subjectIDtracker.add(docsubjectID.get(i));
                        specificsubject.add(subject.get(i));
                    }
                }
                Log.d("CheckerGame", "onStandardItemSelected: " + mClass);

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
                Log.d("CheckerGame", "onSubjectItemSelected: " + mSubject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /**
         * This button will upload all our data on firestore.
         */
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                makeInvisible();

                //Note: THERE ARE TWO DIFFERENT DATE VARIABLES ONE FOR STORING LECTURE DATE AND OTHER FOR GENERATING DOCUMENT ID
                //This currentDateandTime will hold the date to be uploaded on firestore as a key value pair
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                currentDateandTime = sdf.format(new Date());
                Timestamp timestamp = new Timestamp(new Date());
                //This date variable is for generating the document ID and it is not related to the above currentDateandTime variable
                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                date = simpleDateFormat.format(calendar.getTime());

                sname = chaptername.getText().toString();
                sdescription = description.getText().toString();
                svideo = videoUrl.getText().toString();

                if (sname.equals("") || mSubject.equals("") || sdescription.equals("") || mClass.equals("")) {
                    progressBar.setVisibility(View.GONE);
                    makeVisible();
                    Toast.makeText(StoreRecentLectureActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgressDialogWithTitle("Don't press back button.");
                if (pdfOptionFlag) {
                    if (pdfSelectData != null) {
                        mPdfname = pdfname.getText().toString();
                        if (mPdfname.equals("")) {
                            Toast.makeText(StoreRecentLectureActivity.this, "Please enter PDF name", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            makeVisible();
                            return;
                        }
                        final StorageReference reference = storageReference.child("uploads/" + System.currentTimeMillis() + ".pdf");
                        reference.putFile(pdfSelectData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uri.isComplete()) ;
                                Uri url = uri.getResult();
                                saveLectureData(url);
                                Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
                            }
                        });

                    } else {
                        Toast.makeText(StoreRecentLectureActivity.this, "You have not selected the PDF", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        makeVisible();
                    }
                } else {
                    saveLectureData(null);
                }
            }
        });
    }

    /**
     * This function gets all the class and store into the local database.
     */
    private void getClassData() {
        mStandardList = mDbHelper.getAllStandards();
        if (mStandardList == null || mStandardList.isEmpty()) {
            mStandardList = new ArrayList<>();
            showProgressDialogWithTitle("Loading Your Data");
            db.collection(Constant.STANDARD_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Standard model = document.toObject(Standard.class);
                            model.setId(document.getId());
                            standard.add(document.get(Constant.StandardCollectionFields.STANDARD).toString());
                            standardID.add(document.getId());
                            mStandardList.add(model);
                            mDbHelper.addStandard(model.getId(), model.getStandard());
                        }
                    }
                    mSubjectsList = mDbHelper.getAllSubjects();
                    if (mSubjectsList == null || mSubjectsList.isEmpty()) {
                        getAllSubjects();
                    } else {
                        for (Subject model : mSubjectsList) {
                            subject.add(model.getName());
                            subjectID.add(model.getStandard());
                            docsubjectID.add(model.getId());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        } else {
            for (Standard model : mStandardList) {
                standard.add(String.valueOf(model.getStandard()));
                standardID.add(model.getId());
            }

            mSubjectsList = mDbHelper.getAllSubjects();
            if (mSubjectsList == null || mSubjectsList.isEmpty()) {
                getAllSubjects();
            } else {
                for (Subject model : mSubjectsList) {
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
                    Toast.makeText(StoreRecentLectureActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
                    hideProgressDialogWithTitle();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This method is for selecting the PDF from internal storage
     */
    private void selectpdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select pdf file"), 1);
    }


    /**
     * This method is automaticallly called after selectpdf method this will hold the selected pdf
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //this temp variable will hold the selected file(data)
            pdfSelectData = data.getData();
            imageView.setVisibility(View.VISIBLE);
            checkedtv.setVisibility(View.VISIBLE);
            uncheckedtv.setVisibility(View.GONE);
        }
    }


    /**
     * This method will be called when user clicks the submit for uploading all the data
     * @param file
     */
    public void saveLectureData(Uri file) {
        Timestamp timestamp = new Timestamp(new Date());
        Map<String, Object> datatosave = new HashMap<>();
        datatosave.put(Constant.RecentLectureFields.CHAPTERNAME, sname);
        datatosave.put(Constant.RecentLectureFields.DESCRIPTION, sdescription);
        datatosave.put(Constant.RecentLectureFields.SUBJECT, UserSharedPreferenceManager.getUserSubject(getApplicationContext(), mSubject));
        datatosave.put(Constant.RecentLectureFields.DATE, timestamp);
        datatosave.put(Constant.RecentLectureFields.VIDEO_URL, svideo);
        datatosave.put(Constant.RecentLectureFields.PDF_NAME, mPdfname);
        datatosave.put(Constant.RecentLectureFields.STANDARD, mClass);
        if (file != null)
            datatosave.put(Constant.RecentLectureFields.URLKEY, file.toString());

        db.collection(Constant.RECENT_LECTURE_COLLECTION).document(date).set(datatosave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                makeVisible();
                notifyAllUsers(mClass, date, sname);
                Toast.makeText(StoreRecentLectureActivity.this, " Data is successfully added", Toast.LENGTH_SHORT).show();
                hideProgressDialogWithTitle();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                makeVisible();
                Toast.makeText(StoreRecentLectureActivity.this, "Failed to add data,please try again", Toast.LENGTH_SHORT).show();
                hideProgressDialogWithTitle();
            }
        });
    }

    /**
     * This function sends the notification to all the students of that selected class.
     * @param standard
     * @param docId
     * @param lectureName
     */
    private void notifyAllUsers(String standard, final String docId, String lectureName) {
        final ArrayList<String> tokenList = new ArrayList<>();
        db.collection(Constant.USER_COLLECTION)
                .whereEqualTo(Constant.UserCollectionFields.STANDARD, standard)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                if (snapshot.get("token") != null)
                                    tokenList.add(snapshot.get("token").toString());
                            }
                            FCMUtils.sendPushMessage(StoreRecentLectureActivity.this, tokenList, "recentLecture", docId, lectureName);
                        }
                    }
                });
    }

    public void onoptclick(View view) {
        int id = radioGroup.getCheckedRadioButtonId();
        switch (id) {
            case R.id.rbyes:
                pdfOptionFlag = true;
                pdfname.setVisibility(View.VISIBLE);
                addpdffile.setVisibility(View.VISIBLE);
                break;
            case R.id.rbno:
                pdfOptionFlag = false;
                pdfname.setVisibility(View.INVISIBLE);
                addpdffile.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * This is a method for making the other UI components invisible while downloading
     */
    public void makeInvisible() {
        // etsubject.setVisibility(View.GONE);
        chaptername.setVisibility(View.GONE);
        description.setVisibility(View.GONE);
        pdfname.setVisibility(View.GONE);
        videoUrl.setVisibility(View.GONE);
        addbtn.setVisibility(View.GONE);
        addpdffile.setVisibility(View.GONE);
        radioGroup.setVisibility(View.GONE);
        return;
    }


    /**
     * This is a method for making the other UI components visible after downloading
     */
    public void makeVisible() {
        //  etsubject.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        pdfname.setVisibility(View.VISIBLE);
        chaptername.setVisibility(View.VISIBLE);
        addbtn.setVisibility(View.VISIBLE);
        videoUrl.setVisibility(View.VISIBLE);
        addpdffile.setVisibility(View.VISIBLE);
        radioGroup.setVisibility(View.VISIBLE);
        return;
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
