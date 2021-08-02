package org.techtown.hanieum.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
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

    @Query("select * from recruit as r \n" +
            "where 1 = 1 \n" +
            "and (r.b_dong_code in (:bDongCode)) \n" +
            "and ((r.career_required = 2 and r.career_min < :career) or r.career_required<2)\n" +
            "and (r.certificate_required = 0 or exists (select 'x' from recruit_certificate as c where c.recruit_id  = r.recruit_id and c.certificate_id in (:certificateId)))\n" +
            "and (r.enrollment_code = :enrollmentCode);")
    List<Recruit> getFilteredList(List<String> bDongCode, String career, List<String> certificateId, String enrollmentCode);
}
