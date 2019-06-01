package com.example.catblue.time_tracker;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.sql.Time;

@Entity(tableName = "CurrentActivity")
public class CurrentActivity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "activityName")
    private String activityName;

    @ColumnInfo(name = "startTime")
    private Long startTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
}
