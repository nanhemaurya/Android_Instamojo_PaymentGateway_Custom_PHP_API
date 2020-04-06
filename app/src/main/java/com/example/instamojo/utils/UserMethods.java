package com.example.instamojo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.instamojo.MainActivity;

public class UserMethods {

    private Context context;

    public UserMethods(Context context) {
        this.context = context;
    }

    public void logout(){
        if (!SharedPrefs.getSP(context, Constant.login_Token).equalsIgnoreCase("null")) {
            SharedPrefs.removeSP(context,Constant.login_Token);
            ((Activity) context).finish();
            context.startActivity(new Intent(context, MainActivity.class));

        }
    }

}
