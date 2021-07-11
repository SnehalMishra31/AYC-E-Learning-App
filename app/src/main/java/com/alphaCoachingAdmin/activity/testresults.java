package com.alphaCoachingAdmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alphaCoachingAdmin.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class testresults extends AppCompatActivity {

    RecyclerView mrecyclerview;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;

    String quizID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testresults);

        final Intent intent=getIntent();
        quizID=intent.getStringExtra("quizID");
        Log.d("resultsid", "onCreate: "+quizID);

        mrecyclerview=(RecyclerView)findViewById(R.id.testing_recyclerview);
        firebaseFirestore=FirebaseFirestore.getInstance();

        Query query=firebaseFirestore.collection("quizTaken").whereEqualTo("quizId",quizID).orderBy("score",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<TestDataResults> options=new FirestoreRecyclerOptions.Builder<TestDataResults>().setQuery(query,TestDataResults.class).build();

        firestoreRecyclerAdapter= new FirestoreRecyclerAdapter<TestDataResults, TestResultViewHolder>(options) {
            @NonNull
            @Override
            public TestResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item,parent,false);

                return new TestResultViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final TestResultViewHolder holder, final int position, @NonNull final TestDataResults model) {
                holder.name.setText(model.getUserName());
                holder.userID=model.getUserId();
                holder.marks.setText(" "+model.getScore()+"/"+model.getTotalScore());
                holder.details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                //        Intent intent1=new Intent(testresults.this,ResultDetails.class);
                        Intent intent1=new Intent(testresults.this,ResultsQuestionsAnalysis.class);
                        intent1.putExtra("id",getSnapshots().getSnapshot(position).getId());
                        intent1.putExtra("quizID",quizID);
                        intent1.putExtra("name",model.getUserName());
                        intent1.putExtra("userID",holder.userID);
                        intent1.putExtra("score",model.getScore());
                        intent1.putExtra("total",model.TotalScore);
                        startActivity(intent1);
                    }
                });


            }
        };
        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mrecyclerview.setAdapter(firestoreRecyclerAdapter);

    }

    @Override
    public void onStop() {

        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }
}
