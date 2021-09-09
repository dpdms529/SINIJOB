package org.techtown.hanieum.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import org.techtown.hanieum.db.entity.JobCategory;

import java.util.List;

@Dao
    public interface JobCategoryDao {
    @Query("SELECT * FROM job_category")
    List<JobCategory> getAll() ;

    @Query("SELECT * FROM job_category WHERE LENGTH(category_code) < 5")
    List<JobCategory> getCategory() ;

    @Query("select category_code from job_category where primary_cate_code = :primary_cate_code")
    List<String> getAllJobCode(String primary_cate_code);

    @Query("select category_name from job_category where category_code = :category_code")
    String getCategoryName(String category_code);

}
