package com.fsingh.pranshooverma.foodsingh;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.DefaultRetryPolicy;
import com.fsingh.pranshooverma.foodsingh.BuildConfig;

import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Splash extends AppCompatActivity {


    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor;
    ProgressDialog progressBar;
    Context ctx;
    boolean rec = false;

    boolean checker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checker=false;
        rec = false;
        ctx = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        ImageView AnimationTarget = (ImageView)findViewById(R.id.progressBar);
       // Glide.with(this).load(R.drawable.signin).into(AnimationTarget);
       //Display("Loading! Please Wait");

progressBar = ProgressDialog.show(this, "Loading","Please Wait");
        progressBar.setCancelable(false);
        sharedPreferences = getSharedPreferences(constants.foodsingh, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String name = sharedPreferences.getString("name","User");
        String number = sharedPreferences.getString("mobile","000");


        if(checking_net_permission())
        {
            uploadDetails(name, number);
        }
        else
        {
            if(progressBar.isShowing())
            {
                progressBar.dismiss();
            }
            setContentView(R.layout.no_internet);
            Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/android.ttf");
            TextView calm = (TextView)findViewById(R.id.calm);
            final TextView retry = (TextView)findViewById(R.id.menu);
            calm.setTypeface(tf);
            retry.setTypeface(tf);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recreate();
                }
            });
        }

    }

    private void uploadDetails(String name, final String number) {

        StringRequest str = new StringRequest(Request.Method.POST, constants.set_version, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(progressBar.isShowing()){
                    progressBar.cancel();
                }
                try {

                    //Display(response);

                    JSONObject object= new JSONObject(response);
                    String result = object.getString("message");
                    String version = object.getString("version");
                    editor.putString("version",version);
                    editor.apply();
                   if(version.equals("NEW")){
                       if(result.equals("SUCCESS")){

                           startActivity(new Intent(Splash.this, menu.class));
                           finish();

                       }else{
                           Display("Data Transfer Failed! Please check Network connection and try again.");
                           finish();
                       }
                   }else{
                       AlertDialog.Builder dialog = new AlertDialog.Builder(Splash.this);
                       dialog.setTitle("Announcement");
                       dialog.setCancelable(false);
                       dialog.setMessage("You are using an older version of this app. To continue using this app, Please update");
                       dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               rec = true;
                               String url = "https://play.google.com/store/apps/details?id=com.fsingh.pranshooverma.foodsingh";
                               Intent i11 = new Intent(Intent.ACTION_VIEW);
                               i11.setData(Uri.parse(url));
                               startActivity(i11);
                           }
                       });

                       dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               finish();
                           }
                       });
                       dialog.show();
                   }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Display(e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Display(error.toString());
                if(progressBar.isShowing()){
                    progressBar.dismiss();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
               Map<String, String> map = new HashMap<>();
                map.put("mobile", number);
                map.put("version",getAppVersion());
                return map;

            }
        };

        RequestQueue request  = Volley.newRequestQueue(this);
        request.add(str);

        str.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private Boolean checking_net_permission() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void Display(String s){
        Toast.makeText(this, s,Toast.LENGTH_LONG).show();
    }

    private  String getAppVersion(){
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        return versionCode+"";
    }



    @Override
    public void onPause(){
        super.onPause();
        if(progressBar.isShowing()){
            progressBar.cancel();
            checker=true;
        }
    }

    @Override

    public void onResume(){
        super.onResume();
        if(checker){
            if(progressBar!=null){
                if(!progressBar.isShowing()){
                    progressBar = ProgressDialog.show(Splash.this, "Loading.", "Please Wait!");
                }
            }
        }

        if(rec){
            recreate();
        }

    }
}
