package it.uniba.di.piu1920.healthapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class SupportActivity extends AppCompatActivity {


    TextView log,reg,es,td,bm,pas,sch,qr,insc,diet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        log=findViewById(R.id.log);
        reg=findViewById(R.id.reg);
        es=findViewById(R.id.es);
        td=findViewById(R.id.td);
        bm=findViewById(R.id.bm);
        pas=findViewById(R.id.pas);
        sch=findViewById(R.id.sch);
        qr=findViewById(R.id.qr);
        insc=findViewById(R.id.insc);
        diet=findViewById(R.id.diet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_support));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportActivity.this, Home.class);
                startActivity(i);
                finish();
            }
        });
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportActivity.this, WebActivity.class);
                i.putExtra("nome","supp_log");
                startActivity(i);
                finish();
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportActivity.this, WebActivity.class);
                i.putExtra("nome","supp_reg");
                startActivity(i);
                finish();
            }
        });
        es.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportActivity.this, WebActivity.class);
                i.putExtra("nome","supp_es");
                startActivity(i);
                finish();
            }
        });
        td.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportActivity.this, WebActivity.class);
                i.putExtra("nome","supp_tdee");
                startActivity(i);
                finish();
            }
        });
        bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportActivity.this, WebActivity.class);
                i.putExtra("nome","supp_bmi");
                startActivity(i);
                finish();
            }
        });
        pas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportActivity.this, WebActivity.class);
                i.putExtra("nome","supp_pass");
                startActivity(i);
                finish();
            }
        });
        sch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportActivity.this, WebActivity.class);
                i.putExtra("nome","supp_sch");
                startActivity(i);
                finish();
            }
        });
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportActivity.this, WebActivity.class);
                i.putExtra("nome","supp_qr");
                startActivity(i);
                finish();
            }
        });
        insc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportActivity.this, WebActivity.class);
                i.putExtra("nome","supp_inssch");
                startActivity(i);
                finish();
            }
        });
        diet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupportActivity.this, WebActivity.class);
                i.putExtra("nome","supp_diet");
                startActivity(i);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(SupportActivity.this, Home.class);

        startActivity(i);
        finish();


    }


    public void showDialog(String mx) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_account))
                .setMessage(mx)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogs, int which) {
                        dialogs.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(getDrawable(R.drawable.nav_account))
                .show();
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


    }

}