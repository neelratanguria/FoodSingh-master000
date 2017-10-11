package com.fsingh.pranshooverma.foodsingh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Random;


public class Support extends AppCompatActivity {

    private WebView mWebView;
    private Random rand = new Random();
    private int value = rand.nextInt(10000);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        mWebView = (WebView) findViewById(R.id.support_web);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new myWebClient());


     //   mWebView.loadUrl("axuip.foodsingh.com/login.php");

       mWebView.loadUrl("http://foodsingh.com/support");

    }

    public class myWebClient extends WebViewClient
    {
        boolean cond = false;


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            SharedPreferences sharedPreferences = getSharedPreferences(constants.foodsingh, Context.MODE_PRIVATE);
            String nameBuffer = sharedPreferences.getString("name","_");
            String nameFinal = null;
            if(!nameBuffer.equals("_")){
               nameFinal = nameBuffer;
            }


            if(url.startsWith("mailto:")){

                String email = url.substring(7);

                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.setType("vnd.android.cursor.item/email");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {email});
                if(!nameFinal.equals(null)) {
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Customer Support ID:" + value + " " + nameFinal);
                }
                else {
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Customer Support ID:" + value + " ");
                }
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send mail using..."));

                mWebView.loadUrl("http://foodsingh.com/support");
                cond = true;

                return;
            }

            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                mWebView.loadUrl("http://foodsingh.com/support");
                cond = true;
                return;
            }

            if(cond==false) {
                Snackbar snackbar = Snackbar.make(view, "Loading Please Wait", Snackbar.LENGTH_LONG);
                snackbar.show();
            }



            super.onPageStarted(view, url, favicon);
        }
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (cond==false) {
                Snackbar snackbarError = Snackbar.make(view, "Error Connecting Server", Snackbar.LENGTH_LONG);
                snackbarError.show();
            }
            super.onReceivedError(view, request, error);
        }
    }
}


