package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.Bdong;

import java.util.List;

@Dao
public interface BdongDao {
    @Query("SELECT * FROM b_dong")
    List<Bdong> getAll();

    @Query("SELECT distinct(sido_name) FROM b_dong")
    List<String> getsido();

    @Query("select distinct(sigungu_name) from b_dong where sido_name = :sido_name")
    List<String> getsigungu(String sido_name);

    @Query("select distinct(eupmyeondong_name) from b_dong where sido_name = :sido_name and sigungu_name = :sigungu_name")
    List<String> geteupmyeondong(String sido_name, String sigungu_name);


    @Query("select b_dong_code from b_dong where sido_name = :sido_name and sigungu_name = :sigungu_name and eupmyeondong_name = :eupmyeondong_name")
    String getBDongCode(String sido_name, String sigungu_name, String eupmyeondong_name);

    @Query("select substr(b_dong_code,1,2) from b_dong where sido_name = :sido_name limit 1")
    String getTotalSidoCode(String sido_name);

    @Query("select substr(b_dong_code,1,5) from b_dong where sido_name = :sido_name and sigungu_name = :sigungu_name limit 1;")
    String getTotalSigunguCode(String sido_name, String sigungu_name);

    @Query("select b_dong_code from b_dong where substr(b_dong_code,1,2) = :b_dong_code")
    List<String> getAllSidoCode(String b_dong_code);

    @Query("select b_dong_code from b_dong where substr(b_dong_code,1,5) = :b_dong_code")
    List<String> getAllSigunguCode(String b_dong_code);
}
