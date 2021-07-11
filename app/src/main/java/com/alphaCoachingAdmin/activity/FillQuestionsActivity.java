package com.alphaCoachingAdmin.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alphaCoachingAdmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FillQuestionsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    Button dateButton, timeButton, submitButton;
    EditText quizname, quiztime, quizquestions;
    String quizName;
    int quizTime, quizQuestionsCount;
    long quizTimeInMillis;
    Calendar mCalendar;// = Calendar.getInstance();

    FirebaseFirestore db=FirebaseFirestore.getInstance();  //Firestore Reference
    Spinner spinnerClass,spinnerSubject;                    //Spinners for class and subjects

    List<String> standard;                   //Stores name of Standards
    List<String> standardID;                  //Stores ID of the standard documents
    List<String> subject;                    //Stores name of Subjects
    List<String> subjectID;                  //Stores the id of standard which is present as a key in the subject document
    List<String> docsubjectID;               //stores the document id of the subject document
    List<String> specificsubject;           //Stores the name of the sorted subjects
    List<String> subjectIDtracker;            //Stores the document id of the sorted subjects
    String mClass,mSubject;   //Strings which contains selected Class/Standard and subjects iD
    ArrayAdapter<String> adapter,adapter2;
    private ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_details);
        dateButton = (Button) findViewById(R.id.datebtn);
        timeButton = (Button) findViewById(R.id.timebtn);
        submitButton = (Button) findViewById(R.id.submitbtn);
        quizname = (EditText) findViewById(R.id.quizname);
        quizquestions = (EditText) findViewById(R.id.quizquestions);
        quiztime = (EditText) findViewById(R.id.quiztime);

        spinnerClass=findViewById(R.id.spinnerQuizClass);
        spinnerSubject=findViewById(R.id.spinnerQuizSubject);
        standard=new ArrayList<String>();
        standardID=new ArrayList<String>();
        subject=new ArrayList<String>();
        subjectID=new ArrayList<String>();
        specificsubject=new ArrayList<String>();
        subjectIDtracker=new ArrayList<String>();
        docsubjectID=new ArrayList<String>();
        mCalendar = Calendar.getInstance();
        mProgressBar = new ProgressDialog(this);


        showProgressDialogWithTitle("Fetcing Your Data");
        db.collection("standard").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if ( task . isSuccessful ()) {
                    for ( QueryDocumentSnapshot document : task . getResult ()) {
                        standard.add(document.get("standard").toString());
                        standardID.add(document.getId());
                    }
                }
                db.collection("subjects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if ( task.isSuccessful ()) {
                            for ( QueryDocumentSnapshot document : task . getResult ()) {
                                subject.add(document.get("name").toString());
                                subjectID.add(document.get("standard").toString());
                                docsubjectID.add(document.getId());
                            }
//                            Toast.makeText(FillQuestionsActivity.this,"All data is fetched!",Toast.LENGTH_LONG).show();
                            hideProgressDialogWithTitle();

                        }else {
                            Toast.makeText(FillQuestionsActivity.this,"Something went wrong,please try again",Toast.LENGTH_LONG).show();
                            hideProgressDialogWithTitle();
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    quizName = quizname.getText().toString();
                    quizTime = Integer.parseInt(quiztime.getText().toString());
                    quizQuestionsCount = Integer.parseInt(quizquestions.getText().toString());
                    quizTimeInMillis = mCalendar.getTimeInMillis();
                } catch (Exception e) {
                    Toast.makeText(FillQuestionsActivity.this, "Please enter all fields ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (quizName.equals("") ||mClass.equals("")||mSubject.equals("")|| quizTime == 0 || quizQuestionsCount == 0 || quizTimeInMillis == 0) {
                    Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Sending data to the next activity
                Intent intent = new Intent(FillQuestionsActivity.this, FillQuizDataActivity.class);
                intent.putExtra("quizname", quizName);
                intent.putExtra("quizquestions", quizQuestionsCount);
                intent.putExtra("quiztime", quizTime);
                intent.putExtra("quizdate", quizTimeInMillis);
                intent.putExtra("mClass",mClass);
                intent.putExtra("mSubject",mSubject);
                startActivity(intent);
            }
        });


        //TIME PICKER
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");

            }
        });

        // DATE PICKER
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSubject="";
        mClass="";
        specificsubject.clear();
        subjectIDtracker.clear();

        adapter=new ArrayAdapter<String>(FillQuestionsActivity.this, android.R.layout.simple_spinner_dropdown_item,standard);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);

        adapter2=new ArrayAdapter<String>(FillQuestionsActivity.this, android.R.layout.simple_spinner_dropdown_item,specificsubject);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapter2);

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mClass=standardID.get(position);
                specificsubject.clear();
                subjectIDtracker.clear();
                /*
                Will sort the subjects which matches the Standard ID
                 */
                String string=standardID.get(position);
                for (int i=0;i<subject.size();i++){
                    if (subjectID.get(i).equals(string)){
                        subjectIDtracker.add(docsubjectID.get(i));
                        specificsubject.add(subject.get(i));
                    }
                }
                Log.d("CheckerGame", "onStandardItemSelected: "+mClass);

                mSubject=subjectIDtracker.get(0);

                adapter2.notifyDataSetChanged();
                spinnerSubject.setSelection(0);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSubject=subjectIDtracker.get(position);
                Log.d("CheckerGame", "onSubjectItemSelected: "+mSubject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        Calendar calendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentdateString = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());
        String string = "" + dayOfMonth + "-" + month + "-" + year;
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date date;

        try {
            date = (Date) format.parse(string);
            //Converting the date into milliseconds for timestamp in next activity
            quizTimeInMillis = date.getTime();

        } catch (ParseException e) {
            Toast.makeText(FillQuestionsActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
        }

        //displaying date
        TextView textView = (TextView) findViewById(R.id.datetv);
        textView.setText(currentdateString);

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Displaying Time
        TextView textView = (TextView) findViewById(R.id.timetv);
        textView.setText("Hours: " + hourOfDay + " Minutes: " + minute);
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
    }

    /**
     *  this class is used to pick a date.
     */
    public static class DatePickerFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        }

    }

    /**
     * This class is used to pick a time.
     */
    public static class TimePickerFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
        }
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
