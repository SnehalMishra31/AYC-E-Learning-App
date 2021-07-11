package com.alphaCoachingAdmin.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.alphaCoachingAdmin.GridAdapter;
import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.activity.Question;
import com.alphaCoachingAdmin.activity.QuizTakenQuestion;
import com.alphaCoachingAdmin.activity.ResultsSingleQuestionDetails;
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


public class ResultQuestionsFragment extends Fragment {

    private FirebaseFirestore FireStore=FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private static List<Question> questionList;
    private static List<QuizTakenQuestion> takenQuestionList;

    View v;
    private GridView gridView;

   //  List<Question> questionList;
//     List<QuizTakenQuestion> takenQuestionList;
    String quizTakenId,quizId;

    public ResultQuestionsFragment(String quizTakenId, String quizId) {
        this.quizTakenId = quizTakenId;
        this.quizId = quizId;
    }

    /*  public ResultQuestionsFragment(List<Question> questionList,List<QuizTakenQuestion> takenQuestionList,String quizId,String quizTakenId){
             this.questionList=questionList;
             this.takenQuestionList=takenQuestionList;
             this.quizId=quizId;
             this.quizTakenId=quizTakenId;
         }

       */
    public ResultQuestionsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v=inflater.inflate(R.layout.fragment_result_questions, container, false);
        gridView=v.findViewById(R.id.grid);

        getQuestionsList();

      /*  if (questionList.isEmpty()||takenQuestionList.isEmpty()||quizId.isEmpty()||quizTakenId.isEmpty()){

        }else{
            setBackground();
        }

       */
        return v;
    }

    void setBackground(){
        //Array to store the grid text
        String[] number = new String[questionList.size()];

        //Array to store the background of the  grid item
        int[] questionBackground = new int[questionList.size()];

        //Main iteration over all the questions from the quizTakenQuestions Collection
        for (int i = 0; i < questionList.size(); i++) {
            int n = (int) questionList.get(i).getCorrectOption();
            String ans = "";
            switch (n) {
                case 1:
                    ans = (questionList.get(i).getOption1());
                    break;
                case 2:
                    ans = (questionList.get(i).getOption2());
                    break;
                case 3:
                    ans = (questionList.get(i).getOption3());
                    break;
                case 4:
                    ans = (questionList.get(i).getOption4());
                    break;
            }
            String finalAns = ans;
            int background;
            QuizTakenQuestion takenQuestion = getTakenQuestion(questionList.get(i).getId());
            if (takenQuestion == null) {
                return;
            }
            String attemptedAns = takenQuestion.getAttemptedAnswer();
            if (attemptedAns == null) {
                background = -1;
            } else if (attemptedAns.equals(finalAns)) {
                background = 1;
            } else {
                background = 0;
            }
            questionBackground[i] = background;
        }
        for (int i = 0; i < questionList.size(); i++) {
            number[i] = String.valueOf(i + 1);
        }
        GridAdapter adapter = new GridAdapter(getContext(), number, questionBackground);
        gridView.setAdapter(adapter);

        //OnItemClickListener on the grid item
        gridView.setOnItemClickListener((adapterView, view, position, l) -> {
           Intent i = new Intent(getActivity(), ResultsSingleQuestionDetails.class);
            i.putExtra("QuizId", quizId);
            i.putExtra("quickened", quizTakenId);
            i.putExtra("questionNumber", position);
            startActivity(i);


        });

    }
    private QuizTakenQuestion getTakenQuestion(String id) {
        for (QuizTakenQuestion takenQuestion : takenQuestionList) {
            if (takenQuestion.getQuestionId().equals(id)) {
                return takenQuestion;
            }
        }
        return null;
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
                Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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
                    setBackground();


            }
        });
    }

}
