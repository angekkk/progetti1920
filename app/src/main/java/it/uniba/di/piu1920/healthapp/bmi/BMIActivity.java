package it.uniba.di.piu1920.healthapp.bmi;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DecimalFormat;

import it.uniba.di.piu1920.healthapp.Home;
import it.uniba.di.piu1920.healthapp.R;
import it.uniba.di.piu1920.healthapp.calorie.CalorieActivity;

public class BMIActivity extends AppCompatActivity {

    Button buttonCalculate, buttonExit;
    EditText inputKg, inputM;
    TextView showResult, showBMI, showImpBMI;
    private double kg, m;
    private DecimalFormat TWO_DECIMAL_PLACES = new DecimalFormat(".##");
    MetricFormula metricFormula;
    ImperialFormula imperialFormula;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonExit = findViewById(R.id.buttonExit);
        inputKg = findViewById(R.id.inputKg);
        inputM = findViewById(R.id.inputM);
        showResult = findViewById(R.id.showResult);
        showBMI = findViewById(R.id.showBMI);
        showImpBMI = findViewById(R.id.showImpBMI);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BMIActivity.this, Home.class);
                startActivity(i);
                finish();
            }
        });
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                kg = Double.parseDouble(inputKg.getText().toString());
                m = Double.parseDouble(inputM.getText().toString());

                metricFormula = new MetricFormula(kg, m);
                imperialFormula = new ImperialFormula(kg, m);

                showBMI.setText("BMI = " + String.valueOf(TWO_DECIMAL_PLACES.format(metricFormula.computeBMI(metricFormula.getInputKg(), metricFormula.getInputM()))));
          //      showImpBMI.setText("In imperial formula: " + String.valueOf(TWO_DECIMAL_PLACES.format(imperialFormula.computeBMI(imperialFormula.getInputKg(), imperialFormula.getInputM()))));
                showResult.setText(getCategory(metricFormula.computeBMI(metricFormula.getInputKg(), metricFormula.getInputM()))); //attivo il metodo per recuperare la categoria di appartenenza

            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void checkData(){

        if(inputKg.getText().toString().isEmpty()){

        }else{

        }





    }


    String getCategory(double result) {
        String category;
        if (result < 15) {
            category = getString(R.string.bmi_1);
        } else if (result >= 15 && result <= 16) {
            category = getString(R.string.bmi_2);
        } else if (result > 16 && result <= 18.5) {
            category = getString(R.string.bmi_3);
        } else if (result > 18.5 && result <= 25) {
            category = getString(R.string.bmi_4);
        } else if (result > 25 && result <= 30) {
            category = getString(R.string.bmi_5);
        } else if (result > 30 && result <= 35) {
            category = getString(R.string.bmi_6);
        } else if (result > 35 && result <= 40) {
            category = getString(R.string.bmi_7);
        } else {
            category = getString(R.string.bmi_8);
        }
        return category;
    }
}