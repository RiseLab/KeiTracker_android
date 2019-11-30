package ru.riselab.keitracker.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.riselab.keitracker.db.dao.PointDao;
import ru.riselab.keitracker.db.dao.TrackDao;
import ru.riselab.keitracker.db.model.PointModel;
import ru.riselab.keitracker.db.model.TrackModel;

@Database(entities = {TrackModel.class, PointModel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TrackDao trackDao();
    public abstract PointDao pointDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "keitracker_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
