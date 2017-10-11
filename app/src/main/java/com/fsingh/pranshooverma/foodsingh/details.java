package com.fsingh.pranshooverma.foodsingh;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.print.PrintHelper;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Display;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class details extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

  private   Button button_save_change;
    private  EditText name,email,old_password,new_password;
    private ProgressDialog progress;
    String old_password_check,mobile_old;
    NavigationView navigationView;
    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

         navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    /////////////////////////////////////////////////////////////////////////////
        //CODING CODING CODING

        initialize();

        getting_setting_details();


        button_save_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           String old_pa=old_password.getText().toString();

                   if(old_pa.equals(old_password_check))
                   {
                       if(checking_net_permission())
                       {
                           String n=name.getText().toString();
                           String e=email.getText().toString();
                           String np=new_password.getText().toString();
                           if((np.length()<4 & np.length()>0) || e.length()<=4 || n.length()<=1)
                           {
                               Display("Kindly give proper details");
                           }
                            if(np.length()==0 & e.length()>=4 & n.length()>=1)
                           {
                               np=old_password_check;
                               Display("Your password will be same");
                            upudate_to_deb(n,e,np);
                           }
                           if(np.length()!=0 & np.length()>=4 & e.length()>=4 & n.length()>=1)
                           {
                               upudate_to_deb(n,e,np);
                           }

                       }
                       else
                       {
                           Display("You dont have Internet connection ,cant make changes");
                       }
                   }
                   else
                   {
                       Display("Your Current Password is Incorrect");
                   }

            }
        });
        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);

        }
        manipulatenavigationdrawer();
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/android.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);

    }


    private void manipulatenavigationdrawer() {
        View v = navigationView.getHeaderView(0);
        Typeface tp = Typeface.createFromAsset(getAssets(), "fonts/android.ttf");
        TextView t = (TextView) v.findViewById(R.id.welcome);
        t.setTypeface(tp);
        ImageView back = (ImageView)v.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawers();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences(constants.foodsingh,Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name","_");
        if(!name.equals("_")){
            t.setText("Hello, "+name);
        }
    }

    private void upudate_to_deb( final String n, final String e, final String np) {
        SharedPreferences sh=getSharedPreferences(constants.foodsingh, Context.MODE_PRIVATE);
          mobile_old=sh.getString("mobile","000");
        if(mobile_old.equals("000"))
        {
            Display("Something went wrong,kindly signout and then sign in");
            finish();
        }

        if(progress.isShowing())
        {
            progress.dismiss();
        }
        else
        {
            progress.setMessage("Updating the details....");
            progress.show();
        }
        StringRequest strq=new StringRequest(Request.Method.POST, constants.update_details, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(progress.isShowing())
                {
                    progress.dismiss();
                }
                try {
                    JSONObject obj=new JSONObject(response);
                    String m=obj.getString("message");
                    String r=obj.getString("result");

                    if(m.equals("SUCCESS") && r.equals("true"))
                    {
                        SharedPreferences sg=getSharedPreferences(constants.foodsingh,Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit=sg.edit();
                        edit.putString("password",np);
                        edit.putString("name",n);
                        edit.putString("email",e);
                        edit.apply();
                        Display("Details Has been Updated....");
                        finish();
                    }
                    else
                    {
                        Display("Details has not been updated,try again later");
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progress.isShowing())
                {
                    progress.dismiss();
                }
                Display("Some error Occured may be due to bad internet connection or Server issue");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> maps=new HashMap<>();
                maps.put("name",n);
                maps.put("email",e);
                maps.put("password",np);
                maps.put("mobile",mobile_old);
                return maps;
            }
        };

        RequestQueue re=Volley.newRequestQueue(this);
        re.add(strq);

    }

    private void getting_setting_details() {
        if(checking_net_permission())
        {
            SharedPreferences pref=getSharedPreferences(constants.foodsingh,MODE_PRIVATE);
            String mobile=pref.getString("mobile","000");
            if(mobile.equals("000"))
            {
                Display("Some Error is there,Kindly log out and then log in again");
            }
            else
            {
                get_data_deb(mobile);
            }

        }
        else {
            Display("You need Internet connection,to see and edit your details");
        }
    }

    private void get_data_deb(final String mobile) {
        if(progress.isShowing())
        {
            progress.dismiss();
        }
        else
        {
            progress.setMessage("Getting your details from server.....");
            progress.show();
        }

        StringRequest str=new StringRequest(Request.Method.POST, constants.get_details, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(progress.isShowing())
                {
                    progress.dismiss();
                }

                try {
                    JSONArray array=new JSONArray(response);
                    JSONObject obj=array.getJSONObject(0);
                    name.setText(obj.getString("name"));
                    email.setText(obj.getString("email"));
                    old_password_check=obj.getString("password");
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
                Display("Some Error occured,may be due to bad internet connection or server problem");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> maps=new HashMap<>();
                maps.put("mobile",mobile);
                return maps;
            }
        };
        RequestQueue r= Volley.newRequestQueue(this);
        r.add(str);
    }

    private void Display(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void initialize()
    {
        name =(EditText)findViewById(R.id.text_name);
        email =(EditText)findViewById(R.id.text_email);
        old_password =(EditText)findViewById(R.id.text_cur_pass);
        new_password =(EditText)findViewById(R.id.text_new_pass);
        button_save_change=(Button)findViewById(R.id.button_save_change);
        shared=getSharedPreferences(constants.foodsingh,MODE_PRIVATE);
        progress=new ProgressDialog(this);
        progress.setCancelable(false);
    }


    private Boolean checking_net_permission() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

































    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu) {
            // Handle the camera action
            Intent a=new Intent(getApplicationContext(),menu.class);
            a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);

        } else if (id == R.id.cart) {
            Intent a=new Intent(getApplicationContext(),cart.class);
            a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);

        } else if (id == R.id.orders) {
            Intent a=new Intent(getApplicationContext(),order_history.class);
            a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);

        } else if (id == R.id.SignOut) {

            shared.edit().remove("address").apply();
            shared.edit().remove("password").apply();
            shared.edit().remove("mobile").apply();

            this.finish();
            Intent intent=new Intent(getApplicationContext(),login_page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
        else if(id==R.id.details)
        {

        }

        else if(id==R.id.support)
        {
            Intent a=new Intent(getApplicationContext(),Support.class);
            a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
