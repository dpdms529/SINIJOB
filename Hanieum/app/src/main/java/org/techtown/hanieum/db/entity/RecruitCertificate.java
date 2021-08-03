package org.techtown.hanieum.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recruit_certificate", primaryKeys = {"certificate_no", "recruit_id"})
public class RecruitCertificate {
    @NonNull
    public Integer certificate_no;

    @NonNull
    public String recruit_id;

    @ColumnInfo(name = "certificate_id")
    @NonNull
    public String certificate_id;
}
