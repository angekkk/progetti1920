package it.uniba.di.piu1920.healthapp.calorie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import it.uniba.di.piu1920.healthapp.Home;
import it.uniba.di.piu1920.healthapp.R;

public class CalorieActivity extends AppCompatActivity  {


    int position;
    TextView result;
    FCFormula x = new FCFormula();
     Button next;
     EditText nameET,ageET,weightET,heightET;
     RadioGroup myRadioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kcal);
        next=  findViewById(R.id.calcola);
        nameET= findViewById(R.id.nameInput);

         ageET = findViewById(R.id.ageInput);
         weightET = findViewById(R.id.weightInput);
         heightET = findViewById(R.id.heightInput);
        result = findViewById(R.id.result);
        Toolbar toolbar =  findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CalorieActivity.this, Home.class);
                startActivity(i);
                finish();
            }
        });

       myRadioGroup =  findViewById(R.id.genderGroup);
        myRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                position = myRadioGroup.indexOfChild(findViewById(checkedId));
                if (position == 0) {
                    Log.d("Gender is ", "Male");

                } else {
                    Log.d("Gender is ", "Female");

                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkDati()) {
                    result.setText("Kcal " + x.getFC(position, Integer.parseInt(ageET.getText().toString()), Integer.parseInt(heightET.getText().toString()), Integer.parseInt(weightET.getText().toString())));
                }
            }
        });

    }

    boolean checkDati(){
        boolean check=true;
        if(nameET.getText().toString().length() == 0){
            nameET.setError(getString(R.string.richiesto));
            check=false;
        }
        if(ageET.getText().toString().length()==0 ){
            ageET.setError(getString(R.string.richiesto));
            check=false;
        }
        if(heightET.getText().toString().length()==0){
            heightET.setError(getString(R.string.richiesto));
            check=false;
        }
        if(weightET.getText().toString().length()==0){
            weightET.setError(getString(R.string.richiesto));
            check=false;
        }

        return check;
    }

}
