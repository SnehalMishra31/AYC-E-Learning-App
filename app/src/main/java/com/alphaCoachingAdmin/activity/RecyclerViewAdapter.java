package com.alphaCoachingAdmin.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoachingAdmin.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context mContext ;
    List<StudyData> mData;

    public RecyclerViewAdapter(Context mContext, List<StudyData> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v= LayoutInflater.from(mContext).inflate(R.layout.recycler_item,parent,false);
        MyViewHolder vHolder = new MyViewHolder(v);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.chapterName.setText(mData.get(position).getPDFName());
        holder.subTopic.setText(mData.get(position).getUrl());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView chapterName,subTopic;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            chapterName = (TextView) itemView.findViewById(R.id.chapterName);
            subTopic = (TextView) itemView.findViewById(R.id.subtopic);
        }
    }
}
