package leopikinc.musiciansyandex;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends ListActivity {

    ArrayList<Musician> arrayOfMusicians;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);
        context = this;
        String link = "http://download.cdn.yandex.net/mobilization-2016/artists.json";
//      String link = "https://vk.com/doc13725705_437399881";
        new ProcessJSON(link).execute();
    }

    // Class which asynchronously downloads JSONArray and as soon as it finishes is creates and sets ListAdapter
    private class ProcessJSON extends AsyncTask<Void, Void, String>{

        String url;

        public ProcessJSON(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                InputStream inputStream = new URL(url).openStream();
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                String json = builder.toString();
                return json;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String strJson){
            super.onPostExecute(strJson);
            try {
                JSONArray jsonArray = new JSONArray(strJson);
                arrayOfMusicians = Musician.fromJson(jsonArray);
                MusicianAdapter adapter = new MusicianAdapter(context, arrayOfMusicians);
                setListAdapter(adapter);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
