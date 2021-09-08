package org.techtown.hanieum.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cover_letter")
public class CoverLetter {
    @PrimaryKey
    @NonNull
    public Integer cover_letter_no;

    @ColumnInfo(name = "cover_dist_code")
    @NonNull
    public String cover_dist_code;

    @ColumnInfo(name = "first_item")
    @NonNull
    public String first_item;

    @ColumnInfo(name = "second_item")
    @NonNull
    public String second_item;

    @ColumnInfo(name = "third_item")
    @NonNull
    public String third_item;
}
