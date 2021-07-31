package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.Recruit;

import java.util.List;

@Dao
public interface RecruitCertificateDao {
    @Query("SELECT * FROM recruit_certificate")
    List<Recruit> getAll();

}