package it.uniba.di.piu1920.healthapp;

import android.content.Intent;
import android.os.Bundle;

import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import it.uniba.di.piu1920.healthapp.classes.Esercizio;

public class DetailsActivity extends AppCompatActivity {

    Esercizio ex;
    TextView categoria,esecuzione;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        img=findViewById(R.id.img);
        categoria=findViewById(R.id.categoria);
        esecuzione=findViewById(R.id.esecuzione);
        Bundle  arg = getIntent().getExtras();
        Bundle x= arg.getBundle("bund");
        ex=(Esercizio) x.get("ogg");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(ex.getNome());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailsActivity.this, ExerciseActivity.class);
                startActivity(i);
                finish();
            }
        });
        esecuzione.setText(ex.getEsecuzione());
        categoria.setText(ex.getNomecategoria());
        Picasso.with(DetailsActivity.this).load("http://ddauniba.altervista.org/HealthApp/img/"+ex.getLink()).into(img);




    }


    @Override
    public void onBackPressed() {

            Intent i = new Intent(DetailsActivity.this, ExerciseActivity.class);
            startActivity(i);
            finish();


        return;
    }

}
