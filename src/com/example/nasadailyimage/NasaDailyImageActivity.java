package com.example.nasadailyimage;

import java.io.IOException;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nasadailyimage.sax.IotdHandler;

public class NasaDailyImageActivity extends Fragment {

    private static final String TAG = "NasaDailyImageActivity";

    private Handler handler;

    private ProgressDialog dialog;

    private Bitmap image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new DailyImageHandler(this);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.daily_image, container, false);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        refreshFromFeed(handler);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nasa_daily_image, menu);
        return true;
    }*/

    public void onRefreshButtonClick(View view) {

        refreshFromFeed(handler);
    }

    public void onSetWallPaperButtonClick(View view) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                WallpaperManager wpManager = WallpaperManager.getInstance(getActivity());
                try {
                    if (image != null) {
                        wpManager.setBitmap(image);
                        Toast.makeText(getActivity(), "Wallpaper set", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Unable to set wallpaper", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Unable to set a null image as wallpaper");
                    }
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "Error setting wallpaper", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error setting wallpaper", e);
                }
            }
        });
    }

    private void refreshFromFeed(final Handler handler) {

        dialog = ProgressDialog.show(getActivity(), "Loading", "loading the image of the day");

        final IotdHandler iotdHandler = new IotdHandler();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                iotdHandler.processFeedAndDispatch(handler);
            }
        });

        Log.i(TAG, "Starting refresh");
        t.start();
    }

    private static final class DailyImageHandler extends Handler {

        private static final String TAG = "DailyImageHandler";

        private NasaDailyImageActivity fragment;

        public DailyImageHandler(NasaDailyImageActivity activity) {
            this.fragment = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handling message callback");
            Bundle data = msg.getData();

            resetDisplay((String) data.get("title"), (String) data.get("date"), (Bitmap) data.get("image"),
                    (String) data.get("description"));
            if (fragment.dialog != null) {
                fragment.dialog.dismiss();
            }
        }

        private void resetDisplay(String title, String date, Bitmap image, String description) {
            TextView titleTextView = (TextView) fragment.getActivity().findViewById(R.id.imageTitle);
            titleTextView.setText(title);

            TextView dateTextView = (TextView) fragment.getActivity().findViewById(R.id.imageDate);
            dateTextView.setText(date);

            ImageView imageView = (ImageView) fragment.getActivity().findViewById(R.id.imageDisplay);
            imageView.setImageBitmap(image);

            TextView descriptionTextView = (TextView) fragment.getActivity().findViewById(R.id.imageDescription);
            descriptionTextView.setText(description);

            fragment.image = image;
        }
    }
}
