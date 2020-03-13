package it.uniba.di.piu1920.healthapp.login;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.se.omapi.Session;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniba.di.piu1920.healthapp.Home;
import it.uniba.di.piu1920.healthapp.R;
import it.uniba.di.piu1920.healthapp.classes.SessionManager;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private EditText emailTV, passwordTV;
    private Button loginBtn;
    private ProgressBar progressBar;
    SessionManager session;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;
    GoogleApiClient mGoogleApiClient;

    ImageView swipe;
    ImageButton scopri;

    private static final String TAG_SUCCESS = "success";
    JSONArray access = null;
    private static String url_accesso_utente = "http://ddauniba.altervista.org/HealthApp/get_accesso.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        mAuth = FirebaseAuth.getInstance();
        session = new SessionManager(getApplicationContext());
        initializeUI();
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        swipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
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

    private void loginUserAccount() {
        progressBar.setVisibility(View.VISIBLE);
        String email, password;

        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_em), Toast.LENGTH_LONG).show();
            return;
        }
        if (!isEmailValid(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_em2), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_pssw), Toast.LENGTH_LONG).show();
            return;
        }

        new ControllaAccesso().execute();
    }

    private void initializeUI() {
        emailTV = findViewById(R.id.et_email);
        passwordTV = findViewById(R.id.et_password);
        swipe=findViewById(R.id.swipe);
        scopri=findViewById(R.id.scopri);

        loginBtn = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);
    }

    static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                authWithGoogle(account);

                session.createLoginSession(account.getEmail(),"1",0,1);//tipo 2 google
            }
        }
    }

    private void authWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Auth Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void signIn() {
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signIntent,RC_SIGN_IN);
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
                    session.createLoginSession(c.getString("email"), c.getString("password"), Integer.parseInt(c.getString("tipo")), Integer.parseInt(c.getString("id")));
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
                        Toast.makeText(getApplicationContext(), getString(R.string.log_ok), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(LoginActivity.this, Home.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), getString(R.string.log_err), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
