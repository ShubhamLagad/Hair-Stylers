package com.subhdroid.hairstylers.CloudMessage;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.subhdroid.hairstylers.Customer.CustomerDashboard;
import com.subhdroid.hairstylers.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FCMNotificationSender {
    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Class mActivity;


    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey ="AAAAU1vX6KM:APA91bGdgM2Z_1dPpadIyaiRvhIT-BMoNV3D7OPIpG-akx834a8tWR9U8BgHN0OM6RfTxi27_pjr0VRAOPUXOs-O97_zgnlqHeaVh0p9P8cMQyfEFi9-Gwv92-oq9iJNdOgFz0sLz4h4";
//    private final String fcmServerKey ="e8IxPqrjS1eTx454P3KmXe:APA91bFK1-ARCTp-hJ3LZZN4eVA-jwCU63Bm5T0Znr7sdDk42sxaGj0Bo5xH8Ctz2mYOgVavzeJa3_IHoVAlyYcJCmWmufy-kQBgdfocLy3d7CbqIwrcBxTRFDwJxrH-uTUfMDIeD-bX";

    public FCMNotificationSender(String userFcmToken, String title, String body, Context mContext
            , Class<CustomerDashboard> mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;


    }

    public void sendNotifications() {

        Log.d("Log","Notification send call");

        requestQueue = Volley.newRequestQueue(mContext);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            mainObj.put("notification", notiObject);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {


                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;


                }
            };
            requestQueue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

}
