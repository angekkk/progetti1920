package it.uniba.di.piu1920.healthapp.splash;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.google.firebase.messaging.FirebaseMessaging;

import it.uniba.di.piu1920.healthapp.Home;
import it.uniba.di.piu1920.healthapp.R;

//check del 22/06
public class AppIntroActivity extends AppIntro {


    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {
      startUpTasks();
    }

    private void startUpTasks() {
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        //  If the activity has never started before...
        if (isFirstRun) {
            FirebaseMessaging.getInstance().subscribeToTopic("ALIMENTAZIONE");
            FirebaseMessaging.getInstance().subscribeToTopic("ESERCIZI");
            FirebaseMessaging.getInstance().subscribeToTopic("ALTRO");
            //  Launch app intro
            // Instead of fragments, you can also use our default slide
            // Just set a title, description, background and image. AppIntro will do the rest.
            addSlide(AppIntroFragment.newInstance(getString(R.string.benvenuto), getString(R.string.benvenuto_intro), R.drawable.appintro5, getResources().getColor(R.color.appintro3)));
            addSlide(AppIntroFragment.newInstance(getString(R.string.t2), getString(R.string.intro2), R.drawable.appintro3, getResources().getColor(R.color.appintro3)));
            addSlide(AppIntroFragment.newInstance(getString(R.string.t1), getString(R.string.intro1), R.drawable.appintro1, getResources().getColor(R.color.appintro1)));
            addSlide(AppIntroFragment.newInstance("", getString(R.string.intro3), R.drawable.appintro4, getResources().getColor(R.color.appintro4)));

            // OPTIONAL METHODS
            // Override bar/separator color.
            setBarColor(Color.parseColor("#F44336"));
            setSeparatorColor(Color.parseColor("#2196F3"));

            // Hide Skip/Done button.
            showSkipButton(true);
            setProgressButtonEnabled(true);

            // Turn vibration on and set intensity.
            setVibrate(true);
            setVibrateIntensity(30);

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).commit();

        } else {

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).commit();
            Intent i = new Intent(AppIntroActivity.this, Home.class);
            startActivity(i);

        }
    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
        Intent i = new Intent(AppIntroActivity.this, Home.class);
        startActivity(i);
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        Intent i = new Intent(AppIntroActivity.this, Home.class);
        startActivity(i);
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {

    }

}