package com.fsingh.pranshooverma.foodsingh;

import android.content.Intent;
import android.graphics.Typeface;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class FakeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fake);
        TextView t = (TextView)findViewById(R.id.calm);
        TextView home = (TextView)findViewById(R.id.menu);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/android.ttf");
        t.setTypeface(tf);
        home.setTypeface(tf);
        constants.item_name_deb.clear();
        constants.item_quant_deb.clear();
        constants.items_price.clear();
        constants.items_name.clear();
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent a=new Intent(getApplicationContext(),menu.class);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(a);
            }
        });
    }
}
