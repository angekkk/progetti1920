package it.uniba.di.piu1920.healthapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import it.uniba.di.piu1920.healthapp.R;
import it.uniba.di.piu1920.healthapp.bmi.BMIActivity;
import it.uniba.di.piu1920.healthapp.calorie.CalorieActivity;
import it.uniba.di.piu1920.healthapp.ExerciseActivity;
import it.uniba.di.piu1920.healthapp.calorie.NutriActivity;
import it.uniba.di.piu1920.healthapp.classes.Item;
import it.uniba.di.piu1920.healthapp.NutritionActivity;
import it.uniba.di.piu1920.healthapp.recycler.RVAdapter;
import it.uniba.di.piu1920.healthapp.recycler.RecyclerItemListener;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    View root;
    private List<Item> listhome = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(root.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        createList();
        System.out.println("SIZE LIST :" + listhome.size());
        RVAdapter ca = new RVAdapter(listhome);
        rv.setAdapter(ca);

        rv.addOnItemTouchListener(new RecyclerItemListener(root.getContext(), rv,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {
                        if (position == 0) {
                            Intent intent = new Intent(root.getContext(), ExerciseActivity.class);
                            startActivity(intent);
                        } else if (position == 1) {
                            Intent intent = new Intent(root.getContext(), NutriActivity.class);
                            startActivity(intent);
                        } else if (position == 2) {
                            Intent intent = new Intent(root.getContext(), BMIActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(root.getContext(), CalorieActivity.class);
                            startActivity(intent);
                        }
                    }

                    public void onLongClickItem(View v, int position) {
                        //System.out.println("On Long Click Item interface");
                    }
                }));


        return root;
    }


    private void createList() {
        listhome.add(new Item(getString(R.string.title_es), root.getContext().getDrawable(R.drawable.esercizi)));
        listhome.add(new Item(getString(R.string.title_ali), root.getContext().getDrawable(R.drawable.alimentazione)));
        listhome.add(new Item(getString(R.string.title_bmi), root.getContext().getDrawable(R.drawable.bmi)));
        listhome.add(new Item(getString(R.string.title_calorie), root.getContext().getDrawable(R.drawable.calorie)));
    }
}
