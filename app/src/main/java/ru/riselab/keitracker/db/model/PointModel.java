package ru.riselab.keitracker.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "point")
public class PointModel {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer mId;

    @NonNull
    @ColumnInfo(name = "track_id")
    private Integer mTrackId;

    @NonNull
    @ColumnInfo(name = "latitude")
    private Double mLatitude;

    @NonNull
    @ColumnInfo(name = "longitude")
    private Double mLongitude;

    @NonNull
    @ColumnInfo(name = "altitude")
    private Double mAltitude;

    @NonNull
    @ColumnInfo(name = "fixed_at")
    private Long mFixedAt;

    public PointModel(@NonNull Integer trackId,
                      @NonNull Double latitude,
                      @NonNull Double longitude,
                      @NonNull Double altitude,
                      @NonNull Long fixedAt) {
        mTrackId = trackId;
        mLatitude = latitude;
        mLongitude = longitude;
        mAltitude = altitude;
        mFixedAt = fixedAt;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    @NonNull
    public Integer getTrackId() {
        return mTrackId;
    }

    public void setTrackId(@NonNull Integer trackId) {
        mTrackId = trackId;
    }

    @NonNull
    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(@NonNull Double latitude) {
        mLatitude = latitude;
    }

    @NonNull
    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(@NonNull Double longitude) {
        mLongitude = longitude;
    }

    @NonNull
    public Double getAltitude() {
        return mAltitude;
    }

    public void setAltitude(@NonNull Double altitude) {
        mAltitude = altitude;
    }

    @NonNull
    public Long getFixedAt() {
        return mFixedAt;
    }

    public void setFixedAt(@NonNull Long fixedAt) {
        mFixedAt = fixedAt;
    }
}
