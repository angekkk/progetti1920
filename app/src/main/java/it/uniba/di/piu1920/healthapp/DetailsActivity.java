package it.uniba.di.piu1920.healthapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import it.uniba.di.piu1920.healthapp.classes.Esercizio;

public class DetailsActivity extends AppCompatActivity {

    Esercizio ex;
    TextView categoria,esecuzione;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        img=findViewById(R.id.img);
        categoria=findViewById(R.id.categoria);
        esecuzione=findViewById(R.id.esecuzione);
        Bundle  arg = getIntent().getExtras();
        Bundle x= arg.getBundle("bund");
        ex=(Esercizio) x.get("ogg");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(ex.getNome());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailsActivity.this, ExerciseActivity.class);
                startActivity(i);
                finish();
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
