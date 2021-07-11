package com.alphaCoachingAdmin.QuizResultsAndQuestions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.activity.Solutions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewQuizQuestions extends AppCompatActivity {
   public RecyclerView mrecyclerview;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    String quizID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quiz_questions);


        mrecyclerview=(RecyclerView)findViewById(R.id.questions_recyclerview);
        firebaseFirestore=FirebaseFirestore.getInstance();

       Intent intent=getIntent();
       quizID=intent.getStringExtra("quizID");
       Log.d("questions", "onCreate: "+quizID);
        Query query=firebaseFirestore.collection("questions").whereEqualTo("quizID",quizID);
        //.whereEqualTo("standard",mClass).whereEqualTo("subject",mSubject);
        FirestoreRecyclerOptions<QuestionsData> options=new FirestoreRecyclerOptions.Builder<QuestionsData>().setQuery(query, QuestionsData.class).build();

        firestoreRecyclerAdapter= new FirestoreRecyclerAdapter<QuestionsData, QuestionsViewHolder>(options) {
            @NonNull
            @Override
            public QuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item,parent,false);
                return new QuestionsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final QuestionsViewHolder holder, int position, @NonNull final QuestionsData model) {
                holder.question.setText(model.getQuestion());
                holder.questionID=getSnapshots().getSnapshot(position).getId();
                holder.opt1.setText("(1) "+model.getOption1());
                holder.opt2.setText("(2) "+model.getOption2());
                holder.opt3.setText("(3) "+model.getOption3());
                holder.opt4.setText("(4) "+model.getOption4());
                switch (model.getCorrectOption()){
                    case 1: holder.opt1.setTextColor(Color.GREEN); break;
                    case 2: holder.opt2.setTextColor(Color.GREEN); break;
                    case 3: holder.opt3.setTextColor(Color.GREEN); break;
                    case 4: holder.opt4.setTextColor(Color.GREEN); break;
                }

                holder.correcttv.setText("Correct option: "+model.getCorrectOption());
                holder.exptimetv.setText("Expected time : "+model.getTime());
                if (model.getImage()!=null) {
                  holder.imageView.setVisibility(View.VISIBLE);
                    holder.setImageView(model.getImage());
                }
                Log.d("questions", "onBind: "+model.getCorrectOption());


                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1=new Intent(ViewQuizQuestions.this,EditQuestion.class);
                        intent1.putExtra("quizID",quizID);
                        intent1.putExtra("questionID",holder.questionID);
                        intent1.putExtra("question",model.getQuestion());
                        intent1.putExtra("option1",model.getOption1());
                        intent1.putExtra("option2",model.getOption2());
                        intent1.putExtra("option3",model.getOption3());
                        intent1.putExtra("option4",model.getOption4());
                        intent1.putExtra("correctOption",model.getCorrectOption());
                        intent1.putExtra("image",model.getImage());
                        intent1.putExtra("time",model.getTime());

                        startActivity(intent1);
                    }
                });

                holder.solution.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1=new Intent(ViewQuizQuestions.this, Solutions.class);
                        intent1.putExtra("questionID",holder.questionID);
                        intent1.putExtra("question",model.getQuestion());
                        startActivity(intent1);
                    }
                });

            }
        };
        mrecyclerview.setLayoutManager(new LinearLayoutManager(ViewQuizQuestions.this));
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
        firestoreRecyclerAdapter.notifyDataSetChanged();
        firestoreRecyclerAdapter.startListening();
    }
}
