package it.uniba.di.piu1920.healthapp.ui.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.piu1920.healthapp.AlimentazioneActivity;
import it.uniba.di.piu1920.healthapp.ExerciseActivity;
import it.uniba.di.piu1920.healthapp.R;
import it.uniba.di.piu1920.healthapp.SchedaActivity;
import it.uniba.di.piu1920.healthapp.bmi.BMIActivity;
import it.uniba.di.piu1920.healthapp.calorie.NutriActivity;
import it.uniba.di.piu1920.healthapp.classes.Item;
import it.uniba.di.piu1920.healthapp.classes.SessionManager;
import it.uniba.di.piu1920.healthapp.connect.JSONParser;
import it.uniba.di.piu1920.healthapp.connect.TwoParamsList;
import it.uniba.di.piu1920.healthapp.recycler.RVAdapter;
import it.uniba.di.piu1920.healthapp.recycler.RecyclerItemListener;

//check del 22/06
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

        root = inflater.inflate(R.layout.fragment_home, container, false);
        rv = (RecyclerView) root.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(root.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);


        session = new SessionManager(getContext()); //dichiaro l'oggetto per controllare la sessione di log
        if (session.checkLogin()) {
            System.out.println("SESSION ON :" + session.getUserDetails().getId());
            new GetIdScheda().execute();
        } else {
            System.out.println("SESSION OFF :" + session.getUserDetails().getId());
            createList();
        }


        rv.addOnItemTouchListener(new RecyclerItemListener(root.getContext(), rv,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {
                        if (!idscheda.contentEquals("non")) {
                            if (position == 0) {
                                Log.d("IDSCHEDA: ", "" + idscheda);
                                Intent intent = new Intent(root.getContext(), SchedaActivity.class);
                                intent.putExtra("idscheda", Integer.parseInt(idscheda));
                                startActivity(intent);
                            } else if (position == 2) {
                                Intent intent = new Intent(root.getContext(), AlimentazioneActivity.class);
                                startActivity(intent);
                            } else if (position == 1) {
                                Intent intent = new Intent(root.getContext(), ExerciseActivity.class);
                                startActivity(intent);
                            } else if (position == 3) {
                                Intent intent = new Intent(root.getContext(), NutriActivity.class);
                                startActivity(intent);
                            } else if (position == 4) {
                                Intent intent = new Intent(root.getContext(), BMIActivity.class);
                                startActivity(intent);
                            }
                        } else {
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
        listhome.add(new Item(getString(R.string.title_es), root.getContext().getDrawable(R.drawable.esercizi2)));
        listhome.add(new Item(getString(R.string.title_ali), root.getContext().getDrawable(R.drawable.alimentazione2)));
        listhome.add(new Item(getString(R.string.title_calorie), root.getContext().getDrawable(R.drawable.calorie3)));
        listhome.add(new Item(getString(R.string.title_bmi), root.getContext().getDrawable(R.drawable.bmi3)));
        ca = new RVAdapter(listhome);
        rv.setAdapter(ca);
    }


    class GetIdScheda extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

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
