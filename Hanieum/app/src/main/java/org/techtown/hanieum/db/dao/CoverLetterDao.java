package org.techtown.hanieum.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.CoverLetter;

import java.util.List;

@Dao
public interface CoverLetterDao {
    @Query("SELECT * FROM cover_letter")
    LiveData<List<CoverLetter>> getAll();

    @Query("SELECT * FROM cover_letter where user_id = :user_id and cover_letter_no = :no")
    CoverLetter getSelected(String user_id, String no);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCoverLetter(CoverLetter coverLetter);

    @Query("DElETE FROM cover_letter WHERE user_id = :user_id and cover_letter_no = :no")
    void deleteCoverLetter(String user_id, String no);

    @Query("UPDATE cover_letter SET first_item = :first, second_item = :second, third_item = :third where user_id = :user_id and cover_letter_no = :no")
    void updateCoverLetter(String first, String second, String third, String user_id, String no);
}
