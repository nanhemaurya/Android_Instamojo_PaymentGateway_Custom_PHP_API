package com.example.instamojo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.instamojo.utils.Constant;
import com.example.instamojo.utils.SharedPrefs;
import com.example.instamojo.utils.UserMethods;

public class MainActivity extends AppCompatActivity {


    Context context;
    AppCompatButton logout_btn;
    TextView user_detailsView;
    UserMethods userMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        userMethods = new UserMethods(context);
        logout_btn = findViewById(R.id.logout_btn);
        user_detailsView = findViewById(R.id.user_details);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMethods.logout();
            }
        });

        String name, email, user_Id, user_detail = "";
        if (SharedPrefs.getSP(context, Constant.login_Token).equalsIgnoreCase("null")) {
            ((Activity) context).finish();
            context.startActivity(new Intent(context, Login.class));
        }


        name = SharedPrefs.getSP(context, Constant.loginUserFullName);
        email = SharedPrefs.getSP(context, Constant.loginUserEmail);
        user_Id = SharedPrefs.getSP(context, Constant.login_UserId);


        user_detail += name + "\n";
        user_detail += email + "\n";
        user_detail += user_Id + "\n";


        user_detailsView.setText(user_detail);




    }


    @Override
    public void onResume() {
        super.onResume();
        if(SharedPrefs.getSP(context, Constant.login_Token).equalsIgnoreCase("null")){
            ((Activity) context).finish();
            context.startActivity(new Intent(context, Login.class));
        }

    }

}
