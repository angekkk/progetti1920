package it.uniba.di.piu1920.healthapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import it.uniba.di.piu1920.healthapp.classes.Esercizio;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;
import it.uniba.di.piu1920.healthapp.recycler.RecyclerItemListener;

public class SchedaActivity extends AppCompatActivity {

    private static final String TAG_SUCCESS = "success"; //utilizzato a livello di tag per determinare se la chiamata ha prodotto risultati
    JSONArray arr = null; //array per il recupero json
    List<Esercizio> lista=new ArrayList<>(); //array list per memorizzare gli esercizi letti
    RecyclerView rv; //recyclerview
    private static String url_get_esercizi = "http://ddauniba.altervista.org/HealthApp/get_esercizi_scheda.php"; //link al recupero degli esercizi della scheda
    int idscheda; //id della scheda

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_exercices);

        rv= (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        Bundle arg = getIntent().getExtras();
        if(!arg.getString("idscheda").isEmpty()){ //controllo che sia stato passato l'id della scheda da recuperare
            idscheda=Integer.parseInt(arg.getString("idscheda"));
        }
        inizializza();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_scheda));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SchedaActivity.this, Home.class);
                startActivity(i);
                finish();
            }
        });

        rv.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), rv,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {
                        System.out.println("On Click Item interface");
                    }
                    public void onLongClickItem(View v, int position) {
                        System.out.println("On Long Click Item interface");
                    }
                }));

    }

    //metodo per lanciare il thread asincrono per la chiamata al php
    void inizializza(){
        if(isWorkingInternetPersent()){
            new GetEsercizi().execute(); //chiamata al thread asincrono per il recupero della scheda
        }else{

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.err_connessione))

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(SchedaActivity.this, Home.class);
                            startActivity(i);
                            finish();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(getDrawable(R.drawable.error))
                    .show();
        }
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
        Intent i = new Intent(SchedaActivity.this, Home.class);
        startActivity(i);
        finish();
        return;
    }

    class GetEsercizi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            String ris=null;
            if(isWorkingInternetPersent()){
                TwoParamsList params = new TwoParamsList();
                params.add("idscheda",""+idscheda);
                JSONObject json = new JSONParser().makeHttpRequest(url_get_esercizi, JSONParser.GET, params);
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        ris="ok";
                        arr = json.getJSONArray("esercizio");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            int id = Integer.parseInt(c.getString("id"));
                            int tipo = Integer.parseInt(c.getString("tipo"));
                            String nome=c.getString("nome");
                            String esecuzione = Html.fromHtml(c.getString("esecuzione")).toString();
                            String link=c.getString("link");
                            //   String link=c.getString("link");
                            Esercizio x=new Esercizio( nome,  "",  link,  esecuzione,  id,  tipo);
                            lista.add(x);
                        }
                    } else {
                        Log.d("Esercizi: ","SUCCESS 0");
                    }
                } catch (Exception e) {
                    Log.d("Esercizi: ","ECCEZZIONE");
                    e.printStackTrace();
                }
            }else{
                Snackbar.make(getCurrentFocus(), getString(R.string.err_connessione), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            return ris;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if(file_url!=null){
                        Log.d("LISTA SIZE: ",""+lista.size());
                        for(int i=0;i<lista.size();i++){
                            Log.d("Link :",""+"http://ddauniba.altervista.org/HealthApp/img/"+lista.get(i).getLink());
                        }
                        ExAdapt ca = new ExAdapt(lista);
                        rv.setAdapter(ca);
                    }else{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(SchedaActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.dialog, null);
                        String message=getString(R.string.err_connessione);
                        builder.setMessage(message);
                        builder.setView(view);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(SchedaActivity.this, Home.class);
                                startActivity(i);
                                finish();
                            }
                        });

                        builder.show();
                    }
                }
            });

        }


    }

    public class ExAdapt extends RecyclerView.Adapter<ExAdapt.MyViewHolder> {

        private List<Esercizio> listahome;


        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView title,esec;
            ImageView img;


            public MyViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.title);
                esec = (TextView) view.findViewById(R.id.esec);
                img = (ImageView) view.findViewById(R.id.img);

            }
        }

        public ExAdapt(List<Esercizio> listahomet) {
            this.listahome = listahomet;
        }

        @NonNull
        @Override
        public ExAdapt.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_scheda, parent, false);
            return new ExAdapt.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ExAdapt.MyViewHolder holder, int position) {

            Esercizio c = listahome.get(position);
            System.out.println("Bind ["+holder+"] - Pos ["+position+"]"+c.getNome());
            holder.title.setText(c.getNome());
            holder.esec.setText(c.getEsecuzione());
            Picasso.get().load("http://ddauniba.altervista.org/HealthApp/img/"+c.getLink()).into( holder.img);

        }

        @Override
        public int getItemCount() {
            return listahome.size();
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

}