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
}