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

    @ColumnInfo(name = "x")
    @NonNull
    public String x_coordinate;

    @ColumnInfo(name = "y")
    @NonNull
    public String y_coordinate;
}
