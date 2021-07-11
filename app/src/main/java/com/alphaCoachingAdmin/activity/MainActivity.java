package com.alphaCoachingAdmin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alphaCoachingAdmin.Constant.Constant;
import com.alphaCoachingAdmin.Constant.FCMUtils;
import com.alphaCoachingAdmin.R;
import com.alphaCoachingAdmin.UserRequests.RequestsUsers;
import com.alphaCoachingAdmin.Utils.UserSharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static com.alphaCoachingAdmin.AlphaApplication.getAppContext;
import static com.alphaCoachingAdmin.Utils.UserSharedPreferenceManager.isMasterRole;

public class MainActivity extends AppCompatActivity {
    private Button recentLecturebtn, quiz, studymaterial, createUserBtn, requestsBtn;
    private Button mAddFaculty;
    private Context mContext;
    private Button mLogoutBtn;
    private FirebaseAuth fireAuth;
    private Button mAddGems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getAppContext();

        recentLecturebtn = (Button) findViewById(R.id.addRecentLecture);
        studymaterial = (Button) findViewById(R.id.addstudymaterial);
        quiz = (Button) findViewById(R.id.quiz);
        createUserBtn = (Button) findViewById(R.id.createBtn);
        requestsBtn = (Button) findViewById(R.id.requestbtn);
        mAddFaculty = (Button) findViewById(R.id.addUserFaculty);
        mLogoutBtn = (Button) findViewById(R.id.logout);
        mAddGems = (Button) findViewById(R.id.addGemsButton);
        fireAuth = FirebaseAuth.getInstance();
        setAdminUtilities();
        setClickListeners();
    }

    private void setClickListeners() {
        createUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                notifyAllUsers();
                Intent intent = new Intent(mContext, CreateUser.class);
                intent.putExtra("userRole", Constant.UserRoles.STUDENT);
                if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mContext.startActivity(intent);
            }
        });

        requestsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                notifyAllUsers();
                Intent intent = new Intent(mContext, RequestsUsers.class);
                if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mContext.startActivity(intent);
            }
        });

        recentLecturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StoreRecentLectureActivity.class);
                if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mContext.startActivity(intent);
            }
        });

        studymaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(mContext, SelectClass.class);
                if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
                    intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mContext.startActivity(intent3);
            }
        });

        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(mContext, FillQuestionsActivity.class);
                if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mContext.startActivity(intent2);
            }
        });

        mAddFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                create activity here to add new faculty.
                Intent intent = new Intent(mContext, CreateUser.class);
                intent.putExtra("userRole", Constant.UserRoles.FACULTY);
                if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mContext.startActivity(intent);
            }
        });

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoginActivity().logoutUser(UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_UUID));
                fireAuth.signOut();
                UserSharedPreferenceManager.removeUserData(getApplicationContext());
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        mAddGems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GemsActivity.class);
                if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mContext.startActivity(intent);
            }
        });
    }

    private void setAdminUtilities() {
        if (isMasterRole(mContext)) {
            mAddFaculty.setVisibility(View.VISIBLE);
            requestsBtn.setVisibility(View.VISIBLE);
            createUserBtn.setVisibility(View.VISIBLE);
            mAddGems.setVisibility(View.VISIBLE);
        } else {
            mAddFaculty.setVisibility(View.GONE);
            requestsBtn.setVisibility(View.GONE);
            createUserBtn.setVisibility(View.INVISIBLE);
            mAddGems.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

//    dummy code for checking notifications.
    private void notifyAllUsers() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("e2etzJwoTLy1LArHDqJjYf:APA91bGvQYZgiCkbGroyEbkor2l-3AE_QxQyJV1pJWoLmvkLbWEU-NCyue0RgGuh5tW6SoRj_Fxhr8wK-Ut-68lvWdhCZP5o-CqKAZ46DKt8QRIBDR_QwwPhH9MrcPaJaJCV9SwHARjC");
        strings.add("dbQpgZuvQuKgDiLLUDK8Rh:APA91bHmULrXetP2o6SjHbOzNZqjq7uWC_WjeRTC8xU3UbGSkciGqQOwtVeiiTjYipAwRu65mr3W152k-Uf9v5pp6AYqcccjg8mJx_uvxANqaOYG1ok04GWeLI597elmtNdExix8xcWV");
        FCMUtils.sendPushMessage(MainActivity.this, strings, "", "", "");
    }
}
