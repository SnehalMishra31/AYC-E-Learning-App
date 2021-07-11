package com.alphaCoachingAdmin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.Video.AddVideoActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class VideoSubcategory extends AppCompatActivity {

    RecyclerView mrecyclerview;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    String uid, mClass, mSubject;
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_subcategory);
        add = findViewById(R.id.addSubcategoryVideo);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoSubcategory.this, AddVideoActivity.class);
                intent.putExtra("class", mClass);
                intent.putExtra("subject", mSubject);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        final Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        mClass = intent.getStringExtra("class");
        mSubject = intent.getStringExtra("subject");
        mrecyclerview = (RecyclerView) findViewById(R.id.subcategory_video_recyclerview);
        firebaseFirestore = FirebaseFirestore.getInstance();

        String TAG = "New one";
        Log.d(TAG, "check: " + uid);
        Query query = firebaseFirestore.collection("video").whereEqualTo("category", uid);

        FirestoreRecyclerOptions<ModelVideoSubcategory> options = new FirestoreRecyclerOptions.Builder<ModelVideoSubcategory>().setQuery(query, ModelVideoSubcategory.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<ModelVideoSubcategory, SubVideoViewHolder>(options) {
            @NonNull
            @Override
            public SubVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_recycler_item, parent, false);

                return new SubVideoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final SubVideoViewHolder holder, int position, @NonNull final ModelVideoSubcategory model) {

                holder.subCategoryVideo.setText(model.getName());
                holder.videoTitle = model.getName();
                holder.videoURL = model.getUrl();
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(VideoSubcategory.this);
                        builder.setMessage("Are you sure you want to delete this " + model.getName() + " Video ?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDocument(getSnapshots().getSnapshot(position).getId());
                            }
                        }).setNegativeButton("Cancel", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(VideoSubcategory.this, VideoDisplay.class);
                        intent1.putExtra("title", holder.videoTitle);
                        intent1.putExtra("url", holder.videoURL);
                        startActivity(intent1);
                    }
                });
            }
        };
        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mrecyclerview.setAdapter(firestoreRecyclerAdapter);

    }

    private class SubVideoViewHolder extends RecyclerView.ViewHolder {

        TextView subCategoryVideo;
        String videoURL, videoTitle;
        Button delete;


        public SubVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            subCategoryVideo = itemView.findViewById(R.id.videoSubcategoryName);
            delete = itemView.findViewById(R.id.deleteVideo);
        }
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

    public void deleteDocument(String docID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("video").document(docID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(VideoSubcategory.this, "Document deleted successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VideoSubcategory.this, "Failed to Delete the document", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
