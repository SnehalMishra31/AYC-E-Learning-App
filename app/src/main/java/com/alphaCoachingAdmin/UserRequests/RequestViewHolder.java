package com.alphaCoachingAdmin.UserRequests;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoachingAdmin.R;

public class RequestViewHolder extends RecyclerView.ViewHolder {

   public TextView userName,userClass;


    public RequestViewHolder(@NonNull View itemView) {
        super(itemView);
       userName=itemView.findViewById(R.id.requestUserName);
        userClass=itemView.findViewById(R.id.requestUserClass);







    }
}
