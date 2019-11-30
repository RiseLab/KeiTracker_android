package ru.riselab.keitracker.db.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "track")
public class TrackModel {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer mId;

    @Nullable
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "started_at")
    private Long mStartedAt;

    @Nullable
    @ColumnInfo(name = "stopped_at")
    private Long mStoppedAt;

    public TrackModel(@Nullable String name, @NonNull Long startedAt, @Nullable Long stoppedAt) {
        mName = name;
        mStartedAt = startedAt;
        mStoppedAt = stoppedAt;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    public void setName(@Nullable String name) {
        mName = name;
    }

    @NonNull
    public Long getStartedAt() {
        return mStartedAt;
    }

    public void setStartedAt(@NonNull Long startedAt) {
        mStartedAt = startedAt;
    }

    @Nullable
    public Long getStoppedAt() {
        return mStoppedAt;
    }

    public void setStoppedAt(@Nullable Long stoppedAt) {
        mStoppedAt = stoppedAt;
    }
}
