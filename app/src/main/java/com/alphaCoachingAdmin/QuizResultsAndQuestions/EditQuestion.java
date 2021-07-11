package com.alphaCoachingAdmin.QuizResultsAndQuestions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoachingAdmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditQuestion extends AppCompatActivity {

    TextView question,option1,option2,option3,option4,correctOption,expectedtime;
    String mquestion,moption1,moption2,moption3,moption4;
    String image;
    int mexpectedtime,mcorrectOption;
    Button submit;
    String quizID,questionID;
    ImageView imageView;
    Button addbtn,removebtn;
    Uri temp;
    boolean flag=true;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        question=findViewById(R.id.editquestionfield);
        option1=findViewById(R.id.editoption1field);
        option2=findViewById(R.id.editoption2field);
        option3=findViewById(R.id.editoption3field);
        option4=findViewById(R.id.editoption4field);
        correctOption=findViewById(R.id.editcorrectoptionfield);
        expectedtime=findViewById(R.id.edittimefield);
        submit=findViewById(R.id.changequestionbtn);
        imageView=findViewById(R.id.editImageView);
        addbtn=findViewById(R.id.addQuestionImage);
        removebtn=findViewById(R.id.removeQuestionImage);
        storageRef=FirebaseStorage.getInstance().getReference();



        Intent intent1=getIntent();
        quizID=intent1.getStringExtra("quizID");
        questionID=intent1.getStringExtra("questionID");
        mquestion=intent1.getStringExtra("question");
        moption1=intent1.getStringExtra("option1");
        moption2=intent1.getStringExtra("option2");
        moption3=intent1.getStringExtra("option3");
        moption4=intent1.getStringExtra("option4");
        mcorrectOption=intent1.getIntExtra("correctOption",0);
        mexpectedtime=intent1.getIntExtra("time",0);
        image=intent1.getStringExtra("image");

        if (image!=null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("imagesQuestion").child(image);
            storageReference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    imageView.setVisibility(View.VISIBLE);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);

                }
            });
        }else {
            removebtn.setVisibility(View.GONE);
        }
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        removebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp=null;
                showImage(temp);
            }
        });

        question.setText(mquestion);
        option1.setText(moption1);
        option2.setText(moption2);
        option3.setText(moption3);
        option4.setText(moption4);
        correctOption.setText(""+mcorrectOption);
        expectedtime.setText(""+mexpectedtime);


        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("EDIT", "flag: "+flag);
                mquestion=question.getText().toString();
                moption1=option1.getText().toString();
                moption2=option2.getText().toString();
                moption3=option3.getText().toString();
                moption4=option4.getText().toString();
                try {
                    mcorrectOption = Integer.parseInt(correctOption.getText().toString());
                    mexpectedtime = Integer.parseInt(expectedtime.getText().toString());
                }catch (Exception e){
                    Toast.makeText(EditQuestion.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mcorrectOption>4||mcorrectOption<1){
                    Toast.makeText(EditQuestion.this, "Enter a valid correct option(1 to 4)", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mquestion.equals("")||moption1.equals("")||moption2.equals("")||moption3.equals("")||moption4.equals("")){
                    Toast.makeText(EditQuestion.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (flag){
                    //No change in IMAGE
                    Log.d("EDIT", "Entered if(true) "+flag);

                    Map<String, Object> datatosave = new HashMap<>();
                    datatosave.put("question",mquestion);
                    datatosave.put("option1",moption1);
                    datatosave.put("option2",moption2);
                    datatosave.put("option3",moption3);
                    datatosave.put("option4",moption4);
                    datatosave.put("correctOption",mcorrectOption);
                    datatosave.put("time",mexpectedtime);
                    datatosave.put("image",image);
                    datatosave.put("quizID",quizID);

                    FirebaseFirestore db=FirebaseFirestore.getInstance();
                    db.collection("questions").document(questionID).set(datatosave).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditQuestion.this, "Edited Successfully !", Toast.LENGTH_SHORT).show();
                            Log.d("EDIT", "if true done");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditQuestion.this, "Failed to edit,please try again", Toast.LENGTH_SHORT).show();
                        }
                    });

                    return;
                }else {
                    Log.d("EDIT", "Entered else part ");

                    //Image has been changed or removed
                    if (temp!=null){
                        //Upload the image first
                        Log.d("EDIT", "Entered if(temp!=null) part: ");

                        uploadImages(temp);

                    }else {
                        //No image
                        Log.d("EDIT", "Entered else temp ==null ");

                        Map<String, Object> datatosave = new HashMap<>();
                        datatosave.put("question",mquestion);
                        datatosave.put("option1",moption1);
                        datatosave.put("option2",moption2);
                        datatosave.put("option3",moption3);
                        datatosave.put("option4",moption4);
                        datatosave.put("correctOption",mcorrectOption);
                        datatosave.put("time",mexpectedtime);
                        datatosave.put("image",null);
                        datatosave.put("quizID",quizID);

                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        db.collection("questions").document(questionID).set(datatosave).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditQuestion.this, "Edited Successfully !", Toast.LENGTH_SHORT).show();
                                Log.d("EDIT", "done with null image");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditQuestion.this, "Failed to edit,please try again", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }



                }


            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //this temp variable will hold the selected file(data)
            flag=false;
            temp = data.getData();
            imageView.setVisibility(View.VISIBLE);
            removebtn.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),temp);
                imageView.setImageBitmap(bitmap);
                Log.d("EDIT", "image selected");

            }catch (Exception e){
                Toast.makeText(this, " "+e.getMessage(), Toast.LENGTH_SHORT).show();

            }


        }

    }
    public void showImage(Uri uri){
        flag=false;
        if (uri != null){
            temp=uri;
            imageView.setVisibility(View.VISIBLE);
            removebtn.setVisibility(View.VISIBLE);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                imageView.setImageBitmap(bitmap);
            }catch (Exception e){
                Toast.makeText(this, " "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            temp=null;
            imageView.setVisibility(View.GONE);
            removebtn.setVisibility(View.GONE);
        }
        Log.d("EDIT", "showImage flag "+flag);

    }

    //This method will be called when user clicks the addbtn for uploading all the data
    public void uploadImages(final Uri data) {
        Log.d("EDIT", "Uploading images ");

        String nameOfImg=""+System.currentTimeMillis()+""+UUID.randomUUID().toString();
        final StorageReference reference = storageRef.child("imagesQuestion/" +nameOfImg);
        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                Uri url = uri.getResult();
                //   imageUrl[num]=url.toString();
                String name=nameOfImg;

                Log.d("EDIT", "image uploaded "+name+" "+url.toString());

                Toast.makeText(EditQuestion.this, "Image Uploaded SuccessFully", Toast.LENGTH_SHORT).show();
                Map<String, Object> datatosave = new HashMap<>();
                datatosave.put("question",mquestion);
                datatosave.put("option1",moption1);
                datatosave.put("option2",moption2);
                datatosave.put("option3",moption3);
                datatosave.put("option4",moption4);
                datatosave.put("correctOption",mcorrectOption);
                datatosave.put("time",mexpectedtime);
                datatosave.put("image",name);
                datatosave.put("quizID",quizID);

                FirebaseFirestore db=FirebaseFirestore.getInstance();
                db.collection("questions").document(questionID).set(datatosave).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditQuestion.this, "Edited Successfully !", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditQuestion.this, "Failed to edit,please try again", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // WE CAN SHOW THE PROGRESS HERE WHILE DOWNLOADING IF WE WANT
            }
        });

    }

}
