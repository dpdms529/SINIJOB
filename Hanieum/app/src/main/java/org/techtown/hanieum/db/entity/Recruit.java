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
    public String career_min;

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
}
