package it.uniba.di.piu1920.healthapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import it.uniba.di.piu1920.healthapp.classes.Item;
import it.uniba.di.piu1920.healthapp.recycler.RVAdapter;
import it.uniba.di.piu1920.healthapp.recycler.RecyclerItemListener;

public class NutritionActivity extends AppCompatActivity {

    private List<Item> listhome= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_home);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        createList();
        System.out.println("SIZE LIST :"+listhome.size());
        RVAdapter ca = new RVAdapter(listhome);
        rv.setAdapter(ca);

        rv.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), rv,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {
                        System.out.println("On Click Item interface");
                    }

                    public void onLongClickItem(View v, int position) {
                        System.out.println("On Long Click Item interface");
                    }
                }));

        // Add decorator
        // rv.addItemDecoration(new VerticalSpacingDecoration(64));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

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

    private void createList() {
        //  listhome.add(new Item("Esercizi", getDrawable(R.drawable.esercizi)));



    }
}