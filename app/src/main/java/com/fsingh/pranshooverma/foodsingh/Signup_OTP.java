package com.fsingh.pranshooverma.foodsingh;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

public class Signup_OTP extends AppCompatActivity {

    Button signup,loginme;
    EditText mobnumber,password,name,email;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup__otp);


        initialize();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mob=mobnumber.getText().toString();
                String pass=password.getText().toString();
                String na=name.getText().toString();
                String ema=email.getText().toString();


                if(mob.length()==10 & pass.length()>=4 & na.length()>1 & ema.length()>4 )
                {
                    if(checking_net_permission())
                    {
                        mob="91"+mob;
                        send_otp(mob,pass,na,ema);

                    }
                    else
                    {
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
                else {
                    Display("Give your Mobile No. WITHOUT 91 OR 0 & password should contain minimum 4 characters");
                }
            }
        });
        loginme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ag=new Intent(getApplicationContext(),login_page.class);
                startActivity(ag);
                finish();

            }
        });

    }

    private void send_otp(final String mob, final String pass, final String nam, final String ema) {
        progress.setMessage("Sending OTP message....");
        progress.setCancelable(false);
        progress.show();


        String Url="https://control.msg91.com/api/sendotp.php?authkey=112452AB1seNQy572e2e51&mobile="+mob+"&message=Your%20OTP%20is%20%23%23OTP%23%23%20.%20It%20is%20Valid%20for%203%20minutes%20only.&sender=FoodSingh&otp_expiry=3";
        StringRequest request=new StringRequest(Request.Method.GET, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
              if(progress.isShowing())
              {   progress.dismiss();
           //       Display(response);
                  Intent as=new Intent(getApplicationContext(),verifying_otp.class);
                  Bundle a=new Bundle();
                  a.putString("mob",mob);
                  a.putString("pass",pass);
                  a.putString("name",nam);
                  a.putString("email",ema);
                  as.putExtras(a);
                  startActivity(as);
                  finish();
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

        RequestQueue a= Volley.newRequestQueue(getApplicationContext());
        a.add(request);
    }

    private void Display(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private Boolean checking_net_permission() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void initialize() {

        signup=(Button)findViewById(R.id.btnSignup);
        loginme=(Button)findViewById(R.id.btnLinkToLoginScreen);
        mobnumber=(EditText)findViewById(R.id.mob);
        password=(EditText)findViewById(R.id.password);

        name=(EditText)findViewById(R.id.name);
        email=(EditText)findViewById(R.id.email);

        progress=new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //Glide.with(this).load(R.drawable.background_image2).into(main_image);
        setTypeface();
    }

    private void setTypeface(){
        Typeface t = Typeface.createFromAsset(getAssets(), "fonts/android.ttf");
        signup.setTypeface(t);
        loginme.setTypeface(t);
        mobnumber.setTypeface(t);
        password.setTypeface(t);
        password.setTypeface(t);
        TextView welcome = (TextView)findViewById(R.id.welcome);
        welcome.setTypeface(t);
    }
}
