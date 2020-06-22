package it.uniba.di.piu1920.healthapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.piu1920.healthapp.bt.BtActivity;
import it.uniba.di.piu1920.healthapp.classes.Cliente;
import it.uniba.di.piu1920.healthapp.classes.SessionManager;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;
import it.uniba.di.piu1920.healthapp.recycler.RecyclerItemListener;
import me.ydcool.lib.qrmodule.encoding.QrGenerator;

//check del 22/06
public class GestioneClientiActivity extends AppCompatActivity {


    private static final String TAG_SUCCESS = "success";
    JSONArray arr = null;
    List<Cliente> lista=new ArrayList<>();
    RecyclerView rv;
    private static String url_get_clienti = "http://ddauniba.altervista.org/HealthApp/get_clienti.php"; //preleva i clienti a cui non è stata assegnata nessuna scheda
    private static String url_get_clienti_schedati = "http://ddauniba.altervista.org/HealthApp/get_clienti_schedati.php"; //preleva i clienti a cui non è stata assegnata una scheda
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_exercices);
        rv= (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        sessionManager=new SessionManager(this);
        inizializza();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_clienti));
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
                        if(!lista.get(position).getQr().contentEquals("no")){//se è stata memorizzata la scheda di un cliente, entro nell'activity in modalità MODIFICA
                            Intent i = new Intent(GestioneClientiActivity.this, CreateSchedaActivity.class);
                            i.putExtra("id",lista.get(position).getId());
                            i.putExtra("idscheda",lista.get(position).getQr());
                            Log.v("GESTIONECLIENTI : ", "MODALITA' MODIFCA : "+lista.get(position).getQr());
                            startActivity(i);
                        }else{ //entro nell'activity in modalità CREAZIONE
                            Intent i = new Intent(GestioneClientiActivity.this, CreateSchedaActivity.class);
                            i.putExtra("id",lista.get(position).getId());
                            i.putExtra("idscheda","no");
                            Log.v("GESTIONECLIENTI : ", "MODALITA' INSERIMENTO : no");
                            startActivity(i);
                        }
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(GestioneClientiActivity.this, Home.class);
        startActivity(i);
        finish();
        return;
    }

    //metodo per lanciare il thread asincrono per la chiamata al php
    void inizializza(){
        if(isWorkingInternetPersent()){
            new GetClienti().execute();
        }else{

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.err_connessione))

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(GestioneClientiActivity.this, Home.class);
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

    //clienti non schedati
    class GetClienti extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            String ris=null;
            if(isWorkingInternetPersent()){
                TwoParamsList params = new TwoParamsList();
                params.add("id",""+sessionManager.getUserDetails().getId());
                JSONObject json = new JSONParser().makeHttpRequest(url_get_clienti, JSONParser.GET, params);
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
                            Cliente x=new Cliente(id, nome,  cognome, email ,"no");//al posto del qr, di default imposto la stringa 'no'
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
                    new GetClientiSchedati().execute();
                }
            });

        }


    }

    //clienti  schedati
    class GetClientiSchedati extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            String ris=null;
            if(isWorkingInternetPersent()){
                TwoParamsList params = new TwoParamsList();
                params.add("id",""+sessionManager.getUserDetails().getId());
                JSONObject json = new JSONParser().makeHttpRequest(url_get_clienti_schedati, JSONParser.GET, params);
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        arr = json.getJSONArray("clienti");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            int id = Integer.parseInt(c.getString("id"));
                            String nome=c.getString("nome");
                            String cognome=c.getString("cognome");
                            String scheda=c.getString("idscheda");//memorizzo l'id della scheda
                            Cliente x=new Cliente(id, nome,  cognome, "email" ,scheda);//secondo costruttore con la scheda
                            lista.add(x);
                            System.out.println("CLIENTI SCHEDATI : "+x.getQr());
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

    //adapter della recyclerview
    public class ExAdapt extends RecyclerView.Adapter<ExAdapt.MyViewHolder> {
        //adapeter per la recyclerview
        private List<Cliente> listahome;
        Bitmap qrCode = null;

        /**
         * View holder class
         * */
        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView nomec,cognomec;
            ImageView qr;

            public MyViewHolder(View view) {
                super(view);
                nomec =(TextView) view.findViewById(R.id.nomec);
                cognomec= (TextView) view.findViewById(R.id.cognomec);
                qr=view.findViewById(R.id.qr); //per default la sua visibilità è impostata su INVISIBLE, non è detto che un utente abbia già una scheda
            }
        }

        public ExAdapt(List<Cliente> listahomet) {
            this.listahome = listahomet;
        }

        @NonNull
        @Override
        public ExAdapt.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_cliente, parent, false);//collego il layout per la singola card della recyclerview
            return new ExAdapt.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ExAdapt.MyViewHolder holder, int position) {

            Cliente c = listahome.get(position);
            System.out.println("Bind ["+holder+"] - Pos ["+position+"]"+c.getNome());
            holder.nomec.setText(c.getNome());
            holder.cognomec.setText(c.getCognome());
            if(!c.getQr().contentEquals("no")){ //controllo che l'id della scheda sia stato inizializzato
                holder.qr.setVisibility(View.VISIBLE);

                try {
                    qrCode = new QrGenerator.Builder() //genero il qr impostando le varie caratteristiche
                            .content("ID: "+c.getQr())
                            .qrSize(300)
                            .margin(2)
                            .color(Color.BLACK)
                            .bgColor(Color.WHITE)
                            .ecc(ErrorCorrectionLevel.H)
                            .overlay(GestioneClientiActivity.this,R.mipmap.ic_launcher)
                            .overlaySize(100)
                            .overlayAlpha(255)
                            .overlayXfermode(PorterDuff.Mode.SRC_ATOP)
                            .encode();
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                holder.qr.setImageBitmap(qrCode); //setto il background dell'image view con il qr appena generato
                holder.qr.setOnLongClickListener(new View.OnLongClickListener() {//imposto un listener per la pressione prolungata in grado di mostrare in un dialog il qr
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(GestioneClientiActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.dialog, null);
                        ImageView iv = (ImageView) view.findViewById(R.id.iv);
                        Button esci = (Button) view.findViewById(R.id.esci);
                        Button invia = (Button) view.findViewById(R.id.invia);
                        iv.setImageBitmap(qrCode);
                        builder.setView(view);
                        final AlertDialog alertDialog = builder.show();
                        invia.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(GestioneClientiActivity.this, BtActivity.class);//dichiaro l'intent da richiamare con il contesto corrente
                                i.putExtra("qr", c.getQr());
                                startActivity(i);   //starto l'activity
                            }
                        });
                        esci.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });


                        return true;
                    }

                });


            }
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