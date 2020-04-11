package it.uniba.di.piu1920.healthapp.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.uniba.di.piu1920.healthapp.AlimentazioneActivity;
import it.uniba.di.piu1920.healthapp.R;
import it.uniba.di.piu1920.healthapp.SchedaActivity;
import it.uniba.di.piu1920.healthapp.bmi.BMIActivity;
import it.uniba.di.piu1920.healthapp.ExerciseActivity;
import it.uniba.di.piu1920.healthapp.calorie.FragBNutri;
import it.uniba.di.piu1920.healthapp.calorie.NutriActivity;
import it.uniba.di.piu1920.healthapp.classes.Esercizio;
import it.uniba.di.piu1920.healthapp.classes.Item;
import it.uniba.di.piu1920.healthapp.classes.SessionManager;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;
import it.uniba.di.piu1920.healthapp.recycler.RVAdapter;
import it.uniba.di.piu1920.healthapp.recycler.RecyclerItemListener;

public class HomeFragment extends Fragment {


    View root;
    private List<Item> listhome = new ArrayList<>();
    private static final String TAG_SUCCESS = "success"; //utilizzato a livello di tag per determinare se la chiamata ha prodotto risultati
    private static String url_get_id_scheda = "http://ddauniba.altervista.org/HealthApp/get_id.php"; //url per il recupero degli esercizi relativi alla scheda letta dal qr
    JSONArray arr = null;
    String idscheda="non"; //id scheda letto dall'intent del qr
    SessionManager session;
    RVAdapter ca;
    RecyclerView rv;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

/*
        //controllo se è stato già creato un tdee, in caso positivo visualizzo il fragment relativo al grafico
        if (this.getActivity().getSharedPreferences("Tdee", Context.MODE_PRIVATE) != null && !this.getActivity().getSharedPreferences("Tdee", Context.MODE_PRIVATE).getString("WEIGHT","").contentEquals("") && this.getActivity().getSharedPreferences("Tdee", Context.MODE_PRIVATE).getInt("TDEE",15)!=0) {
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Tdee", Context.MODE_PRIVATE);
            Bundle bundle = new Bundle();
            System.out.println("SHARED WEIGHT: "+Double.parseDouble(sharedPreferences.getString("WEIGHT","")));
            System.out.println("SHARED TDEE: "+sharedPreferences.getInt("TDEE",0));
            bundle.putDouble("WEIGHT", Double.parseDouble(sharedPreferences.getString("WEIGHT","")));
            bundle.putInt("TDEE", sharedPreferences.getInt("TDEE",0));
            FragBNutri simpleFragmentB = FragBNutri.newInstance();
            simpleFragmentB.setArguments(bundle);
            loadTDE(simpleFragmentB);
        }else{}
            */
            root = inflater.inflate(R.layout.fragment_home, container, false);
             rv = (RecyclerView) root.findViewById(R.id.rv);

            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(root.getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);


            session = new SessionManager(getContext()); //dichiaro l'oggetto per controllare la sessione di log
            if(session.checkLogin()){
                System.out.println("SESSION ON :" + session.getUserDetails().getId());
                new GetIdScheda().execute();
            }else{
                System.out.println("SESSION OFF :" + session.getUserDetails().getId());
                createList();
            }


            rv.addOnItemTouchListener(new RecyclerItemListener(root.getContext(), rv,
                    new RecyclerItemListener.RecyclerTouchListener() {
                        public void onClickItem(View v, int position) {
                            if(!idscheda.contentEquals("non")){
                                if (position == 0) {
                                    Intent intent = new Intent(root.getContext(), SchedaActivity.class);
                                    intent.putExtra("idscheda",idscheda);
                                    startActivity(intent);
                                } else if (position == 2) {
                                    Intent intent = new Intent(root.getContext(), AlimentazioneActivity.class);
                                    startActivity(intent);
                                }else if (position == 1) {
                                    Intent intent = new Intent(root.getContext(), ExerciseActivity.class);
                                    startActivity(intent);
                                } else if (position == 3) {
                                    Intent intent = new Intent(root.getContext(), NutriActivity.class);
                                    startActivity(intent);
                                }  else if (position == 4) {
                                    Intent intent = new Intent(root.getContext(), BMIActivity.class);
                                    startActivity(intent);
                                }
                            }else{
                                if (position == 0) {
                                    Intent intent = new Intent(root.getContext(), ExerciseActivity.class);
                                    startActivity(intent);
                                } else if (position == 1) {
                                    Intent intent = new Intent(root.getContext(), AlimentazioneActivity.class);
                                    startActivity(intent);
                                } else if (position == 2) {
                                    Intent intent = new Intent(root.getContext(), NutriActivity.class);
                                    startActivity(intent);
                                } else if (position == 3) {
                                    Intent intent = new Intent(root.getContext(), BMIActivity.class);
                                    startActivity(intent);
                                }
                            }

                        }

                        public void onLongClickItem(View v, int position) {
                            //System.out.println("On Long Click Item interface");
                        }
                    }));






        return root;
    }


    //crea la lista della homepage
    private void createList() {
        listhome.add(new Item(getString(R.string.title_es), root.getContext().getDrawable(R.drawable.esercizi)));
        listhome.add(new Item(getString(R.string.title_ali), root.getContext().getDrawable(R.drawable.alimentazione)));
        listhome.add(new Item(getString(R.string.title_calorie), root.getContext().getDrawable(R.drawable.calorie)));
        listhome.add(new Item(getString(R.string.title_bmi), root.getContext().getDrawable(R.drawable.bmi)));
        ca = new RVAdapter(listhome);
        rv.setAdapter(ca);
    }

    //metodo per richiamare nuovo fragment
    public void loadTDE(Fragment fragment) {

        FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.remove(new HomeFragment());
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.commit();
    }

    class GetIdScheda extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        List<String> categories = new ArrayList<>();
        protected String doInBackground(String... args) {
            String ris = null;

            TwoParamsList params = new TwoParamsList();
            params.add("idu",""+session.getUserDetails().getId());
            JSONObject json = new JSONParser().makeHttpRequest(url_get_id_scheda, JSONParser.GET, params);
             Log.d("Esercizi: ", json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    arr = json.getJSONArray("scheda");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject c = arr.getJSONObject(i);
                        idscheda = c.getString("id");
                    }
                } else {
                    Log.d("Esercizi: ", "SUCCESS 0");
                    idscheda="non";
                }
            } catch (Exception e) {
                Log.d("Esercizi: ", "ECCEZZIONE");
                e.printStackTrace();
            }



            return ris;
        }
        protected void onPostExecute(final String file_url) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    System.out.println("ESERCIZI SCHEDA: " + idscheda);
                    if(!idscheda.contentEquals("non")){
                        listhome.add(new Item(getString(R.string.title_scheda), root.getContext().getDrawable(R.drawable.img_scheda)));
                        createList();
                    }else{
                        createList();
                    }

                }
            });

        }

    }

}
