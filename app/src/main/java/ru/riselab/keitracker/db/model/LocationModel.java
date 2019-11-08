package ru.riselab.keitracker.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import ru.riselab.keitracker.utils.DateConverter;

import java.util.Date;

@Entity(tableName = "location")
@TypeConverters({DateConverter.class})
public class LocationModel {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer mId;

    @NonNull
    @ColumnInfo(name = "track_uuid")
    private String mTrackUuid;

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
    private Date mFixedAt;

    public LocationModel(@NonNull String trackUuid,
                         @NonNull Double latitude,
                         @NonNull Double longitude,
                         @NonNull Double altitude,
                         @NonNull Date fixedAt) {
        mTrackUuid = trackUuid;
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
    public String getTrackUuid() {
        return mTrackUuid;
    }

    public void setTrackUuid(@NonNull String trackUuid) {
        mTrackUuid = trackUuid;
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
    public Date getFixedAt() {
        return mFixedAt;
    }

    public void setFixedAt(@NonNull Date fixedAt) {
        mFixedAt = fixedAt;
    }
}

