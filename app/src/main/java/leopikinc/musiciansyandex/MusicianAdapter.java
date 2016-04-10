package leopikinc.musiciansyandex;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MusicianAdapter extends ArrayAdapter<Musician> {

    private final ImageDownloader imageDownloader = new ImageDownloader(getContext().getResources());

    // Creator
    public MusicianAdapter(Context context, ArrayList<Musician> musicians){
        super(context,0,musicians);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Musician musician = getItem(position);

        //Checks whether it's new view or reused one if new - creates it
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_item_layout,parent,false);
        }

        // Gets access to views
        TextView musName = (TextView) convertView.findViewById(R.id.MusicianName);
        TextView musGenres = (TextView) convertView.findViewById(R.id.MusicianGenres);
        TextView musAlbums = (TextView) convertView.findViewById(R.id.MusicianAlbums);
        TextView musTracks = (TextView) convertView.findViewById(R.id.MusicianSongs);
        ImageView musSmallPhoto = (ImageView) convertView.findViewById(R.id.MusicianSmallPhoto);

        imageDownloader.loadBitmap(musician.getLinkToSmallPhoto(), musSmallPhoto);
        musName.setText(musician.getName());

        // Creates text from arraylist of genres
        if (musician.getGenres().size() != 0) {
            musGenres.setText(musician.getGenres().get(0));
            for (int i = 1; i < musician.getGenres().size(); i++) {
                musGenres.setText(String.format(convertView.getResources().getString(R.string.concatenator_for_genres),
                        musGenres.getText(),
                        musician.getGenres().get(i)));
            }
        } else
            musGenres.setText(R.string.no_genres);

        // Makes correct ending for word
        musAlbums.setText(convertView.getResources().getQuantityString(R.plurals.album_count, musician.getAlbums(), musician.getAlbums()));
        musTracks.setText(convertView.getResources().getQuantityString(R.plurals.song_count, musician.getTracks(), musician.getTracks()));

        return convertView;
    }

}
