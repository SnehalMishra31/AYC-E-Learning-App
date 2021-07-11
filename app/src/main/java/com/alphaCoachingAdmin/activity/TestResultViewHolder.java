package com.alphaCoachingAdmin.activity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoachingAdmin.R;

public class TestResultViewHolder extends RecyclerView.ViewHolder {

   public TextView name,marks;

   public String userID;
   public Button details;


    public TestResultViewHolder(@NonNull View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.testuserName);
        marks=itemView.findViewById(R.id.testmarks);
        details=itemView.findViewById(R.id.deatilsbtn);






    }
}
