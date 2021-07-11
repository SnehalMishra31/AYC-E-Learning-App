package com.alphaCoachingAdmin.activity;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoachingAdmin.R;

public class UserViewHolder extends RecyclerView.ViewHolder {

   public TextView userName;
   public Switch mStudentActiveStatus;


    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        userName=itemView.findViewById(R.id.userName);
        mStudentActiveStatus = itemView.findViewById(R.id.studentChangeActiveStatus);
    }
}
