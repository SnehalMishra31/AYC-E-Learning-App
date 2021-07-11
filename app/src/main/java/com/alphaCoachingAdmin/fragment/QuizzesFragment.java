package com.alphaCoachingAdmin.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.Constant.FCMUtils;
import com.alphaCoachingAdmin.LocalDB.DbHelper;
import com.alphaCoachingAdmin.QuizResultsAndQuestions.ViewQuizQuestions;
import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.activity.FillQuestionsActivity;
import com.alphaCoachingAdmin.activity.QuizData;
import com.alphaCoachingAdmin.activity.QuizViewHolder;
import com.alphaCoachingAdmin.activity.testresults;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.alphaCoachingAdmin.AlphaApplication.getAppContext;


public class QuizzesFragment extends Fragment {

    private View v;
    private String mClass, mSubject;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private RecyclerView mrecyclerview;
    private Query query;
    private Button addQuiz;
    private final String ACTIVATE = "ACTIVATE";
    private final String IN_ACTIVATE = "IN-ACTIVATE";
    private ProgressDialog mProgressBar;
    private Context mContext;
    private DbHelper mDbHelper;

    public QuizzesFragment() {
    }

    public QuizzesFragment(String mClass, String mSubject) {
        this.mClass = mClass;
        this.mSubject = mSubject;
        mContext = getAppContext();
        mDbHelper = DbHelper.getInstance(mContext);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_quizzes, container, false);
        mrecyclerview = (RecyclerView) v.findViewById(R.id.quizzes_recyclerview);
        addQuiz = (Button) v.findViewById(R.id.addQuiz);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mProgressBar = new ProgressDialog(getActivity());
        query = firebaseFirestore.collection("quiz").whereEqualTo("standard", mClass).whereEqualTo("subject", mSubject);
        FirestoreRecyclerOptions<QuizData> options = new FirestoreRecyclerOptions.Builder<QuizData>().setQuery(query, QuizData.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<QuizData, QuizViewHolder>(options) {
            @NonNull
            @Override
            public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_list_item, parent, false);
                return new QuizViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final QuizViewHolder holder, int position, @NonNull final QuizData model) {

                holder.quizName.setText(model.getQuizName());
                holder.quizTime.setText("Total Time: " + model.getQuizTime() + ",\t" + model.getQuestionNumber() + " Questions");
//                holder.quizQuestions.setText("Questions: " + model.getQuestionNumber());
                holder.quizID = getSnapshots().getSnapshot(position).getId();
                if (model.getActiveStatus()) {
                    holder.quizActivate.setText(IN_ACTIVATE);
                    holder.quizActivate.setBackgroundColor(Color.GREEN);
                    holder.quizActivate.setTextColor(Color.BLACK);
                } else {
                    holder.quizActivate.setText(ACTIVATE);
                    holder.quizActivate.setBackgroundColor(Color.RED);
                    holder.quizActivate.setTextColor(Color.WHITE);
                }
                if (model.getQuizDate() != null) {
                    Timestamp timestamp = model.getQuizDate();
                    Date date = timestamp.toDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");
                    String currentDateandTime = sdf.format(date);
                    holder.quizDate.setText("Date: " + currentDateandTime);
                }
                holder.viewQuizResults.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), testresults.class);
                        intent.putExtra("quizID", holder.quizID);
                        startActivity(intent);
                    }
                });
                holder.viewQuiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ViewQuizQuestions.class);
                        intent.putExtra("quizID", holder.quizID);
                        startActivity(intent);
                    }
                });
                holder.quizActivate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialogWithTitle("Updating Your Changes");
                        Map<String, Object> dataToUpdate = new HashMap<>();
                        if (model.getActiveStatus()) {
                            dataToUpdate.put(Constant.QuizCollectionFields.STATUS, false);
                        } else {
                            dataToUpdate.put(Constant.QuizCollectionFields.STATUS, true);
                        }
                        firebaseFirestore.collection(Constant.QUIZ_COLLECTION)
                                .document(holder.quizID)
                                .update(dataToUpdate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (!model.getActiveStatus()) {
                                            holder.quizActivate.setText(IN_ACTIVATE);
                                            holder.quizActivate.setBackgroundColor(Color.GREEN);
                                        } else {
                                            holder.quizActivate.setText(ACTIVATE);
                                            holder.quizActivate.setBackgroundColor(Color.RED);
                                        }
                                        hideProgressDialogWithTitle();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Try again later", Toast.LENGTH_LONG).show();
                                        hideProgressDialogWithTitle();
                                    }
                                });
                    }
                });


            }
        };

        addQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FillQuestionsActivity.class);
                startActivity(intent);
            }
        });
        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mrecyclerview.setAdapter(firestoreRecyclerAdapter);
        return v;
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
