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

public class BMIActivity extends AppCompatActivity {

    Button buttonCalculate;
    EditText inputKg, inputM;
    TextView showResult, showBMI;
    private double kg, m;
    private DecimalFormat TWO_DECIMAL_PLACES = new DecimalFormat(".##");
    MetricFormula metricFormula;
    ImperialFormula imperialFormula;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmi);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        inputKg = findViewById(R.id.inputKg);
        inputM = findViewById(R.id.inputM);
        showResult = findViewById(R.id.showResult);
        showBMI = findViewById(R.id.showBMI);
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
                if(checkData()){
                    kg = Double.parseDouble(inputKg.getText().toString());
                    m = Double.parseDouble(inputM.getText().toString());
                    metricFormula = new MetricFormula(kg, m);
                    imperialFormula = new ImperialFormula(kg, m);
                    showBMI.setText("BMI = " + String.valueOf(TWO_DECIMAL_PLACES.format(metricFormula.computeBMI(metricFormula.getInputKg(), metricFormula.getInputM()))));
                    showResult.setText(getCategory(metricFormula.computeBMI(metricFormula.getInputKg(), metricFormula.getInputM()))); //attivo il metodo per recuperare la categoria di appartenenza
                }
            }
        });

    }

    boolean checkData(){

        if(inputKg.getText().toString().isEmpty() && inputM.getText().toString().isEmpty() ){
                return false;
        }else{
                return true;
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