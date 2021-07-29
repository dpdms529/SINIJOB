package org.techtown.hanieum.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.Recruit;

import java.util.List;

@Dao
public interface RecruitDao {
    @Query("SELECT * FROM recruit")
    List<Recruit> getAll();

}
