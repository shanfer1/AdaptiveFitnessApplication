package com.fitness.database.schema;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fitness.myapplication.ExerciseScheduler;

import java.util.List;

@Entity(tableName = "schedule")
public class Schedule {
    @PrimaryKey(autoGenerate = true)
    public long schedule_id;

    @ColumnInfo(name="email_id")
    public String email_id;

    @ColumnInfo(name="schedule_content", typeAffinity = ColumnInfo.BLOB)
    public byte[] scheduleBlob;

    @Ignore
    public Schedule(String email_id, List<ExerciseScheduler.Result> resultList){
        this.email_id = email_id;
        this.scheduleBlob = ExcersiseSchedulerConverter.fromResult(resultList);
    }

    // This constructor is for Room
    public Schedule(String email_id, byte[] scheduleBlob){
        this.email_id = email_id;
        this.scheduleBlob = scheduleBlob;
    }

    public List<ExerciseScheduler.Result> getSchedule() {
        return ExcersiseSchedulerConverter.toResult(scheduleBlob);
    }

    public void setSchedule(List<ExerciseScheduler.Result> schedule) {
        this.scheduleBlob = ExcersiseSchedulerConverter.fromResult(schedule);
    }

    public String getEmail_id() {
        return this.email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }
}
