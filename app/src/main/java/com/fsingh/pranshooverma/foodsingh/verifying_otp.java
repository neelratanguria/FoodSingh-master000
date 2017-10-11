package com.fsingh.pranshooverma.foodsingh;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class verifying_otp extends AppCompatActivity {
    Button verify;
    EditText otp;
    String pass,mob,name,email;
    TextView t1, t2, t3;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verifying_otp);

        initialize();

        getting_pass_mob();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp_input=otp.getText().toString();
                if(checking_net_permission()){
                    verify_otp(mob,otp_input);
                }
                else {
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
        });
    }

    private void verify_otp(final String mobile, String otp_user) {
        progress.setMessage("Verifying Your otp ,Please Wait.....");
        progress.show();
        String url="https://control.msg91.com/api/verifyRequestOTP.php?authkey=112452AB1seNQy572e2e51&mobile="+mobile+"&otp="+otp_user;
        StringRequest a=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(progress.isShowing())
                {
                    progress.dismiss();
                    try {
                        JSONObject a=new JSONObject(response);
                        String status=a.getString("type");
                        if(status.equals("success"))
                        {
                            save_to_database(mob,pass);
                        }
                        else {
                            Display("You have entered an Invalid input");
                        }
                       Display(status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progress.isShowing())
                {
                    progress.dismiss();
                }
                Display("some error occured ,may be due to bad network ,try again");
            }
        });
        RequestQueue rq= Volley.newRequestQueue(this);
        rq.add(a);
    }

    private void save_to_database(final String mob, final String pass) {
        if(progress.isShowing())
        {
            progress.dismiss();
        }
        progress.setMessage("Loading,Please wait.....");
        progress.show();
        StringRequest as=new StringRequest(Request.Method.POST, constants.login_details_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(progress.isShowing())
                {
                    progress.dismiss();
                }
                try {
                    JSONObject obj=new JSONObject(response);
                    String status=obj.getString("upload");
                    if(status.equals("1"))
                    {
                        Display("Successfully Registered");
                        //saving mobile number and password
                        SharedPreferences as=getSharedPreferences(constants.foodsingh,MODE_PRIVATE);
                        SharedPreferences.Editor edit=as.edit();
                        edit.putString("password",pass);//100 defined for logged in
                        edit.putString("mobile",mob);
                        edit.apply();

                        Intent asd=new Intent(getApplicationContext(),Splash.class);
                        startActivity(asd);
                        finish();
                    }
                    else
                    {
                        Display("You are already Registered,Kindly Login");
                        Intent as=new Intent(getApplicationContext(),login_page.class);
                        startActivity(as);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progress.isShowing())
                {
                    progress.dismiss();
                }
                Display("Some error occured,may be due to bad network");
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> maps=new HashMap<>();
                maps.put("number",mob);
                maps.put("password",pass);
                maps.put("name",name);
                maps.put("email",email);
                return maps;
            }
        };
        RequestQueue asdf=Volley.newRequestQueue(this);
        asdf.add(as);
    }

    private void  Display(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void getting_pass_mob() {
        Bundle as=getIntent().getExtras();
        pass=as.getString("pass");
        mob=as.getString("mob");
        name=as.getString("name");
        email=as.getString("email");

    }

    private Boolean checking_net_permission() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void initialize() {
        verify=(Button)findViewById(R.id.verify);
        otp=(EditText) findViewById(R.id.otp);
        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        t1 = (TextView)findViewById(R.id.welcome);
        t2 = (TextView)findViewById(R.id.Confirmation);
        t3 = (TextView)findViewById(R.id.textCode);
        setTypeface();
    }

    private void setTypeface(){
        Typeface t = Typeface.createFromAsset(getAssets(), "fonts/android.ttf");
        verify.setTypeface(t);
        otp.setTypeface(t);
        t1.setTypeface(t);
        t2.setTypeface(t);
        t3.setTypeface(t);

    }
}
