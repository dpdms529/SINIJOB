package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.Recruit;
import org.techtown.hanieum.db.entity.RecruitCertificate;

import java.util.List;

@Dao
public interface RecruitCertificateDao {
    @Query("SELECT * FROM recruit_certificate")
    List<RecruitCertificate> getAll();

    @Query("SELECT MAX(r.update_dt) FROM recruit_certificate c INNER JOIN recruit r ON c.recruit_id = r.recruit_id")
    List<String> getLastUpdated();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewRecruit(RecruitCertificate newRecruit);

    @Delete
    void deleteGoneRecruit(RecruitCertificate newRecruit);

//    @Query("DElETE FROM recruit_certificate WHERE certificate_no = :certificate_no and recruit_id = :goneId")
//    void deleteGoneRecruit(Integer certificate_no, String goneId);

}