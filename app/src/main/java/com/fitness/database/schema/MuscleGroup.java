package com.fitness.database.schema;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "MuscleGroup",
        indices = {@Index("muscleGroupID")})
public class MuscleGroup {
    @PrimaryKey(autoGenerate = true)
    public int muscleGroupID;

    @ColumnInfo(name="muscle_name")
    private String groupName;

    public MuscleGroup(String groupName){
        this.groupName = groupName;
    }

    public String getGroupName(){ return this.groupName; }
    public void setGroupName(String  groupName){ this.groupName = groupName; }
}
