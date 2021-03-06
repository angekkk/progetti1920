package it.uniba.di.piu1920.healthapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.borjabravo.readmoretextview.ReadMoreTextView;


//check del 22/06
public class DietaActivity extends AppCompatActivity {

    TextView categoria;
    ReadMoreTextView desc;
    TextView colazione;
    TextView pranzo;
    TextView cena;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_details);

        categoria=findViewById(R.id.cat);
        desc=findViewById(R.id.desc);
        colazione=findViewById(R.id.tx4);
        pranzo=findViewById(R.id.tx6);
        cena=findViewById(R.id.tx8);
        Bundle  arg = getIntent().getExtras();
        Bundle x= arg.getBundle("bund");
        categoria.setText(x.getString("cat").toUpperCase());
        desc.setText(x.getString("desc"));


        colazione.setText(x.getString("tx4"));
        pranzo.setText(x.getString("tx6"));
        cena.setText(x.getString("tx8"));


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
