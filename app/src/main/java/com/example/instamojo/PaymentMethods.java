package com.example.instamojo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentMethods {
    private AlertDialog.Builder alertDialog;
    private AlertDialog alert;

    ProgressDialog progressdialog;
    private AppCompatActivity appCompatActivity;
    private Context context;

    public PaymentMethods(Context context) {
        this.context = context;
        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Please Wait....");
        progressdialog.setCancelable(false);
    }

    public void requestPayment(final String userId, final String phone, final String email, final String amount, final String purpose, final String buyerName) {
        progressdialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.paymentRequest,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            String type = data.getString("type");
                            if (type.equals("success")) {
                                JSONObject dataResponse = new JSONObject(data.getString("response"));
                                JSONObject paymentResponse = new JSONObject(dataResponse.getString("response"));
                                String payment_initiated = paymentResponse.getString("payment_initiated");

                                if (payment_initiated.equalsIgnoreCase("true")) {
                                    JSONObject payment_gateway_response = new JSONObject(paymentResponse.getString("payment_gateway_response"));
                                    String longurl;
                                    JSONObject payment_request = new JSONObject(payment_gateway_response.getString("payment_request"));
                                    longurl = payment_request.getString("longurl");
                                    openPaymentActivity(userId,longurl);
                                    progressdialog.dismiss();
                                }else {
                                    progressdialog.dismiss();
                                    Toast.makeText(context,dataResponse.getString("message"),Toast.LENGTH_LONG).show();
                                }
                            } else {
                                progressdialog.dismiss();
                                Toast.makeText(context,"Error while making transaction",Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //System.out.println(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                        Log.e("HttpClient", "error: " + error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("buyer_name", buyerName);
                params.put("email", email);
                params.put("user_id", userId);
                params.put("amount", amount);
                params.put("purpose", purpose);
                params.put("phone", phone);
                params.put("redirect_url", Api.paymentResponse);
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


    public void updatePayment(String responseUrl){
        progressdialog.setMessage("Please Wait, we are confirming your transaction...");
        progressdialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, responseUrl,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            String type = data.getString("type");
                            JSONObject dataResponse = new JSONObject(data.getString("response"));
                            if (type.equals("success")) {
                                JSONObject paymentResponse = new JSONObject(dataResponse.getString("payment_response"));
                                String paymentId = paymentResponse.getString("payment_id");
                                String status = paymentResponse.getString("status");
                                String paymentRequestId = paymentResponse.getString("payment_request_id");
                                progressdialog.dismiss();
                                ((Activity) context).finish();
                                openPaymentResponseActivity(status,paymentRequestId);
                            } else {
                                progressdialog.dismiss();
                                String message = dataResponse.getString("message");
                                Toast.makeText(context,message,Toast.LENGTH_LONG).show();
                                System.out.println(message);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //System.out.println(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                        Log.e("HttpClient", "error: " + error.toString());
                    }
                });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    private void openPaymentActivity(String userId, String longurl) {
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("longurl", longurl);
        context.startActivity(intent);
    }


    public void openPaymentResponseActivity(String status,String paymentId) {
        Intent intent = new Intent(context, PaymentResponse.class);
        intent.putExtra("status", status);
        intent.putExtra("paymentId", paymentId);
        context.startActivity(intent);
    }




}
