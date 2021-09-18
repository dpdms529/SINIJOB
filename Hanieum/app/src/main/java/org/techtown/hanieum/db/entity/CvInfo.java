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

    @ColumnInfo(name = "info")
    @NonNull
    public String info;

    @ColumnInfo(name = "company_name")
    public String company_name;

    @ColumnInfo(name = "job_position")
    public String job_position;

    @ColumnInfo(name = "carrer_start")
    public String career_start;

    @ColumnInfo(name = "carrer_end")
    public String career_end;

    public CvInfo(String cv_dist_code, Integer info_no, String info_code, String info) {
        this.cv_dist_code = cv_dist_code;
        this.info_no = info_no;
        this.info_code = info_code;
        this.info = info;
    }

    public CvInfo(String cv_dist_code, Integer info_no, String info_code, String info, String company_name, String job_position, String career_start, String career_end) {
        this.cv_dist_code = cv_dist_code;
        this.info_no = info_no;
        this.info_code = info_code;
        this.info = info;
        this.company_name = company_name;
        this.job_position = job_position;
        this.career_start = career_start;
        this.career_end = career_end;
    }
}