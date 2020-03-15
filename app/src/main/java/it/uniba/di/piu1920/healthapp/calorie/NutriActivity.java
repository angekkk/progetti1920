package it.uniba.di.piu1920.healthapp.calorie;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import it.uniba.di.piu1920.healthapp.DetailsActivity;
import it.uniba.di.piu1920.healthapp.ExerciseActivity;
import it.uniba.di.piu1920.healthapp.Home;
import it.uniba.di.piu1920.healthapp.R;

import static android.content.Context.MODE_PRIVATE;


public class NutriActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutri);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NutriActivity.this, Home.class);
                startActivity(i);
                finish();
            }
        });
        if (getData() == 0 && savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, FragANutri.newInstance())
                    .commit();
        } else if (getData() != 0) {
            Bundle bundle = new Bundle();
            bundle.putDouble("WEIGHT", getData2());
            bundle.putInt("TDEE", getData());
            bundle.putInt("GOAL", getData3());
            FragBNutri simpleFragmentB = FragBNutri.newInstance();
            simpleFragmentB.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, simpleFragmentB)
                    .commit();
        }
    }

    private int getData() {
        SharedPreferences sharedPreferences = getSharedPreferences("Tdee", MODE_PRIVATE);
        return sharedPreferences.getInt("TDEE", 0);
    }

    private double getData2() {
        SharedPreferences sharedPreferences = getSharedPreferences("Tdee", MODE_PRIVATE);
        return Double.valueOf(sharedPreferences.getString("WEIGHT", "0"));
    }

    private int getData3() {
        SharedPreferences sharedPreferences = getSharedPreferences("Tdee", MODE_PRIVATE);
        return sharedPreferences.getInt("GOAL", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reset, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                erase();
        }
        return super.onOptionsItemSelected(item);
    }

    private void erase() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("Tdee", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();
        mEditor.putInt("TDEE", 0);
        mEditor.putString("WEIGHT", "0");
        mEditor.putInt("GOAL", 0);
        mEditor.apply();
        finish();
        startActivity(getIntent());
    }
}