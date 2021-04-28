package com.example.android26.ui;

import android.app.Application;


import androidx.room.Room;

import com.example.android26.ui.room.FillDatabase;
import com.example.android26.ui.sharePrefs.Share;


public class App extends Application {
    public static App instance;

    public static Share share;
    public static FillDatabase fillDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        share = new Share(this);

        fillDatabase = Room.databaseBuilder(
                this, FillDatabase.class, "database")
                .allowMainThreadQueries()
                .build();

    }
}
