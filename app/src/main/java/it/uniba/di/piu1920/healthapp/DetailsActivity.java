package it.uniba.di.piu1920.healthapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import it.uniba.di.piu1920.healthapp.classes.Esercizio;

public class DetailsActivity extends AppCompatActivity {

    Esercizio ex;
    TextView categoria,esecuzione;
    ImageView img;
    Button start, stop, reset;
    int min, s, ms;
    Handler h = new Handler();
    private TextView tv;
    private long startTime, timeMS, timeSB, update;
    public Runnable r = new Runnable() {
        @Override
        public void run() {
            timeMS = SystemClock.uptimeMillis() - startTime;
            update = timeSB + timeMS;
            s = (int) (update / 1000);
            min = s / 60;
            s = s % 60;
            ms = (int) (update % 1000);
            tv.setText("" + min + ":" + String.format("%02d", s) + ":" + String.format("%03d", ms));
            h.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        img=findViewById(R.id.img);
        categoria=findViewById(R.id.categoria);
        esecuzione=findViewById(R.id.esecuzione);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        reset = findViewById(R.id.reset);
        tv = findViewById(R.id.textView);
        Bundle  arg = getIntent().getExtras();
        Bundle x= arg.getBundle("bund");
        ex=(Esercizio) x.get("ogg");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(ex.getNome());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arg.getString("in")!=null){
                    Intent i = new Intent(DetailsActivity.this, ExInDoorActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Intent i = new Intent(DetailsActivity.this, ExOutDoorActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });
        esecuzione.setText(ex.getEsecuzione());
        categoria.setText(ex.getNomecategoria());
        if(isWorkingInternetPersent()){
            Picasso.get().load("http://ddauniba.altervista.org/HealthApp/img/"+ex.getLink()).into(img);//carico l'immagine dal server
        }else{
            Snackbar.make(getCurrentFocus(), getString(R.string.err_connessione), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = SystemClock.uptimeMillis();
                h.postDelayed(r, 0);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSB += timeMS;
                h.removeCallbacks(r);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = 0;
                timeMS = 0;
                timeSB = 0;
                s = 0;
                min = 0;
                ms = 0;
                h.removeCallbacks(r);
                tv.setText("0:00:000");
            }
        });
    }

    //metodo per controllare la connessione ad internet
    public boolean isWorkingInternetPersent() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);//controllare il servizio delle connessioni
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo(); //recupero di tutte le informazioni
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) { //quando trovo lo stato di connesso, esco con return true
                        return true;
                    }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
            Intent i = new Intent(DetailsActivity.this, ExerciseActivity.class);
            startActivity(i);
            finish();
        return;
    }

}
