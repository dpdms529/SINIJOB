package org.techtown.hanieum.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.Recruit;

import java.util.List;

@Dao
public interface RecruitDao {
    @Query("SELECT * FROM recruit")
    LiveData<List<Recruit>> getAll();

    @Query("SELECT MAX(update_dt) FROM recruit")
    List<String> getLastUpdated();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewRecruit(Recruit newRecruit);

    @Query("DElETE FROM recruit WHERE recruit_id = :goneId")
    void deleteGoneRecruit(String goneId);
}
