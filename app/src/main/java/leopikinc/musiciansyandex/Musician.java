package leopikinc.musiciansyandex;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Musician {
    private int id;
    private String name;
    private ArrayList<String> genres = new ArrayList<>();
    private int tracks;
    private int albums;
    private String linkToSmallPhoto;
    private String linkToBigPhoto;
    private String description;

    // Getters
    public String getName() {
        return name;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public int getTracks() {
        return tracks;
    }

    public int getAlbums() {
        return albums;
    }

    public String getLinkToSmallPhoto() {
        return linkToSmallPhoto;
    }

    //Converts JSON array of musicians to ArrayList
    public static ArrayList<Musician> fromJson(JSONArray jsonArrayOfMusicians) {
        ArrayList<Musician> musicians = new ArrayList<>(jsonArrayOfMusicians.length());
        for (int i = 0; i < jsonArrayOfMusicians.length(); i++) {
            try {
                musicians.add(new Musician(jsonArrayOfMusicians.getJSONObject(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return musicians;
    }

    public Musician(JSONObject object) {
        try {
            name = object.getString("name");
            tracks = object.getInt("tracks");
            albums = object.getInt("albums");
            linkToSmallPhoto = object.getJSONObject("cover").getString("small");

            // Makes arraylist of genres from JSON array
            JSONArray jsonGenres = object.getJSONArray("genres");
            for (int i = 0; i < jsonGenres.length(); i++) {
                    genres.add(jsonGenres.getString(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
