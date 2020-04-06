package com.example.instamojo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class PaymentResponse extends AppCompatActivity {
    ImageView success_icon, failed_icon, error_ic;
    TextView paymentStatus, trans_id;
    String paymentId, status, sessionLoginUserId, sessionLoginToken;
    Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_response);
        context = this;

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating ...");

        Intent getExtra = getIntent();
        status = getExtra.getStringExtra("status");
        paymentId = getExtra.getStringExtra("paymentId");

        success_icon = findViewById(R.id.success_icon);
        failed_icon = findViewById(R.id.fail_icon);
        error_ic = findViewById(R.id.error_icon);
        trans_id = findViewById(R.id.trans_id);
        paymentStatus = findViewById(R.id.status);




        success_icon.setVisibility(View.GONE);
        failed_icon.setVisibility(View.GONE);
        error_ic.setVisibility(View.GONE);


        sessionLoginToken = SharedPrefs.getSP(context, Constant.session_login_Token);
        sessionLoginUserId = SharedPrefs.getSP(context, Constant.session_login_UserId);

        switch (status) {
            case "Credit":
                success_icon.setVisibility(View.VISIBLE);
                getUserDetailByToken(sessionLoginUserId,sessionLoginToken);
                break;
            case "Failed":
                failed_icon.setVisibility(View.VISIBLE);
                break;
            default:
                error_ic.setVisibility(View.VISIBLE);
                break;
        }


        trans_id.setText(paymentId);
        paymentStatus.setText(status);





    }

    public void getUserDetailByToken(final String user_Id, final String token) {
        progressDialog.setMessage("Updating Details...");
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
    public void onBackPressed() {
        ((Activity) context).finishAffinity();
        context.startActivity(new Intent(context,MainActivity.class));
    }


}




