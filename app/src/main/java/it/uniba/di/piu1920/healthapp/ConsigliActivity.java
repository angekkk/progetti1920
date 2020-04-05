package it.uniba.di.piu1920.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ConsigliActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consigli);
        WebView myWebView = (WebView) findViewById(R.id.web);
        myWebView.loadUrl("http://ddauniba.altervista.org/HealthApp/diete.html");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConsigliActivity.this, AlimentazioneActivity.class);
                startActivity(i);
                finish();
            }
        });
    }


}
