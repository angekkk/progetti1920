package it.uniba.di.piu1920.healthapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import it.uniba.di.piu1920.healthapp.classes.Item;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;
import it.uniba.di.piu1920.healthapp.recycler.RecyclerItemListener;

public class AlimentazioneActivity extends AppCompatActivity {

    private static final String TAG_SUCCESS = "success"; //utilizzato a livello di tag per determinare se la chiamata ha prodotto risultati
    JSONArray arr = null; //array per il recupero json
    List<Item> lista=new ArrayList<>(); //array list per memorizzare gli item
    RecyclerView rv; //recyclerview
    private static String url_get_diete = "http://ddauniba.altervista.org/HealthApp/get_dieta.php"; //url per il recupero degli esercizi dal php


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_exercices);
        lista.add(new Item(getString(R.string.al_consigli)));
        rv= (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        inizializza();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AlimentazioneActivity.this, Home.class);
                startActivity(i);
                finish();
            }
        });
        rv.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), rv,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {
                            if(position==0){
                                Intent i = new Intent(AlimentazioneActivity.this, ConsigliActivity.class);
                                startActivity(i);
                                finish();
                            }else{
                                Intent i = new Intent(AlimentazioneActivity.this, DietaActivity.class);
                                Bundle x =new Bundle();
                                x.putString("nome",lista.get(position).getName());
                                x.putString("cat",lista.get(position).getN2());
                                x.putString("desc",lista.get(position).getN3());
                                i.putExtra("bund",x);
                                startActivity(i);
                                finish();
                            }
                    }
                    public void onLongClickItem(View v, int position) {
                        System.out.println("On Long Click Item interface");
                    }
                }));

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AlimentazioneActivity.this, Home.class);
        startActivity(i);
        finish();
        return;
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

    //classe per il recupero delle diete
    class GetDiete extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            String ris=null;
            if(isWorkingInternetPersent()){
                TwoParamsList params = new TwoParamsList();
                JSONObject json = new JSONParser().makeHttpRequest(url_get_diete, JSONParser.GET, params);
                //  Log.d("Esercizi: ", json.toString());
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        arr = json.getJSONArray("dieta");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            int id = Integer.parseInt(c.getString("id_dieta"));
                            String categoria=c.getString("categoria");
                            String nome=c.getString("nome");
                            String desc=c.getString("descrizione");

                            //   String link=c.getString("link");
                            Item x=new Item(id,nome,categoria,desc);

                            lista.add(x);



                        }
                    } else {
                        Log.d("Esercizi: ","SUCCESS 0");
                    }
                } catch (Exception e) {
                    Log.d("Esercizi: ","ECCEZZIONE");
                    e.printStackTrace();
                }
            }



            return ris;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {

                    ExAdapt ex=new ExAdapt(lista);
                    rv.setAdapter(ex);
                }
            });

        }


    }

    public class ExAdapt extends RecyclerView.Adapter<ExAdapt.MyViewHolder> {

        private List<Item> lista;

        /**
         * View holder class
         * */
        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView name;
            public ImageView cat;

            public MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);
                cat = (ImageView) view.findViewById(R.id.image);


            }
        }

        public ExAdapt(List<Item> lista) {
            this.lista = lista;
        }

        @NonNull
        @Override
        public ExAdapt.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
            return new ExAdapt.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ExAdapt.MyViewHolder holder, int position) {

            Item c = lista.get(position);
            System.out.println("Bind ["+holder+"] - Pos ["+position+"]"+c.getName());
            holder.name.setText(c.getName().toUpperCase());
        }

        @Override
        public int getItemCount() {
            return lista.size();
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

    //metodo per inizializzare
    void inizializza(){
        if(isWorkingInternetPersent()){
              new GetDiete().execute();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.err_connessione))

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(AlimentazioneActivity.this, Home.class);
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
}
