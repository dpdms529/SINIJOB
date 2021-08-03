package org.techtown.hanieum.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.techtown.hanieum.db.dao.BdongDao;
import org.techtown.hanieum.db.dao.JobCategoryDao;
import org.techtown.hanieum.db.dao.RecruitCertificateDao;
import org.techtown.hanieum.db.dao.RecruitDao;
import org.techtown.hanieum.db.entity.Bdong;
import org.techtown.hanieum.db.entity.JobCategory;
import org.techtown.hanieum.db.entity.Recruit;
import org.techtown.hanieum.db.entity.RecruitCertificate;


@Database(entities = {JobCategory.class, Bdong.class, Recruit.class, RecruitCertificate.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract JobCategoryDao jobCategoryDao();
    public abstract BdongDao BdongDao();
    public abstract RecruitDao RecruitDao();
    public abstract RecruitCertificateDao recruitCertificateDao();

    private static AppDatabase INSTANCE = null;

    private static final Object sLock = new Object();

    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "hanium.db")
                            .createFromAsset("database/hanium.db")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
            }
            return INSTANCE;
        }
    }

}
