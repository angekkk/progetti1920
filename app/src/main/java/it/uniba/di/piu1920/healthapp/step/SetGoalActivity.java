package it.uniba.di.piu1920.healthapp.step;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

import it.uniba.di.piu1920.healthapp.DetailsActivity;
import it.uniba.di.piu1920.healthapp.ExerciseActivity;
import it.uniba.di.piu1920.healthapp.Home;
import it.uniba.di.piu1920.healthapp.R;


public class SetGoalActivity extends AppCompatActivity {
    public static float mSeries = 0f;
    public static float mSeries1 = 0f;
    SharedPreferences sharedpreferences;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setgoal);


        final EditText stepGoal = findViewById(R.id.et1);
        final EditText calorieGoal = findViewById(R.id.et2);
        sharedpreferences = getSharedPreferences("GOAL", Context.MODE_PRIVATE);
        Button setgoal = findViewById(R.id.setgoal);
        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        System.out.println("GOAL ORA: "+hour24hrs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(this.getClass().getSimpleName());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SetGoalActivity.this, Home.class);
                startActivity(i);
                finish();
            }
        });
        if(hour24hrs==23){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
        }else{
            if(!sharedpreferences.getString("goal","").isEmpty()) {
                Intent myIntent = new Intent(SetGoalActivity.this, StepsActivity.class);
                myIntent.putExtra("goal",sharedpreferences.getString("goal",""));
                startActivity(myIntent);
            }
        }


        setgoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stepGoal.getText().toString().length() == 0) {
                    stepGoal.setError("Set Steps Goal");
                    return;
                } else if (calorieGoal.getText().toString().length() == 0) {
                    calorieGoal.setError("Set Calorie Goal!");
                    return;
                }
                sharedpreferences = getSharedPreferences("GOAL", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("goal", stepGoal.getText().toString());
                System.out.println("GOAL SCRITTO: "+sharedpreferences.getString("goal",""));
                editor.commit();
                Intent myIntent = new Intent(SetGoalActivity.this, StepsActivity.class);
                myIntent.putExtra("goal",stepGoal.getText().toString());
                startActivity(myIntent);
            }
        });


    }
}
