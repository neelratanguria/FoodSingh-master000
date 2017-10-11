package com.fsingh.pranshooverma.foodsingh;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ViewPager.OnPageChangeListener {

    ProgressDialog progress;
    RecyclerView recylerView;
    RecyclerView.LayoutManager layoutmanager;
    List<String> categories=new ArrayList<>();
    List<String> images=new ArrayList<>();
    categoryAdapter adapter;
    localdatabase local;
    ImageButton next, back;
    boolean nav=true;
    pagerAdapter pageradater;
    NavigationView navigationView;
    ViewPager pager;
    AppBarLayout appBarLayout;
    public static int width;
    public static TextView cartitemcount1;
    boolean swipe = false;
    RecyclerView.ItemDecoration itemDecoration;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        //Display(versionStatus());


//FOR NAVIGATION DRAWER
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

         navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getBackground().setAlpha(122);

        //manipulate navigation drawer

         manipulatenavigationdrawer();


        ////////////////////////

        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

///////////////////////////////////////////////////////////////////////////////////////////////////////

//CODING  CODING CODING CODING
        //here the coding part is:
        nav=true;
        initialize();

        next = (ImageButton) findViewById(R.id.right_arrow);
        back = (ImageButton) findViewById(R.id.left_arrow);
        width = getScreenWidth();
        if(checking_net_permission())
        {
            getting_categories();
        }
        else {
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
        local = new localdatabase();
        pageradater = new pagerAdapter(local);
        pager.setAdapter(pageradater);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = pager.getCurrentItem() + 1;
                pager.setCurrentItem(current);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = pager.getCurrentItem() - 1;
                if (current >= 0)
                    pager.setCurrentItem(current);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe = true;
                recylerView.removeItemDecoration(itemDecoration);
                categories.clear();
                images.clear();

                getting_categories();

            }
        });

    }

    private void action(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(menu.this);
        dialog.setTitle("Announcement");
        dialog.setCancelable(false);
        dialog.setMessage("You are using an older version of this app. To continue using this app, Please update");
        dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //rec = true;
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

    private String versionStatus(){
        SharedPreferences sharedPreferences = getSharedPreferences(constants.foodsingh, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        return sharedPreferences.getString("version","null");
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


    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/android.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void initialize() {
        pager = (ViewPager) findViewById(R.id.view_pager);
        appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        progress=new ProgressDialog(this);
        progress.setCancelable(false);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        //swipeRefreshLayout.canScrollVertically()
        recylerView=(RecyclerView) findViewById(R.id.recyclerView);
        layoutmanager=new GridLayoutManager(this,3);
        recylerView.setLayoutManager(layoutmanager);
        recylerView.setNestedScrollingEnabled(true);
        itemDecoration=new GridSpacingItemDecoration(1,dpToPx(1),true);
        setTypeface();
        shared=getSharedPreferences(constants.foodsingh,MODE_PRIVATE);

    }

    private void setTypeface(){
        TextView t1 = (TextView)findViewById(R.id.new_location);
        TextView t2 = (TextView)findViewById(R.id.attack);
        Typeface t = Typeface.createFromAsset(getAssets(), "fonts/android.ttf");
        t1.setTypeface(t);
        t2.setTypeface(t);
    }

    public int getScreenWidth() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        return width;
    }

    public static int showRandomInteger(int aStart, int aEnd, Random aRandom) {
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        int randomNumber = (int) (fraction + aStart);
        return randomNumber;
    }

    private void getting_categories() {
        if(!swipeRefreshLayout.isRefreshing()) {
            progress.setMessage("Fetching Data.....");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
        }
        StringRequest as=new StringRequest(Request.Method.POST, constants.get_categories, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(progress.isShowing())
                {
                    progress.dismiss();
                }

                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                try {
                    JSONArray a=new JSONArray(response);
                    for(int i=0;i<a.length();i++)
                    {
                        JSONObject js=a.getJSONObject(i);
                        String nm=js.getString("name");
                        String img=js.getString("image");
                        categories.add(nm);
                        images.add(img);
                    }
                    getting_service_status();
                    send_to_adapter();
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
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }

                Display("Some Error occurred,may be due to bad internet connection...");
            }
        });
        RequestQueue qw= Volley.newRequestQueue(this);
        qw.add(as);
    }

    private void getting_service_status() {
        if(progress.isShowing())
        {
            progress.dismiss();
        }
        else
        {
            if(!swipeRefreshLayout.isRefreshing()) {
                progress.setMessage("Getting Kitchen Status...");
                progress.show();
            }
        }

        StringRequest str=new StringRequest(Request.Method.POST, constants.get_service_status, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(progress.isShowing())
                {
                    progress.dismiss();
                }
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }

                try {
                    JSONObject a=new JSONObject(response);
                    String b=a.getString("service");
                    if(b.equals("true"))
                    {
                        Display("Kitchen Is Open....");
                    }
                    else {
                        Display("Kitchen is closed....");
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
                Display("Some error occured:May be due to server fault or bad internet connection");
            }
        });

        RequestQueue s=Volley.newRequestQueue(this);
        s.add(str);
    }


    private Boolean checking_net_permission() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void Display(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void send_to_adapter()
    {
        adapter=new categoryAdapter(this,categories,images);
        recylerView.setAdapter(adapter);
        recylerView.addItemDecoration(itemDecoration);


        recylerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == local.url.length - 1) {
            next.setVisibility(View.GONE);
        } else {
            next.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //writing function for Categories
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }




        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }



    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    //For navigation drawer,by android studio...



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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem=menu.findItem(R.id.action_cart);
        View actionView= MenuItemCompat.getActionView(menuItem);
        cartitemcount1=(TextView) actionView.findViewById(R.id.cart_badge);

        cartitemcount1.setText(String.valueOf(constants.items_name.size()));

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ssd=new Intent(getApplicationContext(),cart.class);
                startActivity(ssd);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //    if (id == R.id.action_settings) {
        //      return true;
        // }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu) {
            // Handle the camera action
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

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }
}
