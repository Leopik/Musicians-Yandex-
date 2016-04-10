package leopikinc.musiciansyandex;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

// Downloads bitmpas, cancels not necessary tasks (cares for all downloading work)
public class ImageDownloader {

    // Loading image
    Bitmap mPlaceHolderBitmap;

    // Creator
    public ImageDownloader(Resources res){
        mPlaceHolderBitmap = BitmapFactory.decodeResource(res, R.drawable.test);
    }

    // Launches new ImageLoadTask if necessary
    public void loadBitmap(String url, ImageView imageView) {
        if (cancelPotentialWork(url, imageView)) {

            ImageLoadTask task = new ImageLoadTask(url, imageView);
            AsyncDrawable asyncDrawable = new AsyncDrawable(imageView.getResources(), mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute();
        }
    }

    // Checks whether there is working imageLoadTask on imageView and cancels it if it's not the task that should be (task downloads previous image)
    public static boolean cancelPotentialWork(String url, ImageView imageView) {
        ImageLoadTask imageLoadTask = getImageLoadTask(imageView);

        if (imageLoadTask != null) {
            String bitmapUrl = imageLoadTask.getUrl();

            // If bitmapUrl is not yet set or it differs from the new url
            if ((bitmapUrl == null) || !bitmapUrl.equals(url)) {

                // Cancel previous task
                imageLoadTask.cancel(true);
            } else {

                // The same work is already in progress
                return false;
            }
        }

        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    // Gets ImageLoadTask from imageView
    private static ImageLoadTask getImageLoadTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();

            if (drawable instanceof AsyncDrawable) {
                AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getImageLoadTask();
            }
        }
        return null;
    }

    // Class which asynchronously downloads photos
    class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private final WeakReference<ImageView> imageViewWeakReference;

        // Getters
        public String getUrl() {
            return url;
        }

        // Creator
        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            imageViewWeakReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                InputStream input = new URL(url).openStream();
                Bitmap finalBitmap = BitmapFactory.decodeStream(input);
                return finalBitmap;
            } catch (Exception e) {
                Log.d("TAG", "exception in doInBackground " + url);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (isCancelled()) {
                result = null;
                Log.d("TAG","was cancelled "+url);
            }

            if (imageViewWeakReference != null && result != null) {
                ImageView imageView = imageViewWeakReference.get();
                final ImageLoadTask imageLoadTask =
                        getImageLoadTask(imageView);
                if (this == imageLoadTask ) {
                    imageView.setImageBitmap(result);
                }
            }
        }

    }

    // Drawable which stores reference on it's ImageLoadTask and set's downloading bitmap
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<ImageLoadTask> imageLoadTaskWeakReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, ImageLoadTask imageLoadTask) {
            super(res, bitmap);
            imageLoadTaskWeakReference = new WeakReference<>(imageLoadTask);
        }

        public ImageLoadTask getImageLoadTask() {
            return imageLoadTaskWeakReference.get();
        }
    }

}

