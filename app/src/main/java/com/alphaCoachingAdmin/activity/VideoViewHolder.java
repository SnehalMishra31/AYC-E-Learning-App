package com.alphaCoachingAdmin.activity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoachingAdmin.R;

import static com.alphaCoachingAdmin.AlphaApplication.getAppContext;
import static com.alphaCoachingAdmin.Utils.UserSharedPreferenceManager.isMasterRole;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    public TextView categoryName;

    public Button delete;


    public VideoViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryName = itemView.findViewById(R.id.categoryName);
        delete = itemView.findViewById(R.id.deleteVideoCategory);

        if (isMasterRole(getAppContext())) {
            delete.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }

    }
}
