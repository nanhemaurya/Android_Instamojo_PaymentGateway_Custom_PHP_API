package com.example.instamojo;

public class Api {
    public static String domain = "http://172.20.10.4/instamojo";
    //public static String domain = "http://192.168.56.1/instamojo";
    public static String ApiPath = domain + "/api/v1";
    public static String ApiReg = ApiPath + "/users/register";
    public static String ApiLogin = ApiPath + "/users/login";
    public static String ApiUserDetail = ApiPath + "/users/user";

    public static String paymentResponse = ApiPath + "/payment/payment_response";
    public static String paymentRequest = ApiPath + "/payment/payment_request";

    public static String ApiUserDetailByToken =ApiPath + "/users/user-by-token";;
}
