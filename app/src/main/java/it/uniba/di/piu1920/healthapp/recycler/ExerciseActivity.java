package it.uniba.di.piu1920.healthapp.recycler;

import android.content.Context;
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
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import it.uniba.di.piu1920.healthapp.DetailsActivity;
import it.uniba.di.piu1920.healthapp.Home;
import it.uniba.di.piu1920.healthapp.R;
import it.uniba.di.piu1920.healthapp.classes.Esercizio;
import it.uniba.di.piu1920.healthapp.classes.Item;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;

public class ExerciseActivity extends AppCompatActivity {

    private List<Item> listhome= new ArrayList<>();
    private static final String TAG_SUCCESS = "success"; //utilizzato a livello di tag per determinare se la chiamata ha prodotto risultati
    JSONArray arr = null; //array per il recupero json
    List<Esercizio> lista=new ArrayList<>(); //array list per memorizzare gli esercizi letti
    RecyclerView rv; //recyclerview
    private static String url_get_bozze = "http://ddauniba.altervista.org/HealthApp/get_esercizi.php"; //url per il recupero degli esercizi dal php
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_exercices);

        rv= (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        new GetEsercizi().execute(); //chiamata al thread asincrono
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ExerciseActivity.this, Home.class);
                startActivity(i);
                finish();
            }
        });

        rv.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), rv,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {
                        Bundle x=new Bundle();
                        Intent i = new Intent(ExerciseActivity.this, DetailsActivity.class);//dichiaro l'intent da richiamare con il contesto corrente
                        x.putSerializable("ogg",lista.get(position)); //passo nel bundle l'intero oggetto cliccato
                        i.putExtra("bund",x);  //inserisco il bundle come parametro di passaggio nell'activity
                        startActivity(i);   //starto l'activity
                        finish(); //termino l'activity corrente
                    }

                    public void onLongClickItem(View v, int position) {
                        System.out.println("On Long Click Item interface");
                    }
                }));

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

    class GetEsercizi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            String ris=null;
            if(isWorkingInternetPersent()){
                TwoParamsList params = new TwoParamsList();
                JSONObject json = new JSONParser().makeHttpRequest(url_get_bozze, JSONParser.GET, params);
              //  Log.d("Esercizi: ", json.toString());
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        arr = json.getJSONArray("esercizio");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            int id = Integer.parseInt(c.getString("id"));
                            int tipo = Integer.parseInt(c.getString("tipo"));
                            String nome=c.getString("nome");
                            String nomeC=c.getString("nomeC");
                            String esecuzione = Html.fromHtml(c.getString("esecuzione")).toString();
                            String link=c.getString("link");
                            //   String link=c.getString("link");
                            Esercizio x=new Esercizio( nome,  nomeC,  link,  esecuzione,  id,  tipo);

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
                    Log.d("LISTA SIZE: ",""+lista.size());
                    for(int i=0;i<lista.size();i++){
                        Log.d("Link :",""+"http://ddauniba.altervista.org/HealthApp/img/"+lista.get(i).getLink());
                    }
                    ExAdapt ca = new ExAdapt(lista);
                    rv.setAdapter(ca);
                }
            });

        }


    }

    public class ExAdapt extends RecyclerView.Adapter<ExAdapt.MyViewHolder> {

        private List<Esercizio> listahome;

        /**
         * View holder class
         * */
        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView name;


            public MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);



            }
        }

        public ExAdapt(List<Esercizio> listahomet) {
            this.listahome = listahomet;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_exercise, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ExAdapt.MyViewHolder holder, int position) {

            Esercizio c = listahome.get(position);
            System.out.println("Bind ["+holder+"] - Pos ["+position+"]"+c.getNome());
            holder.name.setText(c.getNome());
           // Picasso.with(ExerciseActivity.this).load("http://ddauniba.altervista.org/HealthApp/img/"+c.getLink()).into( holder.image);

        }

        @Override
        public int getItemCount() {
            return listahome.size();
        }


    }

    public boolean isWorkingInternetPersent() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }
}