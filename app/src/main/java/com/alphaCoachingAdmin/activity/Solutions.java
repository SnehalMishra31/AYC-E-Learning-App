package com.alphaCoachingAdmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoachingAdmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Solutions extends AppCompatActivity {

    Button submit;
    EditText solution;
    TextView question;
    String mQuestion,mSolution,questionID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solutions);

        solution=findViewById(R.id.etSolution);
        submit=findViewById(R.id.submitSolution);
        question=findViewById(R.id.tvquestion);
        Intent intent1=getIntent();
        mQuestion=intent1.getStringExtra("question");
        questionID=intent1.getStringExtra("questionID");
        solution.setVisibility(View.GONE);
        Toast.makeText(Solutions.this, "Wait till old solution gets loaded...", Toast.LENGTH_SHORT).show();
        db.collection("questions").document(questionID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String oldSolution="";
                try {
                    oldSolution=documentSnapshot.get("solution").toString();
                }catch (Exception e){
                    solution.setVisibility(View.VISIBLE);
                    Toast.makeText(Solutions.this, "No solution is present,You can enter a new one now", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (oldSolution.equals("")){
                    solution.setVisibility(View.VISIBLE);
                    return;
                }
                solution.setVisibility(View.VISIBLE);
                solution.setText(oldSolution);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                solution.setVisibility(View.VISIBLE);
            }
        });

        if (mQuestion.equals("")){

        }else {
            question.setText("Question : "+mQuestion);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSolution=solution.getText().toString();
                if (mSolution.equals("")){
                    Toast.makeText(Solutions.this, "Please enter the solution", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> datatosave = new HashMap<>();
                datatosave.put("solution", mSolution);
               // datatosave.put("questionID", questionID);



                db.collection("questions").document(questionID).update(datatosave).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Solutions.this, " Data is successfully added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Solutions.this, "Failed to add data,please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}
