package org.techtown.hanieum.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "certificate")
public class Certificate {
    @PrimaryKey
    @NonNull
    public String certificate_id;

    @ColumnInfo
    @NonNull
    public String category_code;

    @ColumnInfo
    @NonNull
    public String certificate_name;
}
