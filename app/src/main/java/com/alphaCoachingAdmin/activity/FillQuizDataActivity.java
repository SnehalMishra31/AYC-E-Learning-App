package com.alphaCoachingAdmin.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.Constant.FCMUtils;
import com.alphaCoachingAdmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FillQuizDataActivity extends AppCompatActivity {

    String mClass, mSubject;
    //these are the views for taking input from user
    EditText question, option1, option2, option3, option4, correctoption, questiontime;
    //this textview display the current number of the question
    TextView displaynumber;
    //these are buttons for moving to next and previous questions
    Button next, previous, submit;
    //FirebaseFirestore Reference
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String QUESTION = "question";
    public static final String OPTION1 = "option1";
    public static final String OPTION2 = "option2";
    public static final String OPTION3 = "option3";
    public static final String OPTION4 = "option4";
    public static final String CORRECT_OPTION = "correctOption";
    public static final String TIME = "time";
    ProgressBar progressBar;
    //these will store the details of the quiz
    String sname;
    int squestions, stime;
    long timelong;
    //the count variable will refer to the current question index
//    int count;
    //This will store the generated ID of the Quiz Document
    String quizID;

    //images
    Uri imagesList[];
    ImageView imageView;
    StorageReference storageReference;
    Uri temp;
    String imageUrl[];
    Button addImage , removeImage;

    int countTemp=0;

    private int mQuestionNumber;
    private HashMap<Integer, Question> mQuestionsToBeStore;
    private String mQuestion, mOption1, mOption2, mOption3, mOption4;
    private int mQuestionTime, mCorrectOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_data);

        //taking quiz details from previous activity
        final Intent intent = getIntent();
        sname = intent.getStringExtra("quizname");
        squestions = intent.getIntExtra("quizquestions", 0);
        stime = intent.getIntExtra("quiztime", 0);
        timelong = intent.getLongExtra("quizdate", 0);
        mClass = intent.getStringExtra("mClass");
        mSubject = intent.getStringExtra("mSubject");
        progressBar = findViewById(R.id.progress_circular);
        //defining the views
        question = (EditText) findViewById(R.id.question);
        option1 = (EditText) findViewById(R.id.option1);
        option2 = (EditText) findViewById(R.id.option2);
        option3 = (EditText) findViewById(R.id.option3);
        option4 = (EditText) findViewById(R.id.option4);
        correctoption = (EditText) findViewById(R.id.correctoption);
        questiontime = (EditText) findViewById(R.id.questiontime);
        displaynumber = (TextView) findViewById(R.id.displaynumber);
        next = (Button) findViewById(R.id.nextquestion);
        previous = (Button) findViewById(R.id.previousquestion);
        submit = (Button) findViewById(R.id.submitquestion);
        addImage = (Button) findViewById(R.id.addImage);
        removeImage = (Button) findViewById(R.id.removeImage);
        imageView = (ImageView) findViewById(R.id.imageQuestion);
        submit.setEnabled(false);

        mQuestionNumber = 0;
        mQuestionsToBeStore = new HashMap<>();
        storageReference = FirebaseStorage.getInstance().getReference();


        //Initializing the arrays
        imageUrl = new String[squestions];
        imagesList=new Uri[squestions];

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestionNumber>=0&&mQuestionNumber<squestions){
                  imagesList[mQuestionNumber]=null;
                  showImage(imagesList[mQuestionNumber]);
                }

            }
        });
        //NEXT BUTTON :-
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code will execute only if count is less than the max questions
                if (mQuestionNumber < squestions) {
                    Question questionModel = new Question();
                    try {
                        mQuestion = question.getText().toString();
                        mOption1 = option1.getText().toString();
                        mOption2 = option2.getText().toString();
                        mOption3 = option3.getText().toString();
                        mOption4 = option4.getText().toString();
                        mCorrectOption = Integer.parseInt(correctoption.getText().toString());
                        mQuestionTime = Integer.parseInt(questiontime.getText().toString());

                        imagesList[mQuestionNumber]=temp;
                        temp=null;
                    } catch (Exception e) {
                        Toast.makeText(FillQuizDataActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //If some fields are left empty
                    if (mQuestion.isEmpty() || mOption1.isEmpty() || mOption2.isEmpty() || mOption3.isEmpty() || mOption4.isEmpty() || mCorrectOption == 0 || mQuestionTime == 0) {
                        Toast.makeText(FillQuizDataActivity.this, "Please enter all details,make sure time is not zero", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //If user inputs an incorrect or invalid option number
                    if (mCorrectOption > 4 || mCorrectOption < 1) {
                        Toast.makeText(FillQuizDataActivity.this, "You have entered invalid correct option value", Toast.LENGTH_SHORT).show();
                        return;

                    }

                    questionModel.setQuestion(mQuestion);
                    questionModel.setCorrectOption(mCorrectOption);
                    questionModel.setOption1(mOption1);
                    questionModel.setOption2(mOption2);
                    questionModel.setOption3(mOption3);
                    questionModel.setOption4(mOption4);
                    questionModel.setTime(mQuestionTime);
                    mQuestionsToBeStore.put(mQuestionNumber, questionModel);

                    mQuestionNumber++;
                    if (mQuestionNumber == squestions) {
                        //End of questions
                        submit.setEnabled(true);
                        Toast.makeText(FillQuizDataActivity.this, "This was the last question,please SUBMIT now", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    questionModel = mQuestionsToBeStore.get(mQuestionNumber);

                    if (questionModel == null) {
                        question.setText("");
                        option1.setText("");
                        option2.setText("");
                        option3.setText("");
                        option4.setText("");
                        correctoption.setText("");
                        questiontime.setText("");
                    } else {
                        question.setText(questionModel.getQuestion());
                        option1.setText(questionModel.getOption1());
                        option2.setText(questionModel.getOption2());
                        option3.setText(questionModel.getOption3());
                        option4.setText(questionModel.getOption4());
                        correctoption.setText(String.valueOf(questionModel.getCorrectOption()));
                        questiontime.setText(String.valueOf(questionModel.getTime()));
                    }
                    if (mQuestionNumber < imagesList.length)
                        showImage(imagesList[mQuestionNumber]);

                    //Displaying the current question number
                    displaynumber.setText(String.valueOf(mQuestionNumber + 1));
                }
            }
        });


        //PREVIOUS BUTTON :-
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestionNumber > 0) {
//                    count--;
                    storeCurrentQuestion(mQuestionNumber);
                    mQuestionNumber--;
                    Question questionModel = mQuestionsToBeStore.get(mQuestionNumber);
                    question.setText(questionModel.getQuestion());
                    option1.setText(questionModel.getOption1());
                    option2.setText(questionModel.getOption2());
                    option3.setText(questionModel.getOption3());
                    option4.setText(questionModel.getOption4());
                    correctoption.setText(String.valueOf(questionModel.getCorrectOption()));
                    showImage(imagesList[mQuestionNumber]);
                    questiontime.setText(String.valueOf(questionModel.getTime()));
                    displaynumber.setText(String.valueOf(mQuestionNumber + 1));

                }
            }
        });

        //SUBMIT BUTTON :-
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                //Quiz details
                Timestamp timestamp = new Timestamp(timelong);
                Map<String, Object> datatosave = new HashMap<>();
                datatosave.put("quizName", sname);
                datatosave.put("questionNumber", squestions);
                datatosave.put("quizTime", stime);
                datatosave.put("standard", mClass);
                datatosave.put("subject", mSubject);
                datatosave.put("quizDate", timestamp);
                datatosave.put(Constant.QuizCollectionFields.STATUS, false);
                db.collection("quiz").add(datatosave).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Document ID
                        quizID = "" + documentReference.getId();

                        int countImages=0;
                        //calling upload method for uploading questions
                        for(int c=0;c<squestions;c++){
                            if (imagesList[c]!=null){
                                countImages++;
                            }
                        }
                        uploadQuestionImages(squestions, imagesList, countImages);
                        for (int i=0;i<squestions;i++){
                            if (imagesList[i]!=null){
//                                uploadImages(imagesList[i],i,countImages);
                            }else {
                                imageUrl[i]=null;
                            }
                        }
                        notifyAllUsers(mClass, quizID, sname);

                   //     upload(mClass);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FillQuizDataActivity.this, "Failed to add data,please try again", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                        return;
                    }
                });
            }
        });
    }

    private void storeCurrentQuestion(int mQuestionNumber) {
        Question questionModel = new Question();

        mQuestion = question.getText().toString();
        mOption1 = option1.getText().toString();
        mOption2 = option2.getText().toString();
        mOption3 = option3.getText().toString();
        mOption4 = option4.getText().toString();
        if (!correctoption.getText().toString().equals("")) {
            mCorrectOption = Integer.parseInt(correctoption.getText().toString());
            questionModel.setCorrectOption(mCorrectOption);
        } else {
            questionModel.setCorrectOption(0);
        }

        if (!questiontime.getText().toString().equals("")) {
            mQuestionTime = Integer.parseInt(questiontime.getText().toString());
            questionModel.setTime(mQuestionTime);
        } else {
            questionModel.setTime(0);
        }

        imagesList[mQuestionNumber]=temp;
        temp = null;

        questionModel.setQuestion(mQuestion);
        questionModel.setOption1(mOption1);
        questionModel.setOption2(mOption2);
        questionModel.setOption3(mOption3);
        questionModel.setOption4(mOption4);
        mQuestionsToBeStore.put(mQuestionNumber, questionModel);
    }

    //A Method for uploading the questions
    public void upload(String standard) {
        for (int i = 0; i < squestions; i++) {
            final int c = i;
            mQuestionsToBeStore.get(i).setImage(imageUrl[i]);
            mQuestionsToBeStore.get(i).setQuizID(quizID);

            db.collection("questions").add(mQuestionsToBeStore.get(i)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    //All the questions are uploaded
                    if (c == squestions - 1) {
                        Toast.makeText(FillQuizDataActivity.this, "Quiz Data is Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        //jumping to MainActivity
                        Intent intentn = new Intent(FillQuizDataActivity.this, MainActivity.class);
                        intentn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentn);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FillQuizDataActivity.this, "Failed to add data,please try again", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
            });
        }
    }

    private void notifyAllUsers(String standard, final String quizId, String quizName) {
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
                            FCMUtils.sendPushMessage(FillQuizDataActivity.this, tokenList, "quiz", quizId, quizName);
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
            temp = data.getData();
            imageView.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),temp);
                imageView.setImageBitmap(bitmap);
            }catch (Exception e){
                Toast.makeText(this, " "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }

    }
    public void showImage(Uri uri){
        if (uri != null){
            temp=uri;
            imageView.setVisibility(View.VISIBLE);
            removeImage.setVisibility(View.VISIBLE);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                imageView.setImageBitmap(bitmap);
            }catch (Exception e){
                Toast.makeText(this, " "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            temp=null;
            imageView.setVisibility(View.GONE);
            removeImage.setVisibility(View.GONE);
        }

    }

    private void uploadQuestionImages(int squestions, Uri[] imagesList, int countImages) {
        for (int i=0;i<squestions;i++){
            int finalI = i;
            if (imagesList[i]!=null){

                String nameOfImg=""+System.currentTimeMillis()+""+UUID.randomUUID().toString();
                final StorageReference reference = storageReference.child("imagesQuestion/" +nameOfImg);

                reference.putFile(imagesList[i]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete()) ;
                        Uri url = uri.getResult();
                        //   imageUrl[num]=url.toString();
                        imageUrl[finalI]=nameOfImg;
                        countTemp++;
                        if (finalI == imagesList.length - 1) {
                            upload(mClass);
                        }



                        Log.d("FireBase", "Done: "+finalI);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        // WE CAN SHOW THE PROGRESS HERE WHILE DOWNLOADING IF WE WANT
                    }
                });


            }else {
                imageUrl[i]=null;
                if (finalI == imagesList.length - 1) {
                    upload(mClass);
                }
            }
        }
    }

    //This method will be called when user clicks the addbtn for uploading all the data
    public void uploadImages(final Uri data, final int num,int countImages) {


        String nameOfImg=""+System.currentTimeMillis()+""+UUID.randomUUID().toString();
//        final StorageReference reference = storageReference.child("imagesQuestion/" + System.currentTimeMillis() + UUID.randomUUID().toString());
        final StorageReference reference = storageReference.child("imagesQuestion/" +nameOfImg);

        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                Uri url = uri.getResult();
             //   imageUrl[num]=url.toString();
                imageUrl[num]=nameOfImg;
                countTemp++;
                if (countTemp==countImages){
                    upload(mClass);
                }

                
                Log.d("FireBase", "Done: "+num);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // WE CAN SHOW THE PROGRESS HERE WHILE DOWNLOADING IF WE WANT
            }
        });

    }

}
