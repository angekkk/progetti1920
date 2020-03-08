package it.uniba.di.piu1920.healthapp;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.piu1920.healthapp.classes.Cliente;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;
import it.uniba.di.piu1920.healthapp.recycler.ExerciseActivity;
import it.uniba.di.piu1920.healthapp.recycler.Item;
import it.uniba.di.piu1920.healthapp.recycler.RecyclerItemListener;

public class GestioneClientiActivity extends AppCompatActivity {


    private static final String TAG_SUCCESS = "success";
    JSONArray arr = null;
    List<Cliente> lista=new ArrayList<>();
    RecyclerView rv;
    private static String url_get_clienti = "http://ddauniba.altervista.org/HealthApp/get_clienti.php"; //preleva i clienti a cui non è stata assegnata nessuna scheda
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_exercices);
        rv= (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        new GetClienti().execute();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GestioneClientiActivity.this, Home.class);
                startActivity(i);
                finish();
            }
        });

        rv.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), rv,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {

                        Intent i = new Intent(GestioneClientiActivity.this, CreateSchedaActivity.class);
                        i.putExtra("id",lista.get(position).getId());
                        startActivity(i);

                    }

                    public void onLongClickItem(View v, int position) {
                        System.out.println("On Long Click Item interface");
                    }
                }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    class GetClienti extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            String ris=null;
            if(isWorkingInternetPersent()){
                TwoParamsList params = new TwoParamsList();
                JSONObject json = new JSONParser().makeHttpRequest(url_get_clienti, JSONParser.GET, params);
                  Log.d("CLIENTI: ", json.toString());
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        arr = json.getJSONArray("clienti");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            int id = Integer.parseInt(c.getString("id"));
                            String nome=c.getString("nome");
                            String cognome=c.getString("cognome");
                            String email=c.getString("email");
                            Cliente x=new Cliente(id, nome,  cognome,  email);
                            lista.add(x);
                        }
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
            return ris;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("LISTA SIZE: ",""+lista.size());
                    ExAdapt ca = new ExAdapt(lista);
                    rv.setAdapter(ca);
                }
            });

        }


    }

    public class ExAdapt extends RecyclerView.Adapter<ExAdapt.MyViewHolder> {

        private List<Cliente> listahome;

        /**
         * View holder class
         * */
        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView nomec,cognomec;


            public MyViewHolder(View view) {
                super(view);
                nomec =(TextView) view.findViewById(R.id.nomec);
                cognomec= (TextView) view.findViewById(R.id.cognomec);

            }
        }

        public ExAdapt(List<Cliente> listahomet) {
            this.listahome = listahomet;
        }

        @NonNull
        @Override
        public ExAdapt.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_cliente, parent, false);
            return new ExAdapt.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ExAdapt.MyViewHolder holder, int position) {

            Cliente c = listahome.get(position);
            System.out.println("Bind ["+holder+"] - Pos ["+position+"]"+c.getNome());
            holder.nomec.setText(getString(R.string.nome)+": "+c.getNome());
            holder.cognomec.setText(getString(R.string.cognome)+": "+c.getCognome());

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