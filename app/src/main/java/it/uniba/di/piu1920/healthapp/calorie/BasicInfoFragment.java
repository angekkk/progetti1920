package it.uniba.di.piu1920.healthapp.calorie;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.di.piu1920.healthapp.R;

public class BasicInfoFragment extends Fragment {

    int position;
    private FirebaseAuth mAuth;
    TextView result;
    FCFormula x = new FCFormula();


    public BasicInfoFragment() {
    }

    public static BasicInfoFragment newInstance() {
        BasicInfoFragment fragment = new BasicInfoFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_basicinfo, container, false);
        final OnFloatingButtonClickListener mListener;

        try {
            mListener = (OnFloatingButtonClickListener) getContext();
            Log.d("mContext is ", getContext().toString());
        } catch (ClassCastException ex) {
            throw new ClassCastException("The hosting activity of the fragment" +
                    "forgot to implement onFragmentInteractionListener");
        }

        final Button next = (Button) rootView.findViewById(R.id.calcola);
        final EditText nameET = (EditText) rootView.findViewById(R.id.nameInput);

        final EditText ageET = (EditText) rootView.findViewById(R.id.ageInput);
        final EditText weightET = (EditText) rootView.findViewById(R.id.weightInput);
        final EditText heightET = (EditText) rootView.findViewById(R.id.heightInput);
        result = rootView.findViewById(R.id.result);


        final RadioGroup myRadioGroup = (RadioGroup) rootView.findViewById(R.id.genderGroup);
        myRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                position = myRadioGroup.indexOfChild(rootView.findViewById(checkedId));
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
                if (nameET.getText().toString().length() == 0 || ageET.getText().toString().length()==0 || heightET.getText().toString().length()==0 || weightET.getText().toString().length()==0 ) {
                    nameET.setError("Name is required!");
                    return;
                } else {
                    result.setText("Kcal " + x.getFC(position, Integer.parseInt(ageET.getText().toString()), Integer.parseInt(heightET.getText().toString()), Integer.parseInt(weightET.getText().toString())));
                }
                mListener.onFloatingButtonClicked();

            }
        });

        return rootView;
    }

    public interface OnFloatingButtonClickListener {
        public void onFloatingButtonClicked();
    }


}

