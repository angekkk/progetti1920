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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import it.uniba.di.piu1920.healthapp.classes.Cliente;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;
import it.uniba.di.piu1920.healthapp.recycler.ExerciseActivity;
import it.uniba.di.piu1920.healthapp.recycler.Item;
import it.uniba.di.piu1920.healthapp.recycler.RecyclerItemListener;
import me.ydcool.lib.qrmodule.encoding.QrGenerator;

public class GestioneClientiActivity extends AppCompatActivity {


    private static final String TAG_SUCCESS = "success";
    JSONArray arr = null;
    List<Cliente> lista=new ArrayList<>();
    RecyclerView rv;
    private static String url_get_clienti = "http://ddauniba.altervista.org/HealthApp/get_clienti.php"; //preleva i clienti a cui non è stata assegnata nessuna scheda
    private static String url_get_clienti_schedati = "http://ddauniba.altervista.org/HealthApp/get_clienti_schedati.php"; //preleva i clienti a cui non è stata assegnata una scheda
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
                        if(!lista.get(position).getEmail().contentEquals("no")){
                            Intent i = new Intent(GestioneClientiActivity.this, CreateSchedaActivity.class);
                            i.putExtra("id",lista.get(position).getId());
                            i.putExtra("idscheda",lista.get(position).getEmail());
                            startActivity(i);

                        }else{
                            Intent i = new Intent(GestioneClientiActivity.this, CreateSchedaActivity.class);
                            i.putExtra("id",lista.get(position).getId());
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
                            Cliente x=new Cliente(id, nome,  cognome,  "no");
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
                JSONObject json = new JSONParser().makeHttpRequest(url_get_clienti_schedati, JSONParser.GET, params);
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
                            String scheda=c.getString("idscheda");//memorizzo l'id della scheda
                            Cliente x=new Cliente(id, nome,  cognome,  scheda);//assumerà la posizione dell'email nel costruttore, utilizzeremo getEmail per il recupero
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
                qr=view.findViewById(R.id.qr);
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
            holder.nomec.setText(c.getNome());
            holder.cognomec.setText(c.getCognome());
            if(!c.getEmail().contentEquals("no")){
                holder.qr.setVisibility(View.VISIBLE);

                try {
                    qrCode = new QrGenerator.Builder()
                            .content("ID: "+c.getEmail())
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

                holder.qr.setImageBitmap(qrCode);
                holder.qr.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(GestioneClientiActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.dialog, null);

                        ImageView iv = (ImageView) view.findViewById(R.id.iv);
                        iv.setImageBitmap(qrCode);

                        builder.setView(view);

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });

                        builder.show();
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