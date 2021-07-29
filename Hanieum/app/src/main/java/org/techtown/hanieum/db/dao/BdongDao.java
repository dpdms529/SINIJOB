package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.Bdong;

import java.util.List;

@Dao
public interface BdongDao {
    @Query("SELECT distinct(sido_name) FROM b_dong")
    List<String> getsido() ;

    @Query("select distinct(sigungu_name) from b_dong where sido_name like :sido_name")
    List<String> getsigungu(String sido_name);

    @Query("select distinct(eupmyeondong_name) from b_dong where sido_name like :sido_name and sigungu_name like :sigungu_name")
    List<String> geteupmyeondong(String sido_name, String sigungu_name);
}
