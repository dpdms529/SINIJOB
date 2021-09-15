package org.techtown.hanieum.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "cv_info", primaryKeys = {"cv_dist_code", "info_no"})
public class CvInfo {
    @NonNull
    public String cv_dist_code;

    @NonNull
    public Integer info_no;

    @ColumnInfo(name = "info_code")
    @NonNull
    public String info_code;

    @ColumnInfo(name = "career_period")
    public Integer career_period;

    @ColumnInfo(name = "company_name")
    public String company_name;

    public CvInfo(String cv_dist_code, Integer info_no, String info_code, Integer career_period, String company_name) {
        this.cv_dist_code = cv_dist_code;
        this.info_no = info_no;
        this.info_code = info_code;
        this.career_period = career_period;
        this.company_name = company_name;
    }
}