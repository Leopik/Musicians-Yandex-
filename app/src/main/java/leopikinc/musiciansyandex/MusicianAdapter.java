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

    private final ImageDownloader imageDownloader;

    Context context;

    // Creator
    public MusicianAdapter(Context context, ArrayList<Musician> musicians){
        super(context,0,musicians);
        imageDownloader = new ImageDownloader(getContext().getResources(), R.drawable.loading_image, context);
    }

    // ViewHolder for better performance
    static class ViewHolder {
        TextView musName;
        TextView musGenres;
        TextView musAlbums;
        TextView musTracks;
        ImageView musSmallPhoto;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //Checks whether it's new view or reused one if new - creates it
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_item_layout,parent,false);

            // Gets access to views
            holder = new ViewHolder();
            holder.musName = (TextView) convertView.findViewById(R.id.MusicianName);
            holder.musGenres = (TextView) convertView.findViewById(R.id.MusicianGenres);
            holder.musAlbums = (TextView) convertView.findViewById(R.id.MusicianAlbums);
            holder.musTracks = (TextView) convertView.findViewById(R.id.MusicianSongs);
            holder.musSmallPhoto = (ImageView) convertView.findViewById(R.id.MusicianSmallPhoto);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Musician musician = getItem(position);
        if (musician != null) {

            imageDownloader.loadBitmap(musician.getLinkToSmallPhoto(), holder.musSmallPhoto);

            holder.musName.setText(musician.getName());

            // Creates text from arraylist of genres
            if (musician.getGenres().size() != 0) {
                holder.musGenres.setText(musician.getGenres().get(0));
                for (int i = 1; i < musician.getGenres().size(); i++) {
                    holder.musGenres.setText(String.format(convertView.getResources().getString(R.string.concatenator_for_genres),
                            holder.musGenres.getText(),
                            musician.getGenres().get(i)));
                }
            } else
                holder.musGenres.setText(R.string.no_genres);

            // Makes correct ending for word
            String delimeter = ",\u00A0";
            holder.musAlbums.setText(convertView.getResources().getQuantityString(R.plurals.album_count, musician.getAlbums(), musician.getAlbums(), delimeter));
            holder.musTracks.setText(convertView.getResources().getQuantityString(R.plurals.song_count, musician.getTracks(), musician.getTracks()));
        }

        return convertView;
    }

}
