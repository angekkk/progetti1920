package it.uniba.di.piu1920.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import it.uniba.di.piu1920.healthapp.bmi.BMIActivity;
import it.uniba.di.piu1920.healthapp.calorie.CalorieActivity;
import it.uniba.di.piu1920.healthapp.classes.SessionManager;
import it.uniba.di.piu1920.healthapp.classes.Sessione;
import it.uniba.di.piu1920.healthapp.recycler.ExerciseActivity;
import it.uniba.di.piu1920.healthapp.recycler.NutritionActivity;
import me.ydcool.lib.qrmodule.activity.QrScannerActivity;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    DrawerLayout drawer;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
            navigationView.getMenu().getItem(8).setVisible(true);
            navigationView.getMenu().getItem(0).setVisible(false);
            navigationView.getMenu().getItem(3).setVisible(true);

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
                    // i.putExtra("lang",session.getUserDetails().getLang());
                    startActivity(i);
                    // close this activity
                    finish();

                }else if(id==R.id.nav_qr){

                    Intent intent = new Intent(Home.this, QrScannerActivity.class);
                    startActivityForResult(intent, QrScannerActivity.QR_REQUEST_CODE);

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
            Log.d("QR LETTO : ", resultCode == RESULT_OK
                    ? data.getExtras().getString(QrScannerActivity.QR_RESULT_STR)
                    : "Scanned Nothing!");
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
