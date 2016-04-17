package leopikinc.musiciansyandex;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

// Downloads bitmpas, cancels not necessary tasks (cares for all downloading work)
public class ImageDownloader {

    // Loading image
    Bitmap mPlaceHolderBitmap;

    // Cache
    private LruCache<String, Bitmap> mMemoryCache;

    Context context;
    Resources res;

    // Creator
    public ImageDownloader(Resources res, int resId, Context context){

        mPlaceHolderBitmap = BitmapFactory.decodeResource(res, resId);
        this.context = context;
        this.res = res;

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    // Launches new ImageLoadTask if necessary
    public void loadBitmap(String url, ImageView imageView) {
        final Bitmap bitmap = getBitmapFromMemCache(url);
        AsyncDrawable asyncDrawable;

        if (bitmap != null){
//            imageView.setImageBitmap(bitmap);
            setBitmapAndAnimate(imageView, bitmap);
        }else {
            if (cancelPotentialWork(url, imageView)) {
                ImageLoadTask task = new ImageLoadTask(url, imageView);
                asyncDrawable = new AsyncDrawable(imageView.getResources(), mPlaceHolderBitmap, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute();
            }
        }
    }

    private void setBitmapAndAnimate(ImageView imageView, Bitmap bitmap) {
        // Animates bitmap appearance
        final TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                new ColorDrawable(Color.BLACK),
                new BitmapDrawable(res, bitmap)
        });
        imageView.setImageDrawable(td);
        int FADE_TIME = 200;
        td.startTransition(FADE_TIME);
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

    // Calculates size of image
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public  Bitmap decodeBitmapFromUrl(String link, int reqWidth, int reqHeight) {
        InputStream inputStream = null;
        final BitmapFactory.Options options;
        try {
            inputStream = new URL(link).openStream();

            // First decode with inJustDecodeBounds=true to check dimensions
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        try {
            inputStream = new URL(link).openStream();

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            // In case imageView still doesn't have bounds
            if (reqHeight != 0 && reqWidth != 0) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                addBitmapToMemoryCache(link, bitmap);
                return bitmap;
            } else {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                addBitmapToMemoryCache(link, bitmap);
                return bitmap;
            }
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
        return null;
    }

    // Class which asynchronously downloads photos
    class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private final WeakReference<ImageView> imageViewWeakReference;
        private int reqHeight;
        private int reqWidth;

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
        protected void onPreExecute() {
            super.onPreExecute();
            ImageView imageView = imageViewWeakReference.get();
            reqHeight = imageView.getHeight();
            reqWidth = imageView.getWidth();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return decodeBitmapFromUrl(url, reqWidth, reqHeight);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (isCancelled()) {
                result = null;
            }
            if (imageViewWeakReference != null && result != null) {
                ImageView imageView = imageViewWeakReference.get();

                final ImageLoadTask imageLoadTask =
                        getImageLoadTask(imageView);
                if (this == imageLoadTask ) {
//                    imageView.setImageBitmap(result);
                    setBitmapAndAnimate(imageView, result);
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

