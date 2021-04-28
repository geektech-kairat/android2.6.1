package com.example.android26.ui.room;



import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.android26.ui.home.HomeAdapter.HomeModel;

@Database(entities = {HomeModel.class}, version = 1)
public abstract class FillDatabase extends RoomDatabase {
    public abstract FillDao fillDao();
}
