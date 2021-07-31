package org.techtown.hanieum.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recruit_certificate")
public class RecruitCertificate {
    @PrimaryKey
    @NonNull
    public Integer certificate_no;

    @PrimaryKey
    @NonNull
    public String recruit_id;

    @ColumnInfo(name = "certificate_id")
    @NonNull
    public String certificate_id;
}
