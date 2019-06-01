package com.example.catblue.time_tracker;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {UserActivity.class, CurrentActivity.class, ActivityLog.class}, version = 1)
public abstract class ActivityDatabase extends RoomDatabase {
    public abstract UserActivityDao userActivityDao();
}

