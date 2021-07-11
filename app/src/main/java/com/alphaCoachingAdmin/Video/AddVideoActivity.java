package com.alphaCoachingAdmin.Video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.Constant.FCMUtils;
import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.activity.FillQuizDataActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddVideoActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button btn;
    EditText getname, geturl;
    String name, category, subject, standard, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);
        btn = findViewById(R.id.addvid);
        getname = findViewById(R.id.getvidname);
        geturl = findViewById(R.id.geturl);
        Intent intent = getIntent();
        standard = intent.getStringExtra("class");
        subject = intent.getStringExtra("subject");
        category = intent.getStringExtra("uid");


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = getname.getText().toString();
                url = geturl.getText().toString();


                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> datatosave = new HashMap<>();
                datatosave.put("name", name);
                datatosave.put("standard", standard);
                datatosave.put("subject", subject);
                datatosave.put("category", category);
                datatosave.put("url", url);


                if (name.equals("") || url.equals("")) {
                    Toast.makeText(AddVideoActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("video").document().set(datatosave).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddVideoActivity.this, "Video successfully added", Toast.LENGTH_SHORT).show();
                        notifyAllStudents(standard, url, name);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddVideoActivity.this, "Failed to add video", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void notifyAllStudents(String standard, final String url, String videoName) {
        final ArrayList<String> tokenList = new ArrayList<>();
        db.collection(Constant.USER_COLLECTION)
                .whereEqualTo(Constant.UserCollectionFields.STANDARD, standard)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                if (snapshot.get("token") != null)
                                    tokenList.add(snapshot.get("token").toString());
                            }
                            FCMUtils.sendPushMessage(AddVideoActivity.this, tokenList, "video", url, videoName);
                        }
                    }
                });
    }
}
