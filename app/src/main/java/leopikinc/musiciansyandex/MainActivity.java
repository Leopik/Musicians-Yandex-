package leopikinc.musiciansyandex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String PREFERENCE_JSON_NAME= "cachedJSON";
    ArrayList<Musician> arrayOfMusicians;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);

        // Getting toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_in_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(R.string.toolbar_in_main_text);

        // Procees
        String link = "http://download.cdn.yandex.net/mobilization-2016/artists.json";
        ProcessJSON processJSON = new ProcessJSON(link);
        processJSON.execute();
    }

    // Checks access to the internet
//    public boolean isOnline() {
//        ConnectivityManager cm =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        return netInfo != null && netInfo.isConnectedOrConnecting();
//    }

    // Checks access to the internet
    // I use this method not the previous one because previous one checks for network connection but it doesn't check whether internet is available
    // e.g. I can have mobile internet running, but not paid and my operator will not let me use internet but I will still have connection
    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (Exception e) { e.printStackTrace(); }

        return false;
    }

    // Method which should be loaded only after JSON was loaded
    private void continueWork(String strJson) {
        try {
            // Makes JSONArray and converts it to array of musicians
            JSONArray jsonArray = new JSONArray(strJson);
            arrayOfMusicians = Musician.fromJson(jsonArray);

            // Sets adapter
            ListView listView = (ListView) findViewById(R.id.list);
            MusicianAdapter adapter = new MusicianAdapter(this, arrayOfMusicians);
            listView.setAdapter(adapter);

            // Sets ItemClickListener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, MusicianInfo.class);
                    intent.putExtra("Musician",arrayOfMusicians.get(position));
                    startActivity(intent);
                }
            });
        } catch (JSONException | NullPointerException e){

            // Sets "failed" message
            TextView textView = (TextView) findViewById(android.R.id.empty);
            textView.setText(R.string.loading_failed);
        }
    }

    // Class which asynchronously downloads JSONArray and as soon as it finishes is creates and sets ListAdapter
    private class ProcessJSON extends AsyncTask<Void, Void, String>{

        String url;

        // Creator
        public ProcessJSON(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences cachedJSON = getPreferences(MODE_PRIVATE);

            // If there is cached JSON - load it
            if (cachedJSON.getString(PREFERENCE_JSON_NAME, null) != null) {
                continueWork(cachedJSON.getString(PREFERENCE_JSON_NAME, null));
            }
        }

        // Converts JSON to string
        @Override
        protected String doInBackground(Void... params) {

            // If there is internet connection - update JSON
            if (isOnline()) {
                InputStream inputStream = null;
                try {
                    // Makes JSON string
                    inputStream = new URL(url).openStream();
                    StringBuilder builder = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    String json = builder.toString();

                    // Cache JSON
                    SharedPreferences cacheJSON = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = cacheJSON.edit();
                    editor.putString(PREFERENCE_JSON_NAME, json);
                    editor.apply();

                    return json;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null){
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String strJson){
            super.onPostExecute(strJson);

            // If JSON was updated - update listview
            if (isOnline())
                continueWork(strJson);
        }
    }
}
/**
    TODO: try recyclerview + collapsingToolbar + libraries
    TODO: handle with exception in threads?
    TODO: tests?
 */