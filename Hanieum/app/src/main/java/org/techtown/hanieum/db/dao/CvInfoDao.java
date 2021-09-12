package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.CvInfo;

import java.util.List;

@Dao
public interface CvInfoDao {
    @Query("SELECT * FROM cv_info")
    List<CvInfo> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCvInfo(CvInfo cvInfo);

    @Query("select info_code from cv_info where cv_dist_code = :cv_dist_code")
    String getInfoCode(String cv_dist_code);
}
