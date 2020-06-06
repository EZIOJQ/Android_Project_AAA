package com.example.aaa;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Image.class, MarkerEntity.class}, version = 5)
public abstract class  AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "PiccaDB.db";
    private static volatile AppDatabase appDatabase;
    static synchronized AppDatabase getInstance(Context context){
        if (appDatabase == null) appDatabase = create(context);
        return appDatabase;
    }

    private static AppDatabase create(final Context context){
        return Room.databaseBuilder(context,AppDatabase.class,DB_NAME).fallbackToDestructiveMigration().build();
    }
    public abstract ImageDAO getImageDao();
}
