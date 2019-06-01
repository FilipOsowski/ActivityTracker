package com.example.catblue.time_tracker;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UserActivityDao {

    @Query("SELECT * FROM CurrentActivity LIMIT 1")
    CurrentActivity getCurrentActivity();

    @Query("DELETE FROM CurrentActivity")
    void deleteCurrentActivity();

    @Query("SELECT * FROM UserActivity")
    List<UserActivity> getAll();

    @Insert
    void insertCurrentActivity(CurrentActivity currentActivity);

    @Insert
    void insertActivityLog(ActivityLog activityLog);

    @Query("SELECT * FROM ActivityLog")
    List<ActivityLog> getActivityLog();

    @Query("DELETE FROM ActivityLog")
    void deleterActivityLog();

    @Query("SELECT name FROM UserActivity")
    List<String> getAllUserActivityNames();

    @Insert
    void insertUserActivity(UserActivity... userActivities);

    @Query("DELETE FROM UserActivity")
    void deleteUserActivities();
}

