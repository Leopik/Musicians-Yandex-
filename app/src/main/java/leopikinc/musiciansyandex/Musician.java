
package leopikinc.musiciansyandex;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class Musician implements Parcelable {

    private int id;
    private String name;
    private ArrayList<String> genres = new ArrayList<>();
    private int tracks;
    private int albums;
    private String linkToSmallPhoto;
    private String linkToBigPhoto;
    private String description;

    // Getters
    public int getId(){
        return id;
    }
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
    public String getLinkToBigPhoto(){
        return linkToBigPhoto;
    }
    public String getDescription(){
        return description;
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

    // Creator
    public Musician(JSONObject object) {
        try {
            id = object.getInt("id");
            name = object.getString("name");
            tracks = object.getInt("tracks");
            albums = object.getInt("albums");
            linkToSmallPhoto = object.getJSONObject("cover").getString("small");
            linkToBigPhoto = object.getJSONObject("cover").getString("big");
            description = object.getString("description");

            // Makes arraylist of genres from JSON array
            JSONArray jsonGenres = object.getJSONArray("genres");
            for (int i = 0; i < jsonGenres.length(); i++) {
                genres.add(jsonGenres.getString(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Methods for Parcelable
    protected Musician(Parcel in) {
        id = in.readInt();
        name = in.readString();
        if (in.readByte() == 0x01) {
            genres = new ArrayList<>();
            in.readList(genres, String.class.getClassLoader());
        } else {
            genres = null;
        }
        tracks = in.readInt();
        albums = in.readInt();
        linkToSmallPhoto = in.readString();
        linkToBigPhoto = in.readString();
        description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        if (genres == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(genres);
        }
        dest.writeInt(tracks);
        dest.writeInt(albums);
        dest.writeString(linkToSmallPhoto);
        dest.writeString(linkToBigPhoto);
        dest.writeString(description);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Musician> CREATOR = new Parcelable.Creator<Musician>() {
        @Override
        public Musician createFromParcel(Parcel in) {
            return new Musician(in);
        }

        @Override
        public Musician[] newArray(int size) {
            return new Musician[size];
        }
    };
}