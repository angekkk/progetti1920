package it.uniba.di.piu1920.healthapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
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
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import it.uniba.di.piu1920.healthapp.bmi.BMIActivity;
import it.uniba.di.piu1920.healthapp.calorie.CalorieActivity;
import it.uniba.di.piu1920.healthapp.classes.Esercizio;
import it.uniba.di.piu1920.healthapp.classes.SessionManager;
import it.uniba.di.piu1920.healthapp.classes.Sessione;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;
import it.uniba.di.piu1920.healthapp.recycler.ExerciseActivity;
import it.uniba.di.piu1920.healthapp.recycler.NutritionActivity;
import me.ydcool.lib.qrmodule.activity.QrScannerActivity;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    DrawerLayout drawer;
    SessionManager session;
    private static String url_get_esercizi = "http://ddauniba.altervista.org/HealthApp/get_esercizi_scheda.php"; //url per il recupero degli esercizi relativi alla scheda letta dal qr
    private static String url_get_id_scheda = "http://ddauniba.altervista.org/HealthApp/get_id.php"; //url per il recupero degli esercizi relativi alla scheda letta dal qr
    private static final String TAG_SUCCESS = "success";
    JSONArray arr = null;
    List<Esercizio> lista = new ArrayList<>();
    String idscheda=""; //id scheda letto dall'intent del qr

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         session = new SessionManager(getApplicationContext());
         drawer = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_exercise, R.id.nav_aliment,R.id.nav_bmi,R.id.nav_caloorie,R.id.nav_log,R.id.nav_out,R.id.nav_clienti,R.id.nav_scheda,R.id.nav_qr)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View hView =  navigationView.getHeaderView(0);
        TextView email= hView.findViewById(R.id.email);

        if (session.checkLogin()) {

            // get user data from session
            Sessione x = session.getUserDetails();
            email.setText(x.getNum());
            navigationView.getMenu().getItem(9).setVisible(true);
            navigationView.getMenu().getItem(0).setVisible(false);
            navigationView.getMenu().getItem(3).setVisible(true);
            navigationView.getMenu().getItem(4).setVisible(true);
            new GetIdScheda().execute();
            if(x.getTipo()==1){//controllo se l'utente connesso è un pt o un cliente
                //in caso affermativo visualizzo anche l'item relativo alla gestione dei clienti
                navigationView.getMenu().getItem(2).setVisible(true);
            }

        }
        navigationView.setCheckedItem(R.id.nav_home); // la home è sempre selezionata come item principale ad ogni apertura

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id==R.id.nav_home){
                    //Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                }else if(id==R.id.nav_exercise){
                    Intent intent = new Intent(Home.this, ExerciseActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id==R.id.nav_aliment){
                    Intent intent = new Intent(Home.this, NutritionActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id==R.id.nav_bmi){
                    Intent intent = new Intent(Home.this, BMIActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id==R.id.nav_caloorie){
                    Intent intent = new Intent(Home.this, CalorieActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id==R.id.nav_log){
                    Intent intent = new Intent(Home.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else if(id==R.id.nav_out){
                    //effettuare il log out, eliminare la SESSION
                    session.logoutUser();
                    Intent i = new Intent(Home.this, Home.class);
                    // i.putExtra("lang",session.getUserDetails().getLang());
                    startActivity(i);
                    // close this activity
                    finish();

                }else if(id==R.id.nav_clienti){
                    Intent i = new Intent(Home.this, GestioneClientiActivity.class);
                    startActivity(i);
                    finish();

                }else if(id==R.id.nav_qr){
                    final RxPermissions rxPermissions = new RxPermissions(Home.this);
                             rxPermissions.request(Manifest.permission.CAMERA) // richiedo il permesso per l'utilizzo della fotocameraper effettuare la scansione
                                     .subscribe(granted -> {
                                         if (granted) { // Always true pre-M
                                             Intent intent = new Intent(Home.this, QrScannerActivity.class);
                                             startActivityForResult(intent, QrScannerActivity.QR_REQUEST_CODE);
                                         } else {
                                             Snackbar.make(getCurrentFocus(), getString(R.string.cam_err), Snackbar.LENGTH_LONG)
                                                     .setAction("Action", null).show();
                                         }
                                     });


                }else if(id==R.id.nav_scheda){
                    if(!idscheda.contentEquals("non")){
                        Intent i = new Intent(Home.this, SchedaActivity.class);
                        i.putExtra("idscheda",idscheda);
                        startActivity(i);
                        finish();
                    }
                }
                //This is for maintaining the behavior of the Navigation view

                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QrScannerActivity.QR_REQUEST_CODE) {
            if(resultCode == RESULT_OK){

                idscheda=data.getExtras().getString(QrScannerActivity.QR_RESULT_STR);
                Toast.makeText(getApplicationContext(), idscheda.replace("ID: ",""), Toast.LENGTH_LONG).show();
                new GetEsercizi().execute();
            }

        }
    }



    class GetEsercizi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        List<String> categories = new ArrayList<>();
        protected String doInBackground(String... args) {
            String ris = null;

                TwoParamsList params = new TwoParamsList();
                params.add("idscheda",""+idscheda.replace("ID: ",""));
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
                public void run() {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.dialog, null);
                    String message="";
                    for(int i=0;i<categories.size();i++){
                        message=message.concat(categories.get(i).toUpperCase()+"\n");
                    }
                    builder.setMessage(message);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
