package leopikinc.musiciansyandex;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MusicianAdapter extends ArrayAdapter<Musician> {
    public MusicianAdapter(Context context, ArrayList<Musician> musicians){
        super(context,0,musicians);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Musician musician = getItem(position);

        //Checks wheter it's new view or reused onem if new - create's it
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_item_layout,parent,false);
        }

        TextView musName = (TextView) convertView.findViewById(R.id.MusicianName);
        TextView musGenres = (TextView) convertView.findViewById(R.id.MusicianGenres);
        TextView musAlbums = (TextView) convertView.findViewById(R.id.MusicianAlbums);
        TextView musTracks = (TextView) convertView.findViewById(R.id.MusicianSongs);
        ImageView musSmallPhoto = (ImageView) convertView.findViewById(R.id.MusicianSmallPhoto);

        musName.setText(musician.getName());

        // Creates text from arraylist of genres
        if (musician.getGenres().size() != 0) {
            musGenres.setText(musician.getGenres().get(0));
            for (int i = 1; i < musician.getGenres().size(); i++) {
                musGenres.setText(musGenres.getText() + ", " + musician.getGenres().get(i));
            }
        } else
            musGenres.setText(R.string.no_genres);

        //Gets last digit of number and makes correct ending for word
        switch (musician.getAlbums()%10){
            case 1:
                if (musician.getAlbums() % 100 == 11)
                    musAlbums.setText(Integer.toString(musician.getAlbums()) + convertView.getResources().getString(R.string.album_count_ends_with_5));
                else
                    musAlbums.setText(Integer.toString(musician.getAlbums()) + convertView.getResources().getString(R.string.album_count_ends_with_1));
                break;
            case 2:
            case 3:
            case 4:
                if (musician.getAlbums() % 100 == 12 || musician.getAlbums() % 100 == 13 || musician.getAlbums() % 100 == 14)
                    musAlbums.setText(Integer.toString(musician.getAlbums()) + convertView.getResources().getString(R.string.album_count_ends_with_5));
                else
                    musAlbums.setText(Integer.toString(musician.getAlbums()) + convertView.getResources().getString(R.string.album_count_ends_with_2));
                break;
            default:
                musAlbums.setText(Integer.toString(musician.getAlbums()) + convertView.getResources().getString(R.string.album_count_ends_with_5));
                break;
        }

        //Gets last digit of number and makes correct ending for word
        switch (musician.getTracks() - musician.getTracks()%10){
            case 1:
                if (musician.getTracks() % 100 == 11)
                    musTracks.setText(Integer.toString(musician.getTracks()) + convertView.getResources().getString(R.string.song_count_ends_with_5));
                else
                    musTracks.setText(Integer.toString(musician.getTracks()) + convertView.getResources().getString(R.string.song_count_ends_with_1));
                break;
            case 2:
            case 3:
            case 4:
                if (musician.getTracks() % 100 == 12 || musician.getTracks() % 100 == 13 || musician.getTracks() % 100 == 14)
                    musTracks.setText(Integer.toString(musician.getTracks()) + convertView.getResources().getString(R.string.song_count_ends_with_5));
                else
                    musTracks.setText(Integer.toString(musician.getTracks()) + convertView.getResources().getString(R.string.song_count_ends_with_2));
                break;
            default:
                musTracks.setText(Integer.toString(musician.getTracks()) + convertView.getResources().getString(R.string.song_count_ends_with_5));
                break;
        }

        return convertView;
    }
}
