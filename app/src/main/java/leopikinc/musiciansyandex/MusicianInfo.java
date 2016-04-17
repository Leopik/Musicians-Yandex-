package leopikinc.musiciansyandex;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicianInfo extends AppCompatActivity {

    ImageDownloader imageDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_info);

        // Getting "musician" object
        Musician musician = getIntent().getParcelableExtra("Musician");

        // Getting toolbar and setting text to it
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_in_info);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(musician.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Getting access to views in layout
        TextView genres = (TextView) findViewById(R.id.genres);
        TextView albumcount = (TextView) findViewById(R.id.album_count);
        TextView trackcount = (TextView) findViewById(R.id.track_count);
        TextView biographytext = (TextView) findViewById(R.id.biography_text);
        ImageView bigphoto = (ImageView) findViewById(R.id.big_photo);

        // Makes picture take one half of screen
        ViewGroup.LayoutParams lp = bigphoto.getLayoutParams();
        lp.height = (int) (getResources().getDisplayMetrics().heightPixels*0.4f);
        bigphoto.setLayoutParams(lp);

        // Downloading photo
        imageDownloader = new ImageDownloader(getResources(), R.drawable.loading_image_info, this);
        imageDownloader.loadBitmap(musician.getLinkToBigPhoto(),bigphoto);

        // Creates text from arraylist of genres
        if (musician.getGenres().size() != 0) {
            genres.setText(musician.getGenres().get(0));
            for (int i = 1; i < musician.getGenres().size(); i++) {
                genres.setText(String.format(getResources().getString(R.string.concatenator_for_genres),
                        genres.getText(),
                        musician.getGenres().get(i)));
            }
        } else
            genres.setText(R.string.no_genres);

        // Makes correct ending for word
        String delimeter = "\u00A0  •  \u00A0";
        albumcount.setText(getResources().getQuantityString(R.plurals.album_count, musician.getAlbums(), musician.getAlbums(), delimeter));
        trackcount.setText(getResources().getQuantityString(R.plurals.song_count, musician.getTracks(), musician.getTracks()));

        biographytext.setText(musician.getDescription());
    }
}
