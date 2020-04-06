package com.example.instamojo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.instamojo.utils.Constant;
import com.example.instamojo.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    Context context;
    EditText full_name, email, password;
    TextView gotoLogin;
    AppCompatButton regBtn;
    ProgressDialog progressDialog;
    PaymentMethods paymentMethods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        context = this;
        paymentMethods = new PaymentMethods(context);
        full_name = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        gotoLogin = findViewById(R.id.gotoLogin);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Registering...");

        regBtn = findViewById(R.id.btn_reg);
        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,Login.class));
            }
        });
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegClick();
            }
        });

    }


    public void onRegClick() {
        if (!full_name.getText().toString().equalsIgnoreCase("") &&
                !email.getText().toString().equalsIgnoreCase("") &&
                !password.getText().toString().equalsIgnoreCase("")
        ) {

            reg(full_name.getText().toString(), email.getText().toString(), password.getText().toString());

        } else {
            Toast.makeText(context, "Please fill all Information", Toast.LENGTH_LONG).show();
        }
    }
    public void reg(final String fullname, final String email, final String password) {

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.ApiReg,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("HttpClient", "success! response: " + response.toString());

                        /*login_error_msg.setVisibility(View.GONE);
                        String token, error;
                        removeLoginLoader();*/
                        String type,userId,email,buyerName;
                        progressDialog.dismiss();

                        try {
                            JSONObject data = new JSONObject(response);
                            type = data.getString("type");
                            //System.out.println(response);
                            JSONObject dataResponse = new JSONObject(data.getString("response"));

                            if (type.equals("success")) {
                                JSONObject user_detail = new JSONObject(dataResponse.getString("user_detail"));
                                userId = user_detail.getString("user_id");
                                email = user_detail.getString("email");
                                buyerName = user_detail.getString("fullname");
                                paymentMethods.requestPayment(userId,Constant.phone,email,Constant.regAmount, Constant.paymentPurpose,buyerName);

                            } else {
                                String message = dataResponse.getString("message");
                                System.out.println(message);
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                            //Toast.makeText(context, response, Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /*removeLoginLoader();*/
                        progressDialog.dismiss();
                        Log.e("HttpClient", "error: " + error.toString());
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("fullname", fullname);
                params.put("email", email);
                params.put("password", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type","application/json; charset=utf-8");
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!SharedPrefs.getSP(context, Constant.login_Token).equalsIgnoreCase("null")){
            ((Activity) context).finish();
            context.startActivity(new Intent(context, MainActivity.class));
        }
    }



}
