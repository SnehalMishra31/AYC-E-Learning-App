package com.alphaCoachingAdmin.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.fragment.PDFfragment;
import com.alphaCoachingAdmin.fragment.QuizzesFragment;
import com.alphaCoachingAdmin.fragment.ResultAnalysisFragment;
import com.alphaCoachingAdmin.fragment.ResultQuestionsFragment;
import com.alphaCoachingAdmin.fragment.VideosFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResultsQuestionsAnalysis extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;

    ViewPagerAdapter viewPagerAdapter;
    private FirebaseFirestore FireStore=FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private static List<Question> questionList;
    private static List<QuizTakenQuestion> takenQuestionList;

    //Needed to be fetched before all methods run
    String quizTakenId,quizId;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_questions_analysis);

        Toast.makeText(this, "Please Wait till the data gets loaded", Toast.LENGTH_SHORT).show();

        toolbar=(Toolbar) findViewById(R.id.myToolBar2);
        tabLayout=(TabLayout) findViewById(R.id.tabLayout2);
        viewPager=(ViewPager) findViewById(R.id.myViewPager2);



        Intent intent1=getIntent();
        quizId=intent1.getStringExtra("quizID");
        quizTakenId=intent1.getStringExtra("id");
        userName=intent1.getStringExtra("name");



      //  getQuestionsList();


        toolbar.setTitle(userName);
        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);




    }
    private void setupViewPager(ViewPager viewPager){
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
//        viewPagerAdapter.addFragment(new ResultQuestionsFragment(questionList,takenQuestionList,quizId,quizTakenId),"Questions");
        viewPagerAdapter.addFragment(new ResultQuestionsFragment(quizTakenId,quizId),"Questions");
        viewPagerAdapter.addFragment(new ResultAnalysisFragment(quizId,quizTakenId),"Analysis");

        viewPager.setAdapter(viewPagerAdapter);
    }

    private void getQuestionsList() {
      //  mProgressBar.setVisibility(View.VISIBLE);
        //ArrayList to store the question class variable
        questionList = new ArrayList<>();
        FireStore.collection("questions").whereEqualTo("quizID", quizId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot questions = task.getResult();
                assert questions != null;
                for (QueryDocumentSnapshot doc : questions) {
                    Question question = doc.toObject(Question.class);
                    question.setId(doc.getId());
                    questionList.add(question);
                }
                getTakenQuestionList();
            } else {
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        final String[] MaxScore = {null};
        final String[] score = new String[1];
        documentReference = FireStore.collection("quizTaken").document(quizTakenId);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    score[0] = String.valueOf(documentSnapshot.get("score"));
                    MaxScore[0] = String.valueOf(documentSnapshot.get("TotalScore"));
                }
                String text = (score[0]) + "/" + MaxScore[0];
             //   textViewMarks.setText(text);
            } else {
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTakenQuestionList() {
        takenQuestionList = new ArrayList<>();
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference yourCollRef = rootRef.collection("quizTakenQuestions");
        Query query = yourCollRef.whereEqualTo("quizTakenId", quizTakenId);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    QuizTakenQuestion takenQuestion = documentSnapshot.toObject(QuizTakenQuestion.class);
                    assert takenQuestion != null;
                    takenQuestion.setId(documentSnapshot.getId());
                    takenQuestionList.add(takenQuestion);
                }
          //      setBackground();

               // viewPagerAdapter.notifyDataSetChanged();
                //setupViewPager(viewPager);
                //tabLayout.setupWithViewPager(viewPager);
            }
        });
    }
}
