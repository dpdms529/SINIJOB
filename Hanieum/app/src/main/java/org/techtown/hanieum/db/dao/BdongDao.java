package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.Bdong;

import java.util.List;

@Dao
public interface BdongDao {
    @Query("SELECT distinct(sido_name) FROM b_dong")
    List<String> getsido() ;
}
