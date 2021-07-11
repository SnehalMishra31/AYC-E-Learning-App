package com.alphaCoachingAdmin.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.activity.FillQuestionsActivity;
import com.alphaCoachingAdmin.activity.VideoData;
import com.alphaCoachingAdmin.activity.VideoSubcategory;
import com.alphaCoachingAdmin.activity.VideoViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VideosFragment extends Fragment {

    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String date;
    String mClass,mSubject;
    FirebaseFirestore firebaseFirestore,db;
    FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    RecyclerView mrecyclerview;
    View v;
    Button addVideo,cancel,addcategory;
    CardView cardView;
    EditText etcategoryname;
    ProgressBar pgbar;

    String store;
    List<String> StoreID;
    Query query;
    private ProgressDialog mProgressBar;

    public VideosFragment() {

    }

    public VideosFragment(String mClass, String mSubject) {
        this.mClass = mClass;
        this.mSubject = mSubject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_videos, container, false);
        mrecyclerview=(RecyclerView) v.findViewById(R.id.video_recyclerview);
        cardView=(CardView) v.findViewById(R.id.cardview);
        addVideo=(Button)v.findViewById(R.id.addVideo);
        cancel=(Button) v.findViewById(R.id.cancel);
//        pgbar=(ProgressBar) v.findViewById(R.id.pgbar);
        etcategoryname=(EditText) v.findViewById(R.id.etcategoryname);
        addcategory=(Button)v.findViewById(R.id.addcategory);
        mProgressBar = new ProgressDialog(getActivity());



        addcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String categoryName=etcategoryname.getText().toString();

                if (categoryName.equals("")){
                    Toast.makeText(getActivity(), "Please enter the name", Toast.LENGTH_SHORT).show();
                    return;
                }
//                pgbar.setVisibility(View.VISIBLE);
                showProgressDialogWithTitle("Storing Your Data");
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                date = simpleDateFormat.format(calendar.getTime());

                Map<String,Object> datasave=new HashMap<>();
                datasave.put("name",categoryName);
                datasave.put("standard",mClass);
                datasave.put("subject",mSubject);
                datasave.put("uid",date);
                db.collection("categoryVideo").document(date).set(datasave).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Category is added successfully  ", Toast.LENGTH_SHORT).show();
                        etcategoryname.setText("");
//                        pgbar.setVisibility(View.GONE);
                        hideProgressDialogWithTitle();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        pgbar.setVisibility(View.GONE);
                        hideProgressDialogWithTitle();
                        Toast.makeText(getActivity(), "Failed to add category, try again", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardView.setVisibility(View.GONE);
            }
        });

        firebaseFirestore=FirebaseFirestore.getInstance();
        db=FirebaseFirestore.getInstance();
        StoreID =new ArrayList<String>();


         query=firebaseFirestore.collection("categoryVideo").whereEqualTo("standard",mClass).whereEqualTo("subject",mSubject);





        FirestoreRecyclerOptions<VideoData> options=new FirestoreRecyclerOptions.Builder<VideoData>().setQuery(query,VideoData.class).build();
        firestoreRecyclerAdapter= new FirestoreRecyclerAdapter<VideoData, VideoViewHolder>(options) {
            @NonNull
            @Override
            public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item,parent,false);
                return new VideoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull VideoViewHolder holder, final int position, @NonNull final VideoData model) {
               holder.categoryName.setText(model.getName());
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure you want to delete this "+model.getName()+" Video Category ?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fetchDocuments(getSnapshots().getSnapshot(position).getId());
                            }
                        }).setNegativeButton("Cancel",null);
                        AlertDialog alertDialog=builder.create();
                        alertDialog.show();
                    }
                });
               holder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent=new Intent(getActivity(), VideoSubcategory.class);
                       intent.putExtra("uid",model.getUid());
                       intent.putExtra("class",mClass);
                       intent.putExtra("subject",mSubject);

                       startActivity(intent);
                   }
               });




            }
        };

        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mrecyclerview.setAdapter(firestoreRecyclerAdapter);

        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.VISIBLE);
            }
        });




        return v;
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


    public void fetchDocuments(String docID){
        FirebaseFirestore fire=FirebaseFirestore.getInstance();
        fire.collection("video").whereEqualTo("category",docID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if ( task.isSuccessful ()) {
                    StoreID.clear();
                    for ( QueryDocumentSnapshot document : task . getResult ()) {
                        StoreID.add(document.getId());
                    }
                    Toast.makeText(getActivity(),"All data is fetched!",Toast.LENGTH_LONG).show();
                    deleteVideos(docID,StoreID);
                }else {
                    Toast.makeText(getActivity(),"Something went wrong,please try again",Toast.LENGTH_LONG).show();
                }
            }
        });



    }
    public void deleteVideos(String docID,List<String> storeID){

        for (int i=0;i<storeID.size();i++){
            db.collection("video").document(storeID.get(i))
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to Delete the document", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        deleteDocument(docID);


    }

    public void deleteDocument(String docID){
        FirebaseFirestore db=FirebaseFirestore.getInstance();


        db.collection("categoryVideo").document(docID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Document deleted successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to Delete the document", Toast.LENGTH_SHORT).show();

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
