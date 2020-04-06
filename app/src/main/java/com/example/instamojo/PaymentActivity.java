package com.example.instamojo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {
    Activity activity;
    Context context;

    WebView webview_layout;
    boolean loadingFinished;
    boolean redirect;
    String domain;
    ProgressDialog progressdialog;
    private PaymentMethods paymentMethods;
    AlertDialog alert;
    AlertDialog.Builder alertDialog;
    String userId, longurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        context = this;
        paymentMethods = new PaymentMethods(context);
        webview_layout = findViewById(R.id.webview_layout);

        loadingFinished = true;
        redirect = false;

        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Please Wait....");
        progressdialog.setCancelable(false);
        progressdialog.show();

        Intent getExtra = getIntent();
        userId = getExtra.getStringExtra("userId");
        longurl = getExtra.getStringExtra("longurl");

        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage("Do you want cancel your Transaction?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {dialog.cancel();
                    }
                });
        //Creating dialog box
        alert = alertDialog.create();
        openTransactionWebView(longurl);


    }

    @SuppressLint("SetJavaScriptEnabled")
    private void openTransactionWebView(String url) {
        webview_layout.getSettings().setJavaScriptEnabled(true); // enable javascript
        webview_layout.loadUrl(url);
        webview_layout.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                loadingFinished = false;
                //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                progressdialog.show();
                System.out.println(url);
                getPaymentResponse(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    //HIDE LOADING IT HAS FINISHED
                    progressdialog.dismiss();
                } else {
                    redirect = false;
                }
            }
        });
    }


    /*
    @SuppressLint("SetJavaScriptEnabled")
    private void openTransactionResponseWebView(String url) {
        webview_layout_response.getSettings().setJavaScriptEnabled(true); // enable javascript
        webview_layout_response.loadUrl(url);
        webview_layout_response.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                loadingFinished = false;
                //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                progressdialog.show();

                System.out.println(url);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    //HIDE LOADING IT HAS FINISHED
                    progressdialog.dismiss();
                } else {
                    redirect = false;
                }

            }
        });
    }*/


    public void getPaymentResponse(String detectedUrl) {
        String responseUrl;
        if (detectedUrl.indexOf("response") > 0) {
            webview_layout.stopLoading();
            webview_layout.setVisibility(View.GONE);
            if (detectedUrl.contains("http://localhost")) {
                responseUrl = detectedUrl.replace("http://localhost", Api.domain);
            } else {
                responseUrl = detectedUrl;
            }

            responseUrl +="&user_id="+userId;


            paymentMethods.updatePayment(responseUrl);
            System.out.println("Response Url ---- " + responseUrl);

            //  Toast.makeText(context,"Payment Success",Toast.LENGTH_LONG).show();


        } else {
            System.out.println("Not the url");
        }
    }


    /*public void getPayementResponseDataofRedirectUrl(String redirectUrl) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, redirectUrl,
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject data = new JSONObject(response);
                            //String success = data.getString("success");

                            String payment_status,paymentId;
                            JSONObject payment_request = new JSONObject(data.getString("payment_response"));

                            payment_status = payment_request.getString("status");
                            paymentId = payment_request.getString("payment_request_id");

                           if (payment_status.equalsIgnoreCase("Credit")) {
                            // Redirect to Success Intent


                            } else if (payment_status.equalsIgnoreCase("Failed")) {
                            // Redirect to Failed Intent
                            }

                            Intent responseIntent = new Intent(context, PaymentResponse.class);
                            responseIntent.putExtra("status", payment_status);
                            responseIntent.putExtra("paymentId", paymentId);

                            context.startActivity(responseIntent);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        System.out.println(response);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);


    }*/


    @Override
    public void onBackPressed() {
        alert.setTitle("Cancel Transaction");
        alert.show();
    }

}
