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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    EditText login_id, login_password;
    TextView gotoReg;
    AppCompatButton btn_login;
    Context context;
    ProgressDialog progressDialog;
    PaymentMethods paymentMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;



        paymentMethods = new PaymentMethods(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging In...");

        login_id = findViewById(R.id.login_id);
        login_password = findViewById(R.id.login_password);
        gotoReg = findViewById(R.id.gotoReg);

        gotoReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Registration.class));
            }
        });

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!login_id.getText().toString().equalsIgnoreCase("") &&
                        !login_password.getText().toString().equalsIgnoreCase("")
                ) {


                    login(login_id.getText().toString(), login_password.getText().toString());

                } else {
                    Toast.makeText(context, "Please fill all Information", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void login(final String userId, final String password) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.ApiLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String type, is_signup_payment_done, token;
                        progressDialog.dismiss();
                        try {
                            JSONObject data = new JSONObject(response);
                            type = data.getString("type");
                            //System.out.println(response);
                            JSONObject dataResponse = new JSONObject(data.getString("response"));


                            if (type.equals("success")) {
                                is_signup_payment_done = dataResponse.getString("is_signup_payment_done");

                                JSONObject login_detail = new JSONObject(dataResponse.getString("login_detail"));
                                token = login_detail.getString("token");

                                SharedPrefs.setSP(context,Constant.session_login_Token,token);
                                SharedPrefs.setSP(context,Constant.session_login_UserId,userId);


                                if (!is_signup_payment_done.equals("true")) {
                                    verifyUserDetailByToken(userId, token);
                                    //  paymentMethods.requestPayment(userId,Constant.phone,email,Constant.regAmount, Constant.paymentPurpose,buyerName);
                                }else{

                                    getUserDetailByToken(userId,token);

                                }

                            } else {
                                String message = dataResponse.getString("message");
                                System.out.println(message);
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                            System.out.println(response);
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
                        progressDialog.dismiss();
                        //Log.e("HttpClient", "error: " + error.toString());
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("unique_id", userId);
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

    public void verifyUserDetailByToken(final String userId, final String token) {
        progressDialog.setMessage("Verifying Details");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.ApiUserDetailByToken,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String type, is_signup_payment_done, user_id, email, full_name;
                        progressDialog.dismiss();
                        try {
                            JSONObject data = new JSONObject(response);
                            type = data.getString("type");
                            //System.out.println(response);
                            JSONObject dataResponse = new JSONObject(data.getString("response"));


                            if (type.equals("success")) {
                                is_signup_payment_done = dataResponse.getString("is_signup_payment_done");

                                JSONObject user_details = new JSONObject(dataResponse.getString("user_details"));
                                user_id = user_details.getString("user_id");
                                email = user_details.getString("email");
                                full_name = user_details.getString("full_name");


                                if (!is_signup_payment_done.equals("true")) {
                                    paymentMethods.requestPayment(user_id, Constant.phone, email, Constant.regAmount, Constant.paymentPurpose, full_name);
                                }

                            } else {
                                String message = dataResponse.getString("message");
                                System.out.println(message);
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                            System.out.println(response);
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
                        progressDialog.dismiss();
                        //Log.e("HttpClient", "error: " + error.toString());
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("unique_id", userId);
                params.put("token", token);
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

    public void getUserDetailByToken(final String user_Id, final String token) {
        progressDialog.setMessage("Verifying Details");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.ApiUserDetailByToken,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String type, user_id, email, full_name;
                        progressDialog.dismiss();
                        try {
                            JSONObject data = new JSONObject(response);
                            type = data.getString("type");
                            //System.out.println(response);
                            JSONObject dataResponse = new JSONObject(data.getString("response"));


                            if (type.equals("success")) {

                                JSONObject user_details = new JSONObject(dataResponse.getString("user_details"));
                                user_id = user_details.getString("user_id");
                                email = user_details.getString("email");
                                full_name = user_details.getString("full_name");

                                SharedPrefs.setSP(context,Constant.login_Token,token);
                                SharedPrefs.setSP(context,Constant.loginUserFullName,full_name);
                                SharedPrefs.setSP(context,Constant.loginUserEmail,email);
                                SharedPrefs.setSP(context,Constant.login_UserId,user_id);

                                ((Activity) context).finish();
                                context.startActivity(new Intent(context,MainActivity.class));


                            } else {
                                String message = dataResponse.getString("message");
                                System.out.println(message);
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                            System.out.println(response);
                          //  Toast.makeText(context, response, Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //Log.e("HttpClient", "error: " + error.toString());
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("unique_id", user_Id);
                params.put("token", token);
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


