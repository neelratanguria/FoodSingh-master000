package com.fsingh.pranshooverma.foodsingh;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Display;
import android.view.Gravity;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class place_order_activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView total_amount,final_amount,textCoupon;
    EditText address,comments;
    Button finally_place_order;
    List<String> local_list=new ArrayList<>();
    int final_am=0;
    boolean nav=true;
    String mobile_number;
    ProgressDialog progress;
    NavigationView navigationView;

    int counter=0;
    int discount_amount=0;
    int after_discount=0;


    RelativeLayout relativeLayout_coupon;

    SharedPreferences shared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_place_order_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Typeface t = Typeface.createFromAsset(getAssets(), "fonts/android.ttf");
        TextView toolbarText = (TextView) findViewById(R.id.toolbarText);
        toolbarText.setTypeface(t);
        toolbarText.setTypeface(t);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /////////////////////////////////////
//coding coding

        manipulatenavigationdrawer();
        initialize();

        gettin_amount();

        coupon_from_data();

        after_discount = final_am;

        finally_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checking_net_permission()) {
                    if (final_am == 0) {
                        Toast.makeText(place_order_activity.this, "You have not added anything in the cart", Toast.LENGTH_SHORT).show();
                    } else if(constants.min_order>Integer.parseInt(final_amount.getText().toString())){
                        Display("Order can't be placed. The minimum order is "+constants.min_order);

                    } else{
                        String add = address.getText().toString();
                        String com=comments.getText().toString();
                        if (add.length() >= 7) {
                            SharedPreferences.Editor edit = shared.edit();
                            edit.putString("address", add);
                            edit.apply();
                            send_to_deb(add,com);
                        } else {
                            Toast.makeText(place_order_activity.this, "Give your full address", Toast.LENGTH_SHORT).show();
                        }

                    }

                } else {
                    Toast.makeText(place_order_activity.this, "you dont have net connection...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        relativeLayout_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;

                if (counter % 2 == 1) {
                    if (final_am != 0 & discount_amount != 0) {
                        Snackbar.make(view, "Coupon applied.....", Snackbar.LENGTH_SHORT).show();
                        after_discount = final_am - (((discount_amount) * final_am) / 100);
                        final_amount.setText(String.valueOf(after_discount));
                        textCoupon.setBackgroundResource(R.drawable.strike);
                        Display("Coupon Applied!");
                    } else {
                        Snackbar.make(view, "Cant apply the coupon.....", Snackbar.LENGTH_SHORT).show();

                    }
                } else {
                    textCoupon.setBackgroundResource(R.drawable.normal);
                    Snackbar.make(view, "Coupon removed.....", Snackbar.LENGTH_SHORT).show();
                    final_amount.setText(String.valueOf(final_am));
                    after_discount = final_am;
                    Display("Coupon Removed!");
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

    private void coupon_from_data() {
        if(checking_net_permission())
        {
            getting_coupon();
        }
        else
        {
            nav=false;
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

    private void getting_coupon() {

        progress.setMessage("Fetching coupon code....");

        if(!progress.isShowing())
        {
         progress.show();
        }

        StringRequest str=new StringRequest(Request.Method.POST, constants.discount_coupon, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(progress.isShowing())
                {
                    progress.dismiss();
                }
                try {
                   // Display(response);
                    JSONObject obj=new JSONObject(response);
                    String dis=obj.getString("discount");
                    constants.min_order = obj.getInt("min_order");
                    discount_amount= Integer.parseInt(dis);
                    if(dis.equals("0"))
                    {
                        textCoupon.setText("No coupon to reedem");
                        textCoupon.setGravity(Gravity.CENTER);
                    }
                    else
                    {
                        textCoupon.setText("Click this to redeem "+dis+" % discount on final amount");

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
                Toast.makeText(place_order_activity.this, "Some error occured may be due to bad internet connection", Toast.LENGTH_SHORT).show();
                discount_amount=0;
            }
        });
        RequestQueue r=Volley.newRequestQueue(this);
        r.add(str);

    }


    private void  Display(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void send_to_deb(final String addy, final String com) {
        progress.setMessage("Sending Order........");
        progress.setCancelable(false);
        progress.show();

    //    Toast.makeText(this, addy+"\n"+String.valueOf(final_am)+"\n"+local_list+"\n"+mobile_number, Toast.LENGTH_SHORT).show();
        StringRequest str=new StringRequest(Request.Method.POST, constants.send_to_debian, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(progress.isShowing())
                {
                    progress.dismiss();
                }
                try {
                    JSONObject object = new JSONObject(response);
                    String main_status = object.getString("message");
                    if(main_status.equals("SUCCESS")){

                        startActivity(new Intent(place_order_activity.this, FakeActivity.class));
                        finish();
                    }else{
                        Toast.makeText(place_order_activity.this, "We are unable to process your order right now.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(place_order_activity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> maps=new HashMap<>();
                maps.put("item",local_list.toString());
                maps.put("amount", String.valueOf(after_discount));
                maps.put("mobile",mobile_number);
                maps.put("address",addy);
                maps.put("comments",com);
                return maps;
            }
        };
        RequestQueue re= Volley.newRequestQueue(this);
        re.add(str);
        str.setRetryPolicy(new DefaultRetryPolicy(0,  DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void gettin_amount() {
        Bundle aa=getIntent().getExtras();
        total_amount.setText(aa.getString("sum"));
        final_amount.setText(aa.getString("sum"));

        final_am= Integer.parseInt(aa.getString("sum"));

        for(int i=0;i<constants.item_name_deb.size();i++)
        {
            local_list.add(constants.item_name_deb.get(i)+"-"+constants.item_quant_deb.get(i)+", ");
        }
    }

    private void initialize() {
        TextView t1 = (TextView)findViewById(R.id.textView2);
        TextView t2 = (TextView)findViewById(R.id.textView4);
        TextView t3 = (TextView)findViewById(R.id.textView7);
        total_amount=(TextView)findViewById(R.id.total_amount);
        final_amount=(TextView)findViewById(R.id.final_amount);
        address=(EditText) findViewById(R.id.delivery_address);
        finally_place_order=(Button)findViewById(R.id.final_order);
        comments=(EditText)findViewById(R.id.comments);
        progress=new ProgressDialog(this);
        progress.setCancelable(false);
        shared=getSharedPreferences(constants.foodsingh,MODE_PRIVATE);
        mobile_number=shared.getString("mobile","123");
        if(mobile_number.equals("123"))
        {
            Toast.makeText(this, "Number is missing,kindly logout and log in again", Toast.LENGTH_SHORT).show();
            finish();
        }
        relativeLayout_coupon=(RelativeLayout)findViewById(R.id.relative_layout_coupon);
        textCoupon=(TextView) findViewById(R.id.textView_coupon);
        Typeface t = Typeface.createFromAsset(getAssets(), "fonts/android.ttf");
        total_amount.setTypeface(t);
        final_amount.setTypeface(t);
        address.setTypeface(t);
        finally_place_order.setTypeface(t);
        t1.setTypeface(t);
        t2.setTypeface(t);
        t3.setTypeface(t);
        textCoupon.setTypeface(t);

        String addy=shared.getString("address","no");
        if(addy.equals("no"))
        {
            address.setHint("Enter Your Full address");
        }
        else {
            address.setText(addy);
        }

    }

    private Boolean checking_net_permission() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////






    //////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        if(nav){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }else{
            finish();
        }
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
            Intent a=new Intent(getApplicationContext(),details.class);
            a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);

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
