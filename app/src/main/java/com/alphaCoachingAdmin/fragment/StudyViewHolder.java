package com.alphaCoachingAdmin.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoachingAdmin.R;

import static com.alphaCoachingAdmin.AlphaApplication.getAppContext;
import static com.alphaCoachingAdmin.Utils.UserSharedPreferenceManager.isMasterRole;

class StudyViewHolder extends RecyclerView.ViewHolder {

    TextView chapterNametv;
    String pdfurl;
    String docID;
    Button delete;

    public StudyViewHolder(@NonNull View itemView) {
        super(itemView);
        chapterNametv = itemView.findViewById(R.id.chapterName);
        delete = itemView.findViewById(R.id.deletePDF);
        if (isMasterRole(getAppContext())) {
            delete.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }
    }

}
