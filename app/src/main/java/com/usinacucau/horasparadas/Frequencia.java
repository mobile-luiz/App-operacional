package com.usinacucau.horasparadas;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Frequencia extends AppCompatActivity {


    // sair do aplicativo apos 60 segundos
    private long lastPauseTime;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_frequencia );

        WebView webView = findViewById ( R.id.webView );
        WebSettings webSettings = webView.getSettings ();
        webSettings.setJavaScriptEnabled ( true );

        webView.setWebViewClient ( new WebViewClient () {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view , String url) {
                view.loadUrl ( url );
                return true;
            }
        } );

        webView.loadUrl ( "file:///android_asset/Frequencia.html" );
    }

    ///sair do aplicativo apos 2 minutos
    @Override
    protected void onPause() {
        super.onPause();
        lastPauseTime = System.currentTimeMillis();
        startTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopTimer();
    }

    private void startTimer() {
        timer = new CountDownTimer (120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                finish();
            }
        }.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}

