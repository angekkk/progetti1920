package it.uniba.di.piu1920.healthapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.borjabravo.readmoretextview.ReadMoreTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import it.uniba.di.piu1920.healthapp.classes.Esercizio;

public class DietaActivity extends AppCompatActivity {

    Esercizio ex;
    TextView categoria,nome;
    ReadMoreTextView desc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dieta);

        categoria=findViewById(R.id.cat);
     //   nome=findViewById(R.id.nome);
        desc=findViewById(R.id.desc);
        Bundle  arg = getIntent().getExtras();
        Bundle x= arg.getBundle("bund");
        categoria.setText(x.getString("cat").toUpperCase());
       // nome.setText(x.getString("nome"));
        desc.setText(x.getString("desc"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(x.getString("nome"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DietaActivity.this, AlimentazioneActivity.class);
                startActivity(i);
                finish();
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
        Intent i = new Intent(DietaActivity.this, AlimentazioneActivity.class);
        startActivity(i);
        finish();
        return;
    }

}
