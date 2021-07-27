package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.JobCategory;

import java.util.List;

@Dao
    public interface JobCategoryDao {
    @Query("SELECT * FROM job_category")
    List<JobCategory> getAll() ;

    @Insert
    void insertAll(JobCategory... categories);

    @Delete
    void delete(JobCategory category);
}
