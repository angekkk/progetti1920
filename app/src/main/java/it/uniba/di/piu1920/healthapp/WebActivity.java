package it.uniba.di.piu1920.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consigli);
        WebView myWebView = (WebView) findViewById(R.id.web);
        Bundle  arg = getIntent().getExtras();
        myWebView.loadUrl("http://ddauniba.altervista.org/HealthApp/"+arg.getString("nome")+".html");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arg.getString("nome").contentEquals("diete")){
                    Intent i = new Intent(WebActivity.this, AlimentazioneActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Intent i = new Intent(WebActivity.this, SupportActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });
    }


}