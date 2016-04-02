package leopikinc.musiciansyandex;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends ListActivity {

    ArrayList<Musician> arrayOfMusicians;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);
        context = this;
        new ProcessJSON().execute();
    }

    private class ProcessJSON extends AsyncTask<String, Void, String>{

        String link = "http://download.cdn.yandex.net/mobilization-2016/artists.json";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String json = "";

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(link);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                json = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String strJson){
            super.onPostExecute(strJson);
            try {
                JSONArray jsonArray = new JSONArray(json);
                arrayOfMusicians = Musician.fromJson(jsonArray);
                MusicianAdapter adapter = new MusicianAdapter(context, arrayOfMusicians);
                setListAdapter(adapter);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
