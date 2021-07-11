package com.alphaCoachingAdmin.fragment;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.activity.Question;
import com.alphaCoachingAdmin.activity.QuizTakenQuestion;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultAnalysisFragment extends Fragment {

    View v;
    private TextView textViewMarks;
    private TextView TotalAttempt, progressInPercent;
    private ProgressBar progressBar;
    private ProgressBar TwoprogressBar;
    private LinearLayout mProgressBar;
    private String quizId, quizTakenId;
    private Button ButtonQuestionReview;
    private FirebaseFirestore FireStore=FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private static List<Question> questionList;
    private static List<QuizTakenQuestion> takenQuestionList;

    public ResultAnalysisFragment() {

    }

    public ResultAnalysisFragment(String quizId, String quizTakenId) {
        this.quizId = quizId;
        this.quizTakenId = quizTakenId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_result_analysis, container, false);
        textViewMarks = v.findViewById(R.id.TotalMarks);
        ButtonQuestionReview = v.findViewById(R.id.ButtonQuestionReview);
        mProgressBar = v.findViewById(R.id.quizReviewProgressbar);
        progressInPercent = v.findViewById(R.id.accuracyProgressValue);
        TwoprogressBar = v.findViewById(R.id.twoprogress);

        getQuestionsList();
        ButtonQuestionReview.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), ResultsQuestionReview.class);
            i.putExtra("QuizId", quizId);
            i.putExtra("quickened", quizTakenId);
            startActivity(i);
        });
        return v;
    }

    private void getQuestionsList() {
        mProgressBar.setVisibility(View.VISIBLE);
        //ArrayList to store the question class variable
        questionList = new ArrayList<>();
        FireStore.collection(Constant.QUESTION_COLLECTION).whereEqualTo(Constant.QuestionCollectionFields.QUIZ_ID, quizId)
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
        documentReference = FireStore.collection(Constant.QUIZ_TAKEN_COLLECTION).document(quizTakenId);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    score[0] = String.valueOf(documentSnapshot.get(Constant.QuizTakenCollectionFields.SCORE));
                    MaxScore[0] = String.valueOf(documentSnapshot.get(Constant.QuizTakenCollectionFields.TOTAL_SCORE));
                }
                String text = (score[0]) + "/" + MaxScore[0];
                textViewMarks.setText(text);
            } else {
                Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBackground() {

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


        //following variables to display text in the progressbar and the accuracy
        int totalAttempts = 0;
        int totalCorrect = 0;
        int totalUnAttempts = 0;
        for (int j = 0; j < questionList.size(); j++) {
            if (questionBackground[j] != -1) {
                totalAttempts++;
                if (questionBackground[j] == 1) {
                    totalCorrect++;
                }
            } else
                totalUnAttempts++;
        }
        Log.d("QuizReviewActivity", "The no of 0 is : " + totalAttempts);
        Log.d("QuizReviewActivity", "The no of 1 is : " + totalCorrect);
        Log.d("QuizReviewActivity", "The no of -1 is : " + totalUnAttempts);
        TotalAttempt = v.findViewById(R.id.TotalAttempt);
        progressBar = v.findViewById(R.id.accuracyProgressBar);
        String accuracy = totalCorrect + "/" + totalAttempts;
        float accuracyPercentage = ((float) totalCorrect / (float) totalAttempts) * 100;
        DecimalFormat df = new DecimalFormat("#.##");
        accuracyPercentage = Float.parseFloat(df.format(accuracyPercentage));
        float attemptPercentage = ((float) totalAttempts / (float) questionList.size()) * 100;
        String text = totalAttempts + "/" + questionList.size() + " " + attemptPercentage + "%";

        Log.d("QuizReviewActivity", "The value of accuracy percentage  is : " + accuracyPercentage + totalCorrect + " / " + totalAttempts + " ");
        TotalAttempt.setText(text);
        progressInPercent.setText(accuracyPercentage + "%");
        progressBar.setMax(100);
        progressBar.setProgress((int) accuracyPercentage);

        //The secondary progress bar
        TextView textviewProgress = v.findViewById(R.id.textOfProgressBar);

        SpannableString text1 = new SpannableString("C/W/U : " + totalCorrect + "/" + (totalAttempts - totalCorrect) + "/" + totalUnAttempts);
        text1.setSpan(new ForegroundColorSpan(Color.GREEN), 8, 9, 0);
        text1.setSpan(new ForegroundColorSpan(Color.RED), 10, 11, 0);
        text1.setSpan(new ForegroundColorSpan(Color.LTGRAY), 12, text1.length(), 0);
        textviewProgress.setText(text1, TextView.BufferType.SPANNABLE);

        CircularProgressBar circularProgressBar = v.findViewById(R.id.circularProgressBar);
        // Set Progress
        circularProgressBar.setProgressMax(questionList.size());
        circularProgressBar.setProgress(totalCorrect);

        TextView textView1 = v.findViewById(R.id.centerText);
        float k = ((float)totalCorrect /(float) questionList.size()) * 100;
        k = Float.parseFloat(df.format(k));

        textView1.setText(k + "%");

        TwoprogressBar.setMax(questionList.size());

        // TwoprogressBar.setProgress(0);
        TwoprogressBar.setProgress(totalCorrect);
        TwoprogressBar.setSecondaryProgressTintList(ColorStateList.valueOf(Color.RED));
        TwoprogressBar.setSecondaryProgress(totalAttempts);
        // TwoprogressBar.setSecondaryProgress(4);
        Log.d("QuizReviewActivity", "-=-=-=-=-=-=-" + totalAttempts + " " + totalCorrect + " " + questionList.size());


        mProgressBar.setVisibility(View.GONE);
    }

    private QuizTakenQuestion getTakenQuestion(String id) {
        for (QuizTakenQuestion takenQuestion : takenQuestionList) {
            if (takenQuestion.getQuestionId().equals(id)) {
                return takenQuestion;
            }
        }
        return null;
    }

    private void getTakenQuestionList() {
        takenQuestionList = new ArrayList<>();
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference yourCollRef = rootRef.collection(Constant.QUIZ_TAKEN_QUESTION_COLLECTION);
        Query query = yourCollRef.whereEqualTo(Constant.QuizTakenQuestionsFields.QUIZ_TAKEN_ID, quizTakenId);
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
