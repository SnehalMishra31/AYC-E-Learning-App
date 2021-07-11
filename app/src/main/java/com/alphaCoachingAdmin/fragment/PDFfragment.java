package com.alphaCoachingAdmin.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alphaCoachingAdmin.PDFviewer;
import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.activity.AddingPDF;
import com.alphaCoachingAdmin.activity.StudyData;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class PDFfragment extends Fragment {

    private View v;
    private String mClass, mSubject;
    private RecyclerView mrecyclerview;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private Button addPDF;
    private ProgressDialog mProgressBar;

    public PDFfragment() {

    }

    public PDFfragment(String mClass, String mSubject) {
        this.mClass = mClass;
        this.mSubject = mSubject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pdffragment, container, false);
        mrecyclerview = (RecyclerView) v.findViewById(R.id.pdf_recyclerview);
        addPDF = (Button) v.findViewById(R.id.addPDF);
        mProgressBar = new ProgressDialog(getActivity());

        firebaseFirestore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();

        showProgressDialogWithTitle("Wait a while");
        Query query = firebaseFirestore.collection("PDF").whereEqualTo("standard", mClass).whereEqualTo("subject", mSubject);
        FirestoreRecyclerOptions<StudyData> options = new FirestoreRecyclerOptions.Builder<StudyData>().setQuery(query, StudyData.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<StudyData, StudyViewHolder>(options) {
            @NonNull
            @Override
            public StudyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
                hideProgressDialogWithTitle();
                return new StudyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final StudyViewHolder holder, int position, @NonNull StudyData model) {
                holder.chapterNametv.setText(model.getPDFName());
                holder.pdfurl = model.getUrl();
                holder.docID = getSnapshots().getSnapshot(position).getId();
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure you want to delete this " + model.getPDFName() + " PDF ?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFile(holder.pdfurl, holder.docID);
                            }
                        }).setNegativeButton("Cancel", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();


                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PDFviewer.class);
                        intent.putExtra("url", holder.pdfurl);
                        startActivity(intent);
                    }
                });
            }
        };
        hideProgressDialogWithTitle();
        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mrecyclerview.setAdapter(firestoreRecyclerAdapter);

        addPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddingPDF.class);
                intent.putExtra("class", mClass);
                intent.putExtra("subject", mSubject);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void deleteFile(String url, String docID) {
        showProgressDialogWithTitle("Deleting Your Data");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(getActivity(), "File deleted,Deleting the document now", Toast.LENGTH_SHORT).show();
                hideProgressDialogWithTitle();
                deleteDocument(docID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed in Deleting the File", Toast.LENGTH_SHORT).show();
                hideProgressDialogWithTitle();
            }
        });
    }

    public void deleteDocument(String docID) {
        showProgressDialogWithTitle("Deleting Your Data");
        db.collection("PDF").document(docID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "File and Document deleted successfully", Toast.LENGTH_SHORT).show();
                        hideProgressDialogWithTitle();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed in Deleting the document now", Toast.LENGTH_SHORT).show();
                        hideProgressDialogWithTitle();
                    }
                });
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
