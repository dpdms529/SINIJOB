package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.CvInfo;

import java.util.List;

@Dao
public interface CvInfoDao {
    @Query("SELECT * FROM cv_info")
    List<CvInfo> getAll();
}
