package it.uniba.di.piu1920.healthapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import io.ghyeok.stickyswitch.widget.StickySwitch;
import it.uniba.di.piu1920.healthapp.classes.SessionManager;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;


public class SettingsActivity extends AppCompatActivity {


    private static String url_update = "http://ddauniba.altervista.org/HealthApp/update_profile.php"; //link al recupero degli esercizi della scheda
    int idutente; //id della scheda
    SessionManager sessionManager;
    Button email,password;
    String n_password="",n_email="";
    AlertDialog.Builder dialog;
    StickySwitch sticky;
    ImageView img;
    String KEY_LING="LINGUA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sessionManager=new SessionManager(this);
        idutente=sessionManager.getUserDetails().getId();
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        sticky=findViewById(R.id.sticky);
        img=findViewById(R.id.img);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_account));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, Home.class);
                startActivity(i);
                finish();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        SharedPreferences sharedPreferences = this.getSharedPreferences("Lingua", Context.MODE_APPEND);
        System.out.println("LINGUA COND 2: "+!sharedPreferences.getString("LING","").equals("") );
        System.out.println("LINGUA LETTA :  "+sharedPreferences.getString("LING","") );
        if (!sharedPreferences.getString("LING","").equals("") ) {
            System.out.println("LINGUA ENTRO E LEGGO"+getSharedPreferences("Lingua", Context.MODE_APPEND).getString("LING",""));

            if(sharedPreferences.getString("LING","").equals("it")){
                sticky.setDirection(StickySwitch.Direction.LEFT);
                System.out.println("LINGUA left: "+sharedPreferences.getString("LING",""));
            }else if(sharedPreferences.getString("LING","").equals("en")){
                sticky.setDirection(StickySwitch.Direction.RIGHT);
                System.out.println("LINGUA right: "+sharedPreferences.getString("LING",""));
            }
        }

        sticky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("Lingua", Context.MODE_APPEND).edit().clear().apply();
                if (sticky.getDirection().name().equals("LEFT")) {
                    System.out.println("LINGUA ENTRO STICY LEFT: ");
                    SharedPreferences preferences =getSharedPreferences("Lingua",Context.MODE_APPEND);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("LING", "it");
                    editor.apply();
                    System.out.println("LINGUA ENTRO STICY LEFT: "+preferences.getString("LING",""));
                    setAppLocale("it");

                }else{
                    System.out.println("LINGUA ENTRO STICY right: ");
                    SharedPreferences preferences =getSharedPreferences("Lingua",Context.MODE_APPEND);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("LING", "en");
                    editor.apply();
                    System.out.println("LINGUA : "+preferences.getString("LING",""));
                    setAppLocale("en");

                }
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 dialog = new AlertDialog.Builder(SettingsActivity.this);
                dialog.setTitle(getString(R.string.modp));


                LinearLayout layout = new LinearLayout(SettingsActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                // Add a TextView here for the "Title" label, as noted in the comments
                EditText ema = new EditText(SettingsActivity.this);
                ema.setHint(getString(R.string.email));
                ema.setHintTextColor(Color.BLACK);
                layout.addView(ema); // Notice this is an add method
                final Button mod = new Button(SettingsActivity.this);
                mod.setText(getString(R.string.modp));
                mod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            if(!ema.getText().toString().isEmpty()){
                                if(isWorkingInternetPersent()){
                                    n_email=ema.getText().toString();
                                    new update().execute();
                                }else{
                                    Snackbar.make(getCurrentFocus(), getString(R.string.err_connessione), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }else{
                                ema.setError(getString(R.string.err_em));
                            }

                    }
                });
                layout.addView(mod); // Another add method

                dialog.setView(layout); // Again this is a set method, not add
                dialog.create();
                dialog.show();
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(SettingsActivity.this);
                dialog.setTitle(getString(R.string.modp));


                LinearLayout layout = new LinearLayout(SettingsActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                // Add a TextView here for the "Title" label, as noted in the comments
                EditText passold = new EditText(SettingsActivity.this);
                passold.setHint(getString(R.string.pssold));
                passold.setHintTextColor(Color.BLACK);
                layout.addView(passold); // Notice this is an add method

                // Add another TextView here for the "Description" label
                EditText pass = new EditText(SettingsActivity.this);
                pass.setHint(getString(R.string.password));
                pass.setHintTextColor(Color.BLACK);
                layout.addView(pass); // Another add method


                EditText passconf = new EditText(SettingsActivity.this);
                passconf.setHint(getString(R.string.rewrite_pss));
                passconf.setHintTextColor(Color.BLACK);
                layout.addView(passconf); // Another add method

                final Button mod = new Button(SettingsActivity.this);
                mod.setText(getString(R.string.modp));
                mod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Password vecchia : "+sessionManager.getUserDetails().getPass());
                            if(passold.getText().toString().contentEquals(sessionManager.getUserDetails().getPass())){
                                    if(pass.getText().toString().contentEquals(passconf.getText().toString())){
                                            if(isWorkingInternetPersent()){
                                                    n_password=passconf.getText().toString();
                                                    new update().execute();

                                            }else{
                                                Snackbar.make(getCurrentFocus(), getString(R.string.err_connessione), Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                    }else{
                                        passconf.setError(getString(R.string.err_rpssw));
                                    }
                            }else{
                                  passold.setError(getString(R.string.err_rpssw2));
                            }
                    }
                });
                layout.addView(mod); // Another add method

                dialog.setView(layout); // Again this is a set method, not add
                dialog.create();
                dialog.show();
            }
        });

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
    public void onBackPressed() {
        Intent i = new Intent(SettingsActivity.this, Home.class);
        startActivity(i);
        finish();


    }

    //modifica esercizi
    class update extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            TwoParamsList params = new TwoParamsList();

            params.add("idu",""+ idutente);
            if(!n_email.contentEquals("")){
                params.add("email",n_email);
            }
            if(!n_password.contentEquals("")) {
                params.add("password",n_password);
            }
            String ret="";
            System.out.println("PARAMS : "+params.toString());
            JSONObject json = new JSONParser().makeHttpRequest(url_update, JSONParser.POST, params);
            if (json != null) {
                try {
                    int success = json.getInt("success");
                    if (success == 1) {
                        System.out.println("PARAMS : OK");
                        ret="ok";
                    }else{
                        System.out.println("PARAMS : NO");
                        ret="no";
                    }
                } catch (JSONException e) {
                    System.out.println("PARAMS : "+e.getMessage());
                }
            }else{
                System.out.println("PARAMS : json nullo");
            }
            return ret;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if(file_url.contentEquals("ok")){
                        sessionManager.clear();
                        if(!n_email.contentEquals("")){
                            sessionManager.createLoginSession(n_email,sessionManager.getUserDetails().getPass(),sessionManager.getUserDetails().getTipo(),idutente);
                        }
                        if(!n_password.contentEquals("")) {
                            sessionManager.createLoginSession(sessionManager.getUserDetails().getEma(),n_password,sessionManager.getUserDetails().getTipo(),idutente);
                        }
                        showDialog(getString(R.string.mod_ok));
                    }else if(file_url.contentEquals("no")){
                        showDialog(getString(R.string.upd_err));
                    }
                    n_email="";
                    n_password="";
                }
            });
        }
    }

    public void showDialog(String mx) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_account))
                .setMessage(mx)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogs, int which) {
                        dialogs.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(getDrawable(R.drawable.nav_account))
                .show();
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


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_LING, sticky.getDirection().name());

    }

}