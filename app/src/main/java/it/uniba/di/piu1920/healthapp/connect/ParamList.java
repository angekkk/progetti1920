package it.uniba.di.piu1920.healthapp.connect;

import java.util.ArrayList;

//check del 22/06
public class ParamList extends ArrayList<KeyValue> {

    @Override
    public String toString() {
        String str = "";
        for (KeyValue x : this) {
            str = str + x.toString() + "&";
        }
        str = str.substring(0, str.length()-2);
        return str;
    }
}

class KeyValue {

    String key;
    String value;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + '=' + value;
    }
}