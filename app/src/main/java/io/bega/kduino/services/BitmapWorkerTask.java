package io.bega.kduino.services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/*
 * Created by usuario on 15/09/15.
 */
 public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private int data = 0;
    private Activity ctx;

    public BitmapWorkerTask(Activity context, ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        ctx = context;
    }

    // Decode image in background.
     @Override
     protected Bitmap doInBackground(Integer... params)
     {
        data = params[0];
        return null;
        //return ctx.decodeSampledBitmapFromResource(getResources(), data, 100, 100));
     }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
         protected void onPostExecute(Bitmap bitmap) {
             if (imageViewReference != null && bitmap != null) {
             final ImageView imageView = imageViewReference.get();
             if (imageView != null) {
             imageView.setImageBitmap(bitmap);
            }
        }
    }
 }