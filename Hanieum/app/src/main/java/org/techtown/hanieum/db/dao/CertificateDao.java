package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.Certificate;

import java.util.List;

@Dao
public interface CertificateDao {
    @Query("SELECT * FROM certificate")
    List<Certificate> getAll();
}
