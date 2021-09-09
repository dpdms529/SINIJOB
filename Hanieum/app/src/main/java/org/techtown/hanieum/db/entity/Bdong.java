package org.techtown.hanieum.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "b_dong")
public class Bdong {
    @PrimaryKey
    @NonNull
    public String b_dong_code;

    @ColumnInfo(name = "sido_name")
    @NonNull
    public String sido_name;

    @ColumnInfo(name = "sigungu_name")
    @NonNull
    public String sigungu_name;

    @ColumnInfo(name = "eupmyeondong_name")
    @NonNull
    public String eupmyeondong_name;
}

