package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.CoverLetter;

import java.util.List;

@Dao
public interface CoverLetterDao {
    @Query("SELECT * FROM cover_letter")
    List<CoverLetter> getAll();

    @Query("SELECT * FROM cover_letter where cover_letter_no = :no")
    CoverLetter getSelected(int no);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCoverLetter(CoverLetter coverLetter);

    @Query("DElETE FROM cover_letter WHERE cover_letter_no = :no")
    void deleteCoverLetter(int no);
}
