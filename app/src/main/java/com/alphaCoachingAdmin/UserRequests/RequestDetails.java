package com.alphaCoachingAdmin.UserRequests;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.alphaCoachingAdmin.R;

public class RequestDetails extends AppCompatActivity {

    TextView name, marks, reference, address, standard, mobile;
    String mname, mmarks, mreference, maddress, mstandard, mmobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        name = findViewById(R.id.reqdetName);
        standard = findViewById(R.id.reqdetClass);
        address = findViewById(R.id.reqdetAddress);
        marks = findViewById(R.id.reqdetMarks);
        mobile = findViewById(R.id.reqdetMobile);
        reference = findViewById(R.id.reqdetReference);

        Intent intent = getIntent();
        mname = "<b>" + "Name :" + "</b>" + intent.getStringExtra("name");
        maddress = "<b>" + "Address :" + "</b>" + intent.getStringExtra("address");
        mmarks = "<b>" + "Previous Year Percentage :" + "</b>" + intent.getStringExtra("marks");
        mmobile = "<b>" + "Phone No. :" + "</b>" + intent.getStringExtra("mobile");
        mreference = "<b>" + "Reference :" + "</b>" + intent.getStringExtra("reference");
        mstandard = "<b>" + "Class :" + "</b>" + intent.getStringExtra("class");

        name.setText(Html.fromHtml(mname));
        address.setText(Html.fromHtml(maddress));
        marks.setText(Html.fromHtml(mmarks));
        mobile.setText(Html.fromHtml(mmobile));
        reference.setText(Html.fromHtml(mreference));
        standard.setText(Html.fromHtml(mstandard));

    }
}
