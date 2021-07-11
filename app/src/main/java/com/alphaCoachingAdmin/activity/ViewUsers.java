package com.alphaCoachingAdmin.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.LocalDB.DbHelper;
import com.alphaCoachingAdmin.ModelClass.Standard;
import com.alphaCoachingAdmin.ModelClass.UserModel;
import com.alphaCoachingAdmin.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.alphaCoachingAdmin.AlphaApplication.getAppContext;

public class ViewUsers extends AppCompatActivity {
    private RecyclerView mrecyclerview;
    private FirebaseFirestore db;
    private List<String> standardList;
    private List<String> standardIdList;
    private Spinner spinnerClass;
    private ArrayAdapter<String> adapter;
    private String classID;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private Context mContext;
    private List<Standard> mStandardList;
    private DbHelper mDbHelper;
    private List<UserModel> mUserList;
    private ViewUserAdapter mUserAdapter;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        mrecyclerview = (RecyclerView) findViewById(R.id.users_recyclerview);
        spinnerClass = findViewById(R.id.viewuserClass);
        db = FirebaseFirestore.getInstance();
        standardList = new ArrayList<String>();
        standardIdList = new ArrayList<String>();
        mContext = getAppContext();
        mDbHelper = DbHelper.getInstance(mContext);
        mUserList = new ArrayList<>();
        mProgressDialog = new ProgressDialog(this);

        getclassList();
        adapter = new ArrayAdapter<String>(ViewUsers.this, android.R.layout.simple_spinner_dropdown_item, standardList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);

        showProgressDialogWithTitle("Loading, please wait!");
        db.collection(Constant.USER_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            UserModel user = snapshot.toObject(UserModel.class);
                            user.setDocumentId(snapshot.getId());
                            mUserList.add(user);
                        }
                        ArrayList<UserModel> userModels = new ArrayList<>();
                        for (UserModel userModel : mUserList) {
                            if (userModel.getStandard().equalsIgnoreCase(standardIdList.get(0))) {
                                userModels.add(userModel);
                            }
                        }
                        mUserAdapter = new ViewUserAdapter(userModels);
                        mrecyclerview.setLayoutManager(new LinearLayoutManager(ViewUsers.this));
                        mrecyclerview.setAdapter(mUserAdapter);
                        hideProgressDialogWithTitle();
                    }
                });


        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classID = standardIdList.get(position);
                if (classID != null && classID.equals("")) {
                } else {
                    //   showUsers(classID);
                    updateUI(classID);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    @Override
    public void onStop() {

        super.onStop();
//        firestoreRecyclerAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
//        firestoreRecyclerAdapter.startListening();
    }

    //    This function fetch the list of classes.
    private void getclassList() {
        mStandardList = mDbHelper.getAllStandards();
        if (mStandardList == null || mStandardList.isEmpty()) {
            db.collection(Constant.STANDARD_COLLECTION)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    standardList.add(document.get(Constant.StandardCollectionFields.STANDARD).toString());
                                    standardIdList.add(document.getId());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Please try again later.", Toast.LENGTH_LONG).show();
//                        hideProgressDialogWithTitle();
                        }
                    });
        } else {
            for (Standard standard : mStandardList) {
                standardList.add(String.valueOf(standard.getStandard()));
                standardIdList.add(standard.getId());
            }
        }
    }

    public void updateUI(String classID) {
        List<UserModel> mNewList = new ArrayList<>();
        for (UserModel userModel : mUserList) {
            if (userModel.getStandard().equalsIgnoreCase(classID)) {
                mNewList.add(userModel);
            }
        }
        mUserAdapter = new ViewUserAdapter(mNewList);
        mrecyclerview.setAdapter(mUserAdapter);
    }


    public class ViewUserAdapter extends RecyclerView.Adapter<ViewUserAdapter.UserListHolder> {

        List<UserModel> usersList;
//        ChangeActiveStatus onStatusChangeClick;

        public ViewUserAdapter(List<UserModel> list) {
            usersList = list;
//            onStatusChangeClick = status;
        }

        @NonNull
        @Override
        public UserListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_users, parent, false);
            return new UserListHolder(view, null);
        }

        @Override
        public void onBindViewHolder(@NonNull UserListHolder holder, int position) {
            if (position == -1) return;
            UserModel model = usersList.get(position);
            Log.d("Shubham", "MODEL: onBindViewHolder: " + model.getFirstName() + " " + model.getLastName());
            holder.userName.setText(model.getFirstName() + " " + model.getLastName());
            if (model.getIsActive()) {
//                holder.mStudentActiveStatus.setBackgroundColor(Color.GREEN);
                holder.mStudentActiveStatus.setTextColor(Color.GREEN);
                holder.mStudentActiveStatus.setText("ACTIVATED");
            } else {
//                holder.mStudentActiveStatus.setBackgroundColor(Color.RED);
                holder.mStudentActiveStatus.setTextColor(Color.RED);
                holder.mStudentActiveStatus.setText("INACTIVATED");
            }
            if (position != RecyclerView.NO_POSITION) {
//                UserModel model;
//                model = usersList.get(position);
                holder.mStudentActiveStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.getIsActive()) {
                            db.collection(Constant.USER_COLLECTION)
                                    .document(model.getDocumentId())
                                    .update(Constant.UserCollectionFields.ACTIVE_STATUS, false)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, "Error occured, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            model.setActive(model.isActiveStatus());
//                                            holder.mStudentActiveStatus.setBackgroundColor(Color.GREEN);
                                            holder.mStudentActiveStatus.setTextColor(Color.GREEN);
                                            holder.mStudentActiveStatus.setText("ACTIVATED");
                                        }
                                    });
//                            holder.mStudentActiveStatus.setBackgroundColor(Color.RED);
                            holder.mStudentActiveStatus.setTextColor(Color.RED);
                            holder.mStudentActiveStatus.setText("INACTIVATED");
                        } else {
                            db.collection(Constant.USER_COLLECTION)
                                    .document(model.getDocumentId())
                                    .update(Constant.UserCollectionFields.ACTIVE_STATUS, true)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, "Error occured, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            model.setActive(model.isActiveStatus());
//                                            holder.mStudentActiveStatus.setBackgroundColor(Color.RED);
                                            holder.mStudentActiveStatus.setTextColor(Color.RED);
                                            holder.mStudentActiveStatus.setText("INACTIVATED");
                                        }
                                    });
//                            holder.mStudentActiveStatus.setBackgroundColor(Color.GREEN);
                            holder.mStudentActiveStatus.setTextColor(Color.GREEN);
                            holder.mStudentActiveStatus.setText("ACTIVATED");
                        }
                        model.setActive(!model.getIsActive());
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return usersList.size();
        }

        public class UserListHolder extends RecyclerView.ViewHolder {

            public TextView userName;
            public TextView mStudentActiveStatus;
            private boolean isFromClick = false;


            public UserListHolder(@NonNull View itemView, OnStatusChange onStatusChange) {
                super(itemView);

                mStudentActiveStatus = itemView.findViewById(R.id.studentChangeActiveStatus);
                userName = itemView.findViewById(R.id.userName);
                int position = getAdapterPosition();
                Log.d("Shubham", "UserListHolder: position: " + position);
            }
        }
    }

    public interface OnStatusChange {
        void onStatusChangeClick(UserModel model, boolean isChecked);
    }


    private void showProgressDialogWithTitle(String substring) {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(substring);
        mProgressDialog.show();
    }

    private void hideProgressDialogWithTitle() {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.dismiss();
    }
}
