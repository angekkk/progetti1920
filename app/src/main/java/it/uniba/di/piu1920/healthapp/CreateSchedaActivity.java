package it.uniba.di.piu1920.healthapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import it.uniba.di.piu1920.healthapp.classes.Esercizio;
import it.uniba.di.piu1920.healthapp.classes.SessionManager;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;
import it.uniba.di.piu1920.healthapp.recycler.SwipeToDeleteCallback;

public class CreateSchedaActivity extends AppCompatActivity {

    private static final String TAG_SUCCESS = "success";
    JSONArray arr = null;
    List<Esercizio> lista = new ArrayList<>();
    List<Esercizio> scheda = new ArrayList<>();
    RecyclerView rv;
    private static String url_get_bozze = "http://ddauniba.altervista.org/HealthApp/get_esercizi.php";
    private static String url_inserisci_scheda = "http://ddauniba.altervista.org/HealthApp/inserisci_scheda.php";
    private static String url_id_scheda = "http://ddauniba.altervista.org/HealthApp/get_id_scheda.php";
    private static String url_inserisci_esercizio = "http://ddauniba.altervista.org/HealthApp/inserisci_esercizio.php";
    private static String url_get_esercizi = "http://ddauniba.altervista.org/HealthApp/get_esercizi_scheda.php";
    private static String url_modifica_esercizio = "http://ddauniba.altervista.org/HealthApp/elimina_esercizi_scheda.php";
    ExAdapt ca;
    Spinner spinner;
    ImageButton add;
    Button ok;
    LinearLayoutManager llm;
    ConstraintLayout con;
    int idutente;
    int idscheda=0;//id della scheda generato
    int idesercizio; //id esercizio corrente
    SessionManager session;
    String conca="";
    int MOD=0; //variabile usata per controllare se si è entrati nella schermata per inserire una nuova scheda o come modifica della precedente
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_scheda_activity);
        rv = (RecyclerView) findViewById(R.id.scheda);
        rv.setHasFixedSize(true);
         llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        spinner = findViewById(R.id.spinner);
        add = findViewById(R.id.add);
        ok = findViewById(R.id.ok);
        con=findViewById(R.id.constraint);
        Bundle arg = getIntent().getExtras();
        session=new SessionManager(this);
        idutente=arg.getInt("id");
        inizializza();
        if(!arg.getString("idscheda").contentEquals("no")){
            idscheda=Integer.parseInt(arg.getString("idscheda"));
            MOD=1;//la modalità è attiva sulla MODIFICA
            new GetEserciziScheda().execute();
        }
        enableSwipeToDeleteAndUndo();//abilito lo swipe per eliminare nella recycler view
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(CreateSchedaActivity.this, GestioneClientiActivity.class);
                    startActivity(i);
                    finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addEsercizioScheda((String) spinner.getSelectedItem())){//se l'elemento è stato inserito nell'array, allora ricreo l'adapter per la recycler view
                     ca = new ExAdapt(scheda);
                     rv.setAdapter(ca);
                }
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWorkingInternetPersent()){
                    if(MOD==1){
                        new AlertDialog.Builder(CreateSchedaActivity.this)
                                .setTitle(getString(R.string.modifica_scheda))
                                .setMessage(getString(R.string.conferma))

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new modifica_esercizi().execute();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }else {
                        new AlertDialog.Builder(CreateSchedaActivity.this)
                                .setTitle(getString(R.string.invia_scheda))
                                .setMessage(getString(R.string.conferma))

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new inserisci_scheda().execute();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                }else{
                    Snackbar.make(getCurrentFocus(), getString(R.string.err_connessione), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }


    class GetEserciziScheda extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        List<String> categories = new ArrayList<>();
        protected String doInBackground(String... args) {
            String ris = null;

            TwoParamsList params = new TwoParamsList();
            params.add("idscheda",""+idscheda);
            JSONObject json = new JSONParser().makeHttpRequest(url_get_esercizi, JSONParser.GET, params);
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
                        String esecuzione = Html.fromHtml(c.getString("esecuzione")).toString();
                        String link = c.getString("link");
                        //   String link=c.getString("link");
                        Esercizio x = new Esercizio(nome, "", link, esecuzione, id, tipo);
                        categories.add(nome);
                        scheda.add(x);
                        lista.add(x);


                    }
                } else {
                    Log.d("Esercizi: ", "SUCCESS 0");
                }
            } catch (Exception e) {
                Log.d("Esercizi: ", "ECCEZZIONE");
                e.printStackTrace();
            }



            return ris;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    conca="";
                    for(int i=0;i<scheda.size();i++){
                        idesercizio=scheda.get(i).getId(); //prendo l'id dell'esercizio e lo passo alla chiamata
                        conca=conca+idesercizio+";";
                    }
                    ca = new ExAdapt(scheda);
                    rv.setAdapter(ca);

                }
            });

        }

    }

    //metodo per controllare l'esercizio nella list LISTA, e aggiungerlo nella list SCHEDA
    boolean addEsercizioScheda(String nome){
        boolean aggiunto=false;
        for(int i=0;i<lista.size();i++){
            if(lista.get(i).getNome().contentEquals(nome)){
                if(!scheda.contains(lista.get(i))){
                    scheda.add(lista.get(i));
                    System.out.println("ESERCIZIO : "+lista.get(i).getId());
                    aggiunto= true;
                }
            }
        }
        return aggiunto;
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(CreateSchedaActivity.this, GestioneClientiActivity.class);
        startActivity(i);
        finish();
        return;
    }

    //metodo per abilitare lo swipe to delete e undo alla recyler view
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {//controllo l'evento dello swipe
                final int position = viewHolder.getAdapterPosition(); //prendo la posizione dell'elemento swippato
                final Esercizio item = ca.getData().get(position); //recupero l'elemento ricreando l'oggetto
                ca.removeItem(position);//rimuovo l'oggetto dalla lista
                Snackbar snackbar = Snackbar.make(con, getString(R.string.item_rem), Snackbar.LENGTH_LONG);

                snackbar.setAction("UNDO", new View.OnClickListener() {//ripristino l'oggetto eliminato se viene cliccato UNDO
                    @Override
                    public void onClick(View view) {
                        ca.restoreItem(item, position);
                        rv.scrollToPosition(position);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);//aggiungo il listener dello swipe e lo cvollego alla recycler view sotto
        itemTouchhelper.attachToRecyclerView(rv);
    }

    class inserisci_scheda extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            TwoParamsList params = new TwoParamsList();
            /// params.add("tipo", ""+1);
            params.add("idu",""+ idutente);
            params.add("id",""+ session.getUserDetails().getId());

            String ret=null;
            JSONObject json = new JSONParser().makeHttpRequest(url_inserisci_scheda, JSONParser.POST, params);
            System.out.println("entro in scheda");
            if (json != null) {
                try {
                    int success = json.getInt("success");
                    if (success == 0) {
                        Log.d("SCHEDA : ", "INSERISCO SCHEDA");
                        ret= "inserito";
                    } else if (success == -1) {
                    }
                } catch (JSONException e) {
                }
            }
            ////////////////////////////////////////////////////////////////////////////////////////////recupero l'id della scheda creata

            return ret;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                        new  get_id().execute();
                }
            });
        }
    }

    //recupero l'id della testata della scheda, per l'inserimento successivo alla tabella satellite
    class get_id extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String ret=null;
            ////////////////////////////////////////////////////////////////////////////////////////////recupero l'id della scheda creata
            TwoParamsList param = new TwoParamsList();
            /// params.add("tipo", ""+1);
            param.add("idu",""+ idutente);
            param.add("id",""+ session.getUserDetails().getId());
            JSONObject json2 = new JSONParser().makeHttpRequest(url_id_scheda, JSONParser.GET, param);
            System.out.println("");
            try {
                int success = json2.getInt(TAG_SUCCESS);
                if (success == 1) {
                    arr = json2.getJSONArray("scheda");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject c = arr.getJSONObject(i);
                        int id = Integer.parseInt(c.getString("id"));//id della scheda appena creata
                        idscheda=id;
                    }
                }else{
                    Log.d("SCHEDA : ", "ERRORE");
                }
            } catch (Exception e) {
                Log.d("SCHEDA : ", "ERRORE - "+  e.toString());
            }
            return ret;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    for(int i=0;i<scheda.size();i++){
                        idesercizio=scheda.get(i).getId(); //prendo l'id dell'esercizio e lo passo alla chiamata
                        conca=conca+idesercizio+";";
                    }
                    new inserisci_esercizi().execute();

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
            public ImageView cat;

            public MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);
                cat = (ImageView) view.findViewById(R.id.cat);


            }
        }


        public List<Esercizio> getData() {
            return listahome;
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

            Resources r = getResources();
            int drawableId = r.getIdentifier(c.getNomecategoria(), "drawable", "it.uniba.di.piu1920.healthapp");
            holder.cat.setImageDrawable(getDrawable(drawableId));
        }

        @Override
        public int getItemCount() {
            return listahome.size();
        }

        public void removeItem(int position) {
            listahome.remove(position);
            notifyItemRemoved(position);
        }

        public void restoreItem(Esercizio item, int position) {
            listahome.add(position, item);
            notifyItemInserted(position);
        }
    }

    //recupero del nome degli esercizi per lo spinner
    class GetEsercizi extends AsyncTask<String, String, String> {
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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_exercise_scheda, parent, false);
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

    //inserimento esercizi
    class inserisci_esercizi extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            TwoParamsList params = new TwoParamsList();
            params.add("idscheda",""+ idscheda);
            params.add("idesercizio",""+ conca);//passo una stringa concatenata dagli id dei vari esercizi selezionati dallo spinner, con UNA CHIAMATA, inserisco tutti i record
            String ret=null;
            JSONObject json = new JSONParser().makeHttpRequest(url_inserisci_esercizio, JSONParser.POST, params);
            System.out.println("INSERISCO ESERCIZIO "+conca);
            if (json != null) {
                try {
                    int success = json.getInt("success");
                    if (success == 0) {

                    } else if (success == -1) {
                    }
                } catch (JSONException e) {
                }
            }
            return ret;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), getString(R.string.ins_ok), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(CreateSchedaActivity.this, GestioneClientiActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        }
    }

    //modifica esercizi
    class modifica_esercizi extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            TwoParamsList params = new TwoParamsList();

            System.out.println("MODIFICA ESERCIZI : "+conca);
            params.add("idscheda",""+ idscheda);
            params.add("idesercizio",""+ conca); //passo una stringa concatenata dagli id dei vari esercizi selezionati dallo spinner, con UNA CHIAMATA, inserisco tutti i record
            String ret=null;
            JSONObject json = new JSONParser().makeHttpRequest(url_modifica_esercizio, JSONParser.POST, params);
            if (json != null) {
                try {
                    int success = json.getInt("success");
                    if (success == 0) {

                    } else if (success == -1) {
                    }
                } catch (JSONException e) {
                }
            }
            return ret;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    conca="";
                    for(int i=0;i<scheda.size();i++){
                        idesercizio=scheda.get(i).getId(); //prendo l'id dell'esercizio e lo passo alla chiamata
                        conca=conca+idesercizio+";";
                    }
                    System.out.println("MODIFICA ESERCIZI : "+conca);
                    new inserisci_esercizi().execute();

                }
            });
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
            new GetEsercizi().execute();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.err_connessione))

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(CreateSchedaActivity.this, GestioneClientiActivity.class);
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