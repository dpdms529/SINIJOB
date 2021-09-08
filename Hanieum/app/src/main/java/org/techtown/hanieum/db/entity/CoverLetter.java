package org.techtown.hanieum.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cover_letter")
public class CoverLetter {
    @PrimaryKey(autoGenerate = true)
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

    public CoverLetter(String cover_dist_code, String first_item, String second_item, String third_item){
        this.cover_dist_code = cover_dist_code;
        this.first_item = first_item;
        this.second_item = second_item;
        this.third_item = third_item;
    }
}
