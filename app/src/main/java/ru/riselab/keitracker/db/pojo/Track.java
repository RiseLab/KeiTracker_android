package ru.riselab.keitracker.db.pojo;

import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;

import java.util.Date;

import ru.riselab.keitracker.utils.DateConverter;

@TypeConverters(DateConverter.class)
public class Track {

    @ColumnInfo(name = "track_uuid")
    public String trackUuid;

    @ColumnInfo(name = "first_time")
    public Date firstTime;

    @ColumnInfo(name = "last_time")
    public Date lastTime;
}
