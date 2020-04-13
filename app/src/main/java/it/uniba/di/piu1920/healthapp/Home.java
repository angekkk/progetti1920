package it.uniba.di.piu1920.healthapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.tbruyelle.rxpermissions2.RxPermissions;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.ghyeok.stickyswitch.widget.StickySwitch;
import it.uniba.di.piu1920.healthapp.bmi.BMIActivity;
import it.uniba.di.piu1920.healthapp.calorie.NutriActivity;
import it.uniba.di.piu1920.healthapp.classes.Esercizio;
import it.uniba.di.piu1920.healthapp.classes.SessionManager;
import it.uniba.di.piu1920.healthapp.classes.Sessione;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;
import it.uniba.di.piu1920.healthapp.login.LoginActivity;
import it.uniba.di.piu1920.healthapp.step.SetGoalActivity;
import me.ydcool.lib.qrmodule.activity.QrScannerActivity;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    DrawerLayout drawer;
    SessionManager session;

    private static String url_get_esercizi = "http://ddauniba.altervista.org/HealthApp/get_esercizi_scheda.php"; //url per il recupero degli esercizi relativi alla scheda letta dal qr
    private static String url_get_id_scheda = "http://ddauniba.altervista.org/HealthApp/get_id.php";
    private static String url_invia_richiesta = "http://ddauniba.altervista.org/HealthApp/inserisci_richiesta.php";
    private static String url_get_status = "http://ddauniba.altervista.org/HealthApp/get_status.php";

    private static final String TAG_SUCCESS = "success";
    JSONArray arr = null;
    List<Esercizio> lista = new ArrayList<>();
    String idscheda=""; //id scheda letto dall'intent del qr
    View hView;
    TextView email;
    ImageView foto;
    private static final String TAG = "Home";
    int lingua=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getSharedPreferences("Lingua", Context.MODE_APPEND) != null && !this.getSharedPreferences("Lingua", Context.MODE_APPEND).getString("LING","").contentEquals("") ) {
            SharedPreferences sharedPreferences = this.getSharedPreferences("Lingua", Context.MODE_APPEND);
            setAppLocale(sharedPreferences.getString("LING",""));
            System.out.println("LINGUA HOME: "+sharedPreferences.getString("LING",""));
        }else{
            SharedPreferences sharedPreferences = this.getSharedPreferences("Lingua", Context.MODE_APPEND);
            SharedPreferences.Editor mEditor = sharedPreferences.edit();
            mEditor.putString("LING", "it");
            lingua=0;
            mEditor.apply();
            System.out.println("LINGUA HOME NULL: "+sharedPreferences.getString("LING",""));
            setAppLocale("it");
        }
        setContentView(R.layout.activity_home);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( Home.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         session = new SessionManager(getApplicationContext()); //dichiaro l'oggetto per controllare la sessione di log
        drawer = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_exercise, R.id.nav_aliment,R.id.nav_bmi,R.id.nav_log,R.id.nav_out,R.id.nav_clienti,R.id.nav_scheda,R.id.nav_qr)
                .setDrawerLayout(drawer)
                .build(); //vengono passati e assemblati nel drawer layout gli item della Nav

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);//viene inizializzata la navigazione del NavigationDrawer

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);//linkato l'action bar con il controller della navigzaazione

        NavigationUI.setupWithNavController(navigationView, navController); //collegata la NavView con il controller

         hView =  navigationView.getHeaderView(0); //recuperiamo la view del NavigaionDrawer
         email= hView.findViewById(R.id.email);
         foto= hView.findViewById(R.id.foto);
        if (session.checkLogin()) { //controllo che la sessione sia attiva

            // get user data from session
            Sessione x = session.getUserDetails(); //recupero i dettagli dell'utente loggato, e svolgo le normali operazioni di recupero e settaggio del menù
            email.setText(x.getEma());

            navigationView.getMenu().getItem(11).setVisible(true);//settings account
            navigationView.getMenu().getItem(13).setVisible(true);//logout
            navigationView.getMenu().getItem(0).setVisible(false);//login
            navigationView.getMenu().getItem(8).setVisible(true); //scheda
            navigationView.getMenu().getItem(9).setVisible(true); //contapassi

            new GetIdScheda().execute(); //controllo e recupero in caso affermativo l'id della scheda relativo all'utente loggato
            new GetStatus().execute(); //controllo il tipo dell'utente se è cambiato o meno, in base a quello onPostExecute visualizzo gli item del menù corrispondenti

        }

        navigationView.setCheckedItem(R.id.nav_home); // la home è sempre selezionata come item principale ad ogni apertura

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {//listener per il menù del NavigationDrawer

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();

               if(id==R.id.nav_exercise){
                    Intent intent = new Intent(Home.this, ExerciseActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id==R.id.nav_aliment){
                   Intent intent = new Intent(Home.this, AlimentazioneActivity.class);
                   startActivity(intent);
                   finish();
                }else if(id==R.id.nav_cal){
                    Intent intent = new Intent(Home.this, NutriActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id==R.id.nav_bmi){
                    Intent intent = new Intent(Home.this, BMIActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id==R.id.nav_log){
                    Intent intent = new Intent(Home.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id==R.id.nav_out){
                    //effettuare il log out, eliminare la SESSION
                    session.logoutUser();
                   SharedPreferences sharedPreferences = getSharedPreferences("Tdee", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                   editor.putString("WEIGHT","");
                   editor.apply();
                    Intent i = new Intent(Home.this, Home.class);
                    startActivity(i);
                    finish();

                }else if(id==R.id.nav_clienti){
                    Intent i = new Intent(Home.this, GestioneClientiActivity.class);
                    startActivity(i);
                    finish();
                }else if(id==R.id.nav_qr){
                            getQRPermission();
                }else if(id==R.id.nav_scheda){
                    if(!idscheda.contentEquals("non")){//controllo che l'id della scheda esista o no
                        Intent i = new Intent(Home.this, SchedaActivity.class);
                        i.putExtra("idscheda",Integer.parseInt(idscheda));
                        startActivity(i);
                        finish();
                    }else{
                        Snackbar.make(getCurrentFocus(), getString(R.string.err_no_scheda), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }else if(id==R.id.nav_step){
                        Intent i = new Intent(Home.this, SetGoalActivity.class);
                        startActivity(i);
                        finish();
                }else if(id==R.id.nav_richiesta){
                   if(isWorkingInternetPersent()){
                       new invia_richiesta().execute();
                   }else{
                       Snackbar.make(getCurrentFocus(), getString(R.string.err_connessione), Snackbar.LENGTH_LONG)
                               .setAction("Action", null).show();
                   }
               }else if(id==R.id.nav_settings){
                   Intent i = new Intent(Home.this, SettingsActivity.class);
                   startActivity(i);
                   finish();
               }else if(id==R.id.nav_support){
                   Intent i = new Intent(Home.this, SupportActivity.class);
                   startActivity(i);
                   finish();
               }
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }


    private void setAppLocale(String localeCode){
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QrScannerActivity.QR_REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                idscheda=data.getExtras().getString(QrScannerActivity.QR_RESULT_STR);
/*
                if(isWorkingInternetPersent()){
                    new GetEsercizi().execute();
                }else{
                    Snackbar.make(getCurrentFocus(), getString(R.string.err_connessione), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }*/

                Intent i = new Intent(Home.this, SchedaActivity.class);
                i.putExtra("idscheda",Integer.parseInt(idscheda.replace("ID: ","")));
                startActivity(i);
                finish();
            }

        }
    }

    public void showDialog(String mx) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_richiesta))
                .setMessage(mx)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(getDrawable(R.drawable.nav_richiesta))
                .show();
    }

    public void getQRPermission(){

        final RxPermissions rxPermissions = new RxPermissions(Home.this); //dichiarazione e inizializzazione dell'oggetto rxPermissions
        rxPermissions.request(Manifest.permission.CAMERA) // richiedo il permesso per l'utilizzo della fotocameraper effettuare la scansione
                .subscribe(granted -> {
                    if (granted) { // Se concesso starto l'activity relativa alla lettura del Qrcode
                        Intent intent = new Intent(Home.this, QrScannerActivity.class);
                        startActivityForResult(intent, QrScannerActivity.QR_REQUEST_CODE);
                    } else {
                        Snackbar.make(getCurrentFocus(), getString(R.string.cam_err), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
    }

    //Chiamata ad una risorsa esterna , gestita in un TaskAsincrono
    class GetEsercizi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        List<String> categories = new ArrayList<>();
        protected String doInBackground(String... args) {
            String ris = null;

                TwoParamsList params = new TwoParamsList();
                params.add("idscheda",""+idscheda.replace("ID: ","")); //passo il parametro da inserire nella chiamata
                JSONObject json = new JSONParser().makeHttpRequest(url_get_esercizi, JSONParser.GET, params); //attraverso un JSONParser passo il link della chiamata, il tipo GET/POST e i parametri da mandare
                //  Log.d("Esercizi: ", json.toString());
                try {
                    int success = json.getInt(TAG_SUCCESS); //controllo se è stato prodotto un risultato dal php con il tag success
                    if (success == 1) {
                        arr = json.getJSONArray("esercizio"); //recupero in un array il risultato delle query del php,opportunamente compattate per il json
                        for (int i = 0; i < arr.length(); i++) {//itero sull'array
                            JSONObject c = arr.getJSONObject(i);
                            int id = Integer.parseInt(c.getString("id"));
                            int tipo = Integer.parseInt(c.getString("tipo"));
                            String nome = c.getString("nome");
                            String esecuzione = Html.fromHtml(c.getString("esecuzione")).toString();
                            String link = c.getString("link");
                            Esercizio x = new Esercizio(nome, "", link, esecuzione, id, tipo);
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



            return ris;
        }
        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() { //quando la chiamata è terminata, svolgo le mie normali operazioni
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.dialog, null);
                    String message="";
                    for(int i=0;i<categories.size();i++){
                        message=message.concat(categories.get(i).toUpperCase()+"\n");
                    }
                    builder.setMessage(idscheda.replace("ID: ",""));
                    builder.setView(view);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });

                    builder.show();

                }
            });

        }

    }

    class GetIdScheda extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        List<String> categories = new ArrayList<>();
        protected String doInBackground(String... args) {
            String ris = null;

            TwoParamsList params = new TwoParamsList();
            params.add("idu",""+session.getUserDetails().getId());
            JSONObject json = new JSONParser().makeHttpRequest(url_get_id_scheda, JSONParser.GET, params);
            //  Log.d("Esercizi: ", json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    arr = json.getJSONArray("scheda");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject c = arr.getJSONObject(i);
                        idscheda = c.getString("id");
                    }
                } else {
                    Log.d("Esercizi: ", "SUCCESS 0");
                    idscheda="non";
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
                }
            });

        }

    }

    class invia_richiesta extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            TwoParamsList params = new TwoParamsList();
            /// params.add("tipo", ""+1);
            params.add("idu",""+ session.getUserDetails().getId());
            String ret= "";
            JSONObject json = new JSONParser().makeHttpRequest(url_invia_richiesta, JSONParser.POST, params);

            if (json != null) {
                try {
                    int success = json.getInt("success");
                    if (success == 1) {
                        ret = "inviata";
                    } else if(success==0) {
                        ret = "errore";
                    }else if(success==2){
                        ret = "presente";
                    }
                } catch (JSONException e) {

                }
            }
            return ret;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                  if(file_url.contentEquals("inviata")){
                                showDialog(getString(R.string.richiesta_inv));
                  }else if(file_url.contentEquals("errore")){
                                showDialog(getString(R.string.richiesta_err));
                  }else if(file_url.contentEquals("presente")){
                                showDialog(getString(R.string.richiesta_trovata));
                  }
                }
            });
        }
    }

    class GetStatus extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        List<String> categories = new ArrayList<>();
        protected String doInBackground(String... args) {
            String ris = null;

            TwoParamsList params = new TwoParamsList();
            params.add("idu",""+session.getUserDetails().getId());
            JSONObject json = new JSONParser().makeHttpRequest(url_get_status, JSONParser.GET, params);
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    arr = json.getJSONArray("status");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject c = arr.getJSONObject(i);
                        System.out.println(" SESSION TIPO LETTO: "+Integer.parseInt(c.getString("tipo")));
                        if(session.getUserDetails().getTipo()!=Integer.parseInt(c.getString("tipo"))){
                            System.out.println(" SESSION TIPO PRIMA: "+session.getUserDetails().getTipo());
                            session.logoutUser();
                            session.createLoginSession(c.getString("email"),c.getString("password"),Integer.parseInt(c.getString("tipo")),Integer.parseInt(c.getString("id")));
                            System.out.println(" SESSION TIPO CAMBIATO: "+session.getUserDetails().getTipo());
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("STATUS : ", "");
                e.printStackTrace();
            }
            return ris;
        }
        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    System.out.println(" SESSION TIPO : "+session.getUserDetails().getTipo());
                    if(session.getUserDetails().getTipo()==1){//controllo se l'utente connesso è un pt o un cliente
                        //in caso affermativo visualizzo anche l'item relativo alla gestione dei clienti
                        navigationView.getMenu().getItem(7).setVisible(true);
                        foto.setImageDrawable(getDrawable(R.drawable.pt)); //setto l'immagine del pt nella navbar
                    }else{
                        navigationView.getMenu().getItem(10).setVisible(true);//richiesta pt
                        foto.setImageDrawable(getDrawable(R.drawable.user));
                    }
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.refresh:
                SessionManager session =new SessionManager(this);
                Sessione se=session.getUserDetails();
                this.getSharedPreferences("Tdee", Context.MODE_PRIVATE).edit().clear().apply();
                recreate();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
