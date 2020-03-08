package it.uniba.di.piu1920.healthapp.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniba.di.piu1920.healthapp.Home;
import it.uniba.di.piu1920.healthapp.R;
import it.uniba.di.piu1920.healthapp.classes.SessionManager;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;

public class RegistrationActivity extends AppCompatActivity {

    EditText emailTV, passwordTV,repassword,nome,cognome;
    Button regBtn;
    ProgressBar progressBar;
    ImageView swipe;
    FirebaseAuth mAuth;
    ImageButton scopri;
    SessionManager session; //variabile per creare la sessione
    private static final String TAG_SUCCESS = "success";
    JSONArray access = null;
    private static String url_accesso_utente = "http://ddauniba.altervista.org/HealthApp/get_accesso.php";
    private static String url_inserisci_utente = "http://ddauniba.altervista.org/HealthApp/inserisci_utente.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);

        mAuth = FirebaseAuth.getInstance();
        emailTV = findViewById(R.id.email);
        passwordTV =findViewById(R.id.et_password);
        repassword = findViewById(R.id.et_repassword);
        swipe=findViewById(R.id.swipe);
        scopri=findViewById(R.id.scopri);
        nome = findViewById(R.id.et_name);
        cognome = findViewById(R.id.et_cognome);
        regBtn = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
        session = new SessionManager(getApplicationContext()); //inzializzo la sessione

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        swipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        scopri.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        passwordTV.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordTV.setSelection(passwordTV.getText().length());
                        return true;
                    case MotionEvent.ACTION_UP:
                        passwordTV.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordTV.setSelection(passwordTV.getText().length());
                        return true;
                }
                return false;
            }
        });
    }


    static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password="",rep,nom,cog;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();
        rep=  repassword.getText().toString();
        nom=nome.getText().toString();
        cog=cognome.getText().toString();
        if (TextUtils.isEmpty(nom) ) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_nome), Toast.LENGTH_LONG).show();
            return;
        }
        if ( TextUtils.isEmpty(cog)) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_cog), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(email) ) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_em), Toast.LENGTH_LONG).show();
            return;
        }
        if (!isEmailValid(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_em2), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password) ) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_pssw), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(rep)) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_rpssw), Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.contains(rep)) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_rpssw2), Toast.LENGTH_LONG).show();
            return;
        }

                            new registra().execute();

    }

    class registra extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            TwoParamsList params = new TwoParamsList();
            /// params.add("tipo", ""+1);
            params.add("nome", nome.getText().toString().toLowerCase());
            params.add("cognome", cognome.getText().toString().toLowerCase());
            params.add("email", emailTV.getText().toString().toLowerCase());
            params.add("pssw", passwordTV.getText().toString().toLowerCase());
            String ret=null;
            JSONObject json = new JSONParser().makeHttpRequest(url_inserisci_utente, JSONParser.POST, params);
            if (json != null) {
                try {
                    int success = json.getInt("success");
                    if (success == 0) {
                        ret= "inserito";
                    } else if (success == -1) {
                    }
                } catch (JSONException e) {
                }
            } else {
            }
            return ret;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                  new ControllaAccesso().execute();
                }
            });
        }
    }

    class ControllaAccesso extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            // Building Parameters
            TwoParamsList params = new TwoParamsList();
            String ret = "not";
            params.add("email", emailTV.getText().toString().toLowerCase());
            params.add("password", passwordTV.getText().toString().toLowerCase());
            JSONObject json = new JSONParser().makeHttpRequest(url_accesso_utente, JSONParser.GET, params);

            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    access = json.getJSONArray("Accesso");
                    JSONObject c = access.getJSONObject(0);
                    session.createLoginSession(c.getString("email"), c.getString("password"),Integer.parseInt(c.getString("tipo")),Integer.parseInt(c.getString("id")));
                    ret = "ok";
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ret;
        }

        protected void onPostExecute(final String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (file_url.contentEquals("ok")) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getString(R.string.reg_ok), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegistrationActivity.this, Home.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), getString(R.string.reg_err), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }

    }
}
