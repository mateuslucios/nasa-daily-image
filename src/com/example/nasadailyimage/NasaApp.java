package com.example.nasadailyimage;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

public class NasaApp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void onRefreshButtonClick(View view) {
        FragmentManager fm = getFragmentManager();
        NasaDailyImageActivity fragment = (NasaDailyImageActivity) fm.findFragmentById(R.id.dailyImageFragment);
        fragment.onRefreshButtonClick(view);
    }

    public void onSetWallPaperButtonClick(View view) {
        FragmentManager fm = getFragmentManager();
        NasaDailyImageActivity fragment = (NasaDailyImageActivity) fm.findFragmentById(R.id.dailyImageFragment);
        fragment.onSetWallPaperButtonClick(view);
    }
    
}
