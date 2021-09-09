package org.techtown.hanieum.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "job_category")
public class JobCategory {
    @PrimaryKey
    @NonNull
    public String category_code;

    @ColumnInfo(name = "primary_cate_code")
    public String primary_cate_code;

    @ColumnInfo(name = "category_name")
    @NonNull
    public String category_name;
}

