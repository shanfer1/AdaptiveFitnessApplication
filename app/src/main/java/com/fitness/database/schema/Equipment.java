package com.fitness.database.schema;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "equipment",
        indices = {@Index("equipmentId"), @Index(value = {"equipment_name"}, unique = true)})
public class Equipment {
    @PrimaryKey(autoGenerate = true)
    public int equipmentId;

    @ColumnInfo(name="equipment_name")
    private String equipmentName;

    public Equipment(String equipmentName){
        this.equipmentName = equipmentName;
    }

    public String getEquipmentName(){ return this.equipmentName; }
    public void setEquipmentName(String  equipmentName){ this.equipmentName = equipmentName; }
}
