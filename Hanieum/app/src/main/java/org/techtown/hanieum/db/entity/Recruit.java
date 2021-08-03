package org.techtown.hanieum.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recruit")
public class Recruit {
    @PrimaryKey
    @NonNull
    public String recruit_id;

    @ColumnInfo(name = "title")
    @NonNull
    public String recruit_title;

    @ColumnInfo(name = "organization")
    @NonNull
    public String organization;

    @ColumnInfo(name = "salary_type_code")
    @NonNull
    public String salary_type_code;

    @ColumnInfo(name = "salary")
    @NonNull
    public String salary;

    @ColumnInfo(name = "b_dong_code")
    @NonNull
    public String b_dong_code;

    @ColumnInfo(name = "job_code")
    @NonNull
    public String job_code;

    @ColumnInfo(name = "career_required")
    @NonNull
    public String career_required;

    @ColumnInfo(name = "career_min")
    @NonNull
    public int career_min;

    @ColumnInfo(name = "enrollment_code")
    @NonNull
    public String enrollment_code;

    @ColumnInfo(name = "certificate_required")
    @NonNull
    public String certificate_required;

    @ColumnInfo(name = "x")
    @NonNull
    public String x_coordinate;

    @ColumnInfo(name = "y")
    @NonNull
    public String y_coordinate;

    @ColumnInfo(name = "update_dt")
    @NonNull
    public String update_dt;

    public Recruit(String recruit_id, String recruit_title, String organization, String salary_type_code, String salary, String b_dong_code,
                   String job_code, String career_required, int career_min, String enrollment_code, String certificate_required,
                   String x_coordinate, String y_coordinate, String update_dt){
        this.recruit_id = recruit_id;
        this.recruit_title = recruit_title;
        this.organization = organization;
        this.salary_type_code = salary_type_code;
        this.salary = salary;
        this.b_dong_code = b_dong_code;
        this.job_code = job_code;
        this.career_required = career_required;
        this.career_min = career_min;
        this.enrollment_code = enrollment_code;
        this.certificate_required = certificate_required;
        this.x_coordinate = x_coordinate;
        this.y_coordinate = y_coordinate;
        this.update_dt = update_dt;
    }
}
