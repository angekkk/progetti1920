package it.uniba.di.piu1920.healthapp.connect;


import android.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;

import belka.us.androidtoggleswitch.BuildConfig;


//classe per preparare i parametri nella chiamata
//check del 22/06
public class TwoParamsList extends ArrayList<Pair<String, String>> {
    public void add(String key, String value) {
        add(new Pair(key, value));
    }

    public String toString() {
        String params = BuildConfig.FLAVOR;
        Iterator i$ = iterator();
        while (i$.hasNext()) {
            Pair<String, String> param = (Pair) i$.next();
            if (!params.isEmpty()) {
                params = params + "&";
            }
            params = params + ((String) param.first) + "=" + ((String) param.second);
        }
        return params;
    }

    public String get(String key) {
        Iterator i$ = iterator();
        while (i$.hasNext()) {
            Pair<String, String> param = (Pair) i$.next();
            if (((String) param.first).equals(key)) {
                return (String) param.second;
            }
        }
        return null;
    }
}
