package com.alphaCoachingAdmin.activity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoachingAdmin.R;

public class QuizViewHolder extends RecyclerView.ViewHolder {

   public TextView quizName,quizTime,quizDate,quizQuestions;
  public   Button viewQuiz,viewQuizResults, quizActivate;
  public String quizID;



    public QuizViewHolder(@NonNull View itemView) {
        super(itemView);

        quizName=itemView.findViewById(R.id.quizName);
        quizTime=itemView.findViewById(R.id.quizTime);
        quizDate=itemView.findViewById(R.id.quizDate);
//        quizQuestions=itemView.findViewById(R.id.quizQuestions);
        viewQuiz=itemView.findViewById(R.id.viewQuiz);
        viewQuizResults=itemView.findViewById(R.id.viewQuizResults);
        quizActivate = itemView.findViewById(R.id.quizActivate);






    }
}
