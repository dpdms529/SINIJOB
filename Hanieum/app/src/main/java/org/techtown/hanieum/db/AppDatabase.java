package org.techtown.hanieum.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.techtown.hanieum.db.dao.BdongDao;
import org.techtown.hanieum.db.dao.CertificateDao;
import org.techtown.hanieum.db.dao.CoverLetterDao;
import org.techtown.hanieum.db.dao.CvInfoDao;
import org.techtown.hanieum.db.dao.JobCategoryDao;
import org.techtown.hanieum.db.dao.RecruitCertificateDao;
import org.techtown.hanieum.db.dao.RecruitDao;
import org.techtown.hanieum.db.entity.Bdong;
import org.techtown.hanieum.db.entity.Certificate;
import org.techtown.hanieum.db.entity.CoverLetter;
import org.techtown.hanieum.db.entity.CvInfo;
import org.techtown.hanieum.db.entity.JobCategory;
import org.techtown.hanieum.db.entity.Recruit;
import org.techtown.hanieum.db.entity.RecruitCertificate;

@Database(entities = {JobCategory.class, Bdong.class, Recruit.class, RecruitCertificate.class, CvInfo.class, CoverLetter.class, Certificate.class}, version = 10, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract JobCategoryDao jobCategoryDao();

    public abstract BdongDao BdongDao();

    public abstract RecruitDao RecruitDao();

    public abstract RecruitCertificateDao recruitCertificateDao();

    public abstract CvInfoDao CvInfoDao();

    public abstract CoverLetterDao CoverLetterDao();

    public abstract CertificateDao CertificateDao();

    private static AppDatabase INSTANCE = null;

    private static final Object sLock = new Object();

    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
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
