package ru.riselab.keitracker.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.riselab.keitracker.db.dao.LocationDao;
import ru.riselab.keitracker.db.dao.PointDao;
import ru.riselab.keitracker.db.dao.TrackDao;
import ru.riselab.keitracker.db.model.LocationModel;
import ru.riselab.keitracker.db.model.PointModel;
import ru.riselab.keitracker.db.model.TrackModel;

@Database(entities = {LocationModel.class, TrackModel.class, PointModel.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract LocationDao locationDao();
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
                            .addMigrations(AppDatabase.MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `track` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT, `started_at` INTEGER NOT NULL, `stopped_at` INTEGER)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `point` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `track_id` INTEGER NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `altitude` REAL NOT NULL, `fixed_at` INTEGER NOT NULL)");
        }
    };
}
