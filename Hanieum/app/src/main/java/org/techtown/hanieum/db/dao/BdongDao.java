package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.Bdong;

import java.util.HashMap;
import java.util.List;

@Dao
public interface BdongDao {
    @Query("SELECT * FROM b_dong")
    List<Bdong> getAll();

    @Query("SELECT distinct(sido_name) FROM b_dong")
    List<String> getsido() ;

    @Query("select distinct(sigungu_name) from b_dong where sido_name like :sido_name")
    List<String> getsigungu(String sido_name);

    @Query("select distinct(eupmyeondong_name) from b_dong where sido_name like :sido_name and sigungu_name like :sigungu_name")
    List<String> geteupmyeondong(String sido_name, String sigungu_name);

    @Query("select b_dong_code from b_dong where sido_name like :sido_name and sigungu_name like :sigungu_name like :eupmyeondong_name")
    String getBDongCode(String sido_name, String sigungu_name, String eupmyeondong_name);
}
