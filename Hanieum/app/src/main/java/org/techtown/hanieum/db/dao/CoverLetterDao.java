package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.CoverLetter;

import java.util.List;

@Dao
public interface CoverLetterDao {
    @Query("SELECT * FROM cover_letter")
    List<CoverLetter> getAll();
}
