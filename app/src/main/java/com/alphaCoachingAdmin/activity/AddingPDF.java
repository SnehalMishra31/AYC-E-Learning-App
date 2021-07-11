package com.alphaCoachingAdmin.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.Constant.FCMUtils;
import com.alphaCoachingAdmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddingPDF extends AppCompatActivity {

    Button choosePDF, submitPDF;
    ProgressBar progressBar;
    EditText pdfName;
    Uri temp;
    ImageView imageView;
    TextView checkedtv, uncheckedtv;


    String mClass, mSubject;
    //time and date
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String date;
    String nameOfPDF;

    //Storage refernce of firebase
    StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_pdf);

        choosePDF = (Button) findViewById(R.id.choosePDf);
        submitPDF = (Button) findViewById(R.id.submitPDf);
        pdfName = (EditText) findViewById(R.id.mpdfName);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        checkedtv = (TextView) findViewById(R.id.pdfcheckedtv);
        uncheckedtv = (TextView) findViewById(R.id.pdfnotcheckedtv);
        imageView = (ImageView) findViewById(R.id.imgchecked);
        storageReference = FirebaseStorage.getInstance().getReference();


        Intent intent = getIntent();
        mClass = intent.getStringExtra("class");
        mSubject = intent.getStringExtra("subject");


        choosePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Choose a PDF from internal storage
                selectpdf();
            }
        });


        submitPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Progress bar will appear on screen
                progressBar.setVisibility(View.VISIBLE);
                //all other components will be invisible
                makeInvisible();
                //This date variable is for generating the document ID and it is not related to the above currentDateandTime variable
                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                date = simpleDateFormat.format(calendar.getTime());

                nameOfPDF = pdfName.getText().toString();
                //this will check whether all fields are filled ,if not it will return without uploading anything
                if (nameOfPDF.equals("")) {
                    progressBar.setVisibility(View.GONE);
                    makeVisible();
                    Toast.makeText(AddingPDF.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (temp != null) {
                    uploadPDFFile(temp);
                } else {
                    Toast.makeText(AddingPDF.this, "You have not selected the PDF", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    makeVisible();
                }

            }


        });
    }

    //This is a method for making the other UI components invisible while downloading
    public void makeInvisible() {

        pdfName.setVisibility(View.GONE);
        submitPDF.setVisibility(View.GONE);
        choosePDF.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);


        return;
    }


    //This is a method for making the other UI components visible after downloading
    public void makeVisible() {

        pdfName.setVisibility(View.VISIBLE);
        submitPDF.setVisibility(View.VISIBLE);
        choosePDF.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);


        return;
    }

    //This method is for selecting the PDF from internal storage
    private void selectpdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select pdf file"), 1);


    }


    //This method is automaticallly called after selectpdf method this will hold the selected pdf
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //this temp variable will hold the selected file(data)
            temp = data.getData();
            imageView.setVisibility(View.VISIBLE);
            checkedtv.setVisibility(View.VISIBLE);
            uncheckedtv.setVisibility(View.GONE);

        }

    }

    //This method will be called when user clicks the addbtn for uploading all the data
    public void uploadPDFFile(final Uri data) {

        final StorageReference reference = storageReference.child("uploads/" + System.currentTimeMillis() + ".pdf");

        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                Uri url = uri.getResult();



                Map<String, Object> datatosave = new HashMap<>();
                datatosave.put("standard", mClass);
                datatosave.put("subject", mSubject);
                datatosave.put("PDFName", nameOfPDF);
                datatosave.put("url", url.toString());
                final String notifyUrl = url.toString();


                db.collection("PDF").document(date).set(datatosave).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        progressBar.setVisibility(View.GONE);
                        makeVisible();
                        Toast.makeText(AddingPDF.this, " Data is successfully added", Toast.LENGTH_SHORT).show();
                        notifyAllUsers(mClass, notifyUrl, nameOfPDF);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressBar.setVisibility(View.GONE);
                        makeVisible();
                        Toast.makeText(AddingPDF.this, "Failed to add data,please try again", Toast.LENGTH_SHORT).show();


                    }
                });

                //When file will be uploaded this message will appear
                Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // WE CAN SHOW THE PROGRESS HERE WHILE DOWNLOADING IF WE WANT
            }
        });

    }

//    this function used to send the notification to all the students of the same class.
    private void notifyAllUsers(String standard, final String url, String pdfName) {
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
                            FCMUtils.sendPushMessage(AddingPDF.this, tokenList, "pdf", url, pdfName);
                        }
                    }
                });
    }

}
