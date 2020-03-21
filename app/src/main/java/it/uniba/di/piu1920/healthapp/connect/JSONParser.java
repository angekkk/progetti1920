package it.uniba.di.piu1920.healthapp.connect;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import it.uniba.di.piu1920.healthapp.BuildConfig;


public class JSONParser {
    public static final String GET = "GET";
    public static final String POST = "POST";
    static InputStream is;
    static JSONObject jObj;
    static String json;
    static String output = "";

    public JSONParser() {
        is = null;
        jObj = null;
        json = BuildConfig.FLAVOR;
    }

    public JSONObject makeHttpRequest(String urlString, String method, TwoParamsList params) {
        // Making HTTP request
        try {
            if (method.equals(POST)) {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(urlString).openConnection();
                urlConnection.setDoOutput(true);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(params.toString());
                writer.flush();
                writer.close();
                is = urlConnection.getInputStream();
            } else if (method.equals(GET)) {
                is = ((HttpURLConnection) new URL(urlString + "?" + params.toString()).openConnection()).getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    sb.append(line).append("\n");
                } else {
                    is.close();
                    json = sb.toString();
                    try {
                        jObj = new JSONObject(json);
                        return jObj;
                    } catch (JSONException e2) {
                        return null;
                    }
                }
            }
        } catch (Exception e3) {
            return null;
        }
    }



}
