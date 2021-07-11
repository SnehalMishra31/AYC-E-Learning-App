package com.alphaCoachingAdmin.UserRequests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alphaCoachingAdmin.PDFviewer;
import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.activity.StudyData;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RequestsUsers extends AppCompatActivity {
    private RecyclerView mrecyclerview;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_users);
        mrecyclerview = (RecyclerView)findViewById(R.id.requests_recyclerview);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mProgressBar = new ProgressDialog(this);

        showProgressDialogWithTitle("Fetching Your Date");
        Query query = firebaseFirestore.collection("requested_user_list");
        FirestoreRecyclerOptions<UserRequestData> options = new FirestoreRecyclerOptions.Builder<UserRequestData>().setQuery(query, UserRequestData.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<UserRequestData, RequestViewHolder>(options) {
            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requests_item, parent, false);
                hideProgressDialogWithTitle();
                return new RequestViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull UserRequestData model) {
               holder.userName.setText(model.getStudent_name());
               holder.userClass.setText(model.getStudent_class());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     Intent intent = new Intent(RequestsUsers.this, RequestDetails.class);
                      intent.putExtra("name", model.getStudent_name());
                        intent.putExtra("class", model.getStudent_class());
                        intent.putExtra("reference", model.getReference_name());
                        intent.putExtra("marks", model.getPrevious_class_percentage());
                        intent.putExtra("address", model.getStudent_address());
                        intent.putExtra("mobile", model.getStudent_contact_number());
                       startActivity(intent);
                    }
                });
            }
        };
        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(RequestsUsers.this));
        mrecyclerview.setAdapter(firestoreRecyclerAdapter);
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
