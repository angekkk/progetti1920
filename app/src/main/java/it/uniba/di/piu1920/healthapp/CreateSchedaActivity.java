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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

import it.uniba.di.piu1920.healthapp.classes.Cliente;
import it.uniba.di.piu1920.healthapp.classes.Esercizio;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;
import it.uniba.di.piu1920.healthapp.recycler.ExerciseActivity;
import it.uniba.di.piu1920.healthapp.recycler.Item;
import it.uniba.di.piu1920.healthapp.recycler.RecyclerItemListener;

public class CreateSchedaActivity extends AppCompatActivity {


    private List<Item> listhome = new ArrayList<>();
    private static final String TAG_SUCCESS = "success";
    JSONArray arr = null;
    List<Esercizio> lista = new ArrayList<>();
    RecyclerView rv;
    private static String url_get_bozze = "http://ddauniba.altervista.org/HealthApp/get_esercizi.php";

    Spinner spinner;
    ImageButton add;
    Button ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_scheda_activity);
        rv = (RecyclerView) findViewById(R.id.scheda);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        spinner = findViewById(R.id.spinner);
        add = findViewById(R.id.add);
        ok = findViewById(R.id.ok);
        new GetProdotti().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateSchedaActivity.this, GestioneClientiActivity.class);
                startActivity(i);
                finish();
            }
        });

        rv.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), rv,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {

                        Bundle x = new Bundle();

                        // Intent i = new Intent(GestioneClientiActivity.this, DetailsActivity.class);
                        // x.putSerializable("ogg",lista.get(position));
                        // i.putExtra("bund",x);
                        // startActivity(i);

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


    class GetProdotti extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        List<String> categories = new ArrayList<>();
        protected String doInBackground(String... args) {
            String ris = null;
            if (isWorkingInternetPersent()) {
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
                            String nome = c.getString("nome");
                            String nomeC = c.getString("nomeC");
                            String esecuzione = Html.fromHtml(c.getString("esecuzione")).toString();
                            String link = c.getString("link");
                            //   String link=c.getString("link");
                            Esercizio x = new Esercizio(nome, nomeC, link, esecuzione, id, tipo);
                            categories.add(nome);
                            lista.add(x);


                        }
                    } else {
                        Log.d("Esercizi: ", "SUCCESS 0");
                    }
                } catch (Exception e) {
                    Log.d("Esercizi: ", "ECCEZZIONE");
                    e.printStackTrace();
                }
            }


            return ris;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    // Spinner Drop down elements



                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CreateSchedaActivity.this, android.R.layout.simple_spinner_item, categories);

                    spinner.setAdapter(dataAdapter);
                }
            });

        }

        public class ExAdapt extends RecyclerView.Adapter<ExAdapt.MyViewHolder> {

            private List<Esercizio> listahome;

            /**
             * View holder class
             */
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
                System.out.println("Bind [" + holder + "] - Pos [" + position + "]" + c.getNome());
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


}