package com.alphaCoachingAdmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoachingAdmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ResultDetails extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String quizTakenID;
    int totalquestions;
    int timeForQuestion[];
    String questions[];
    List<DocumentSnapshot> documentSnapshotList;
    List<String> questionsList;
    int count=0;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_details);

        Intent intent1=getIntent();
        quizTakenID=intent1.getStringExtra("id");
        totalquestions=intent1.getIntExtra("total",0);
        documentSnapshotList=new ArrayList<DocumentSnapshot>();
        questionsList=new ArrayList<String>();
        tv=(TextView) findViewById(R.id.displayEachTime);
        Log.d("idcheck", "onCreate: "+quizTakenID);

        db.collection("quizTakenQuestions").whereEqualTo("quizTakenId",quizTakenID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                documentSnapshotList=queryDocumentSnapshots.getDocuments();
                Toast.makeText(ResultDetails.this, "Timings recorded successfully", Toast.LENGTH_SHORT).show();
                fetchQuestions(documentSnapshotList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ResultDetails.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void fetchQuestions(final List<DocumentSnapshot> documentSnapshotList){

        for (int i=0;i<documentSnapshotList.size();i++){
            FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
            String docid=documentSnapshotList.get(i).getString("questionId");
            if (docid.equals("")){
                questionsList.add("Null");
                count++;
            }else {
                firebaseFirestore.collection("questions").document(docid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        questionsList.add(documentSnapshot.getString("question"));
                        count++;
                        if (count==documentSnapshotList.size()){
                            String contentQue="  ";
                            for (int j=0;j<documentSnapshotList.size();j++){
                                contentQue=contentQue+" Question :"+questionsList.get(j)+" \n "+"Time :"+documentSnapshotList.get(j).get("timeTaken")+" \n \n ";
                            }
                            tv.setText(contentQue);
                        }


                    }
                });
            }
        }

    }
}
