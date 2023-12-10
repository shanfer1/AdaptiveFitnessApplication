package com.fitness.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.fitness.database.schema.DifficultyLevel;
import com.fitness.database.schema.Equipment;
import com.fitness.database.schema.Exercise;
import com.fitness.database.schema.MuscleGroup;
import com.fitness.database.schema.Schedule;
import com.fitness.myapplication.ExerciseScheduler;

import java.sql.Blob;
import java.util.List;

@Dao
public interface FitnessDao {
    final String exerciseFilterSortedQuery = "SELECT * FROM Exercise e " +
            "JOIN Equipment eq ON e.equipment_id = eq.equipmentId " +
            "JOIN MuscleGroup mg ON e.muscle_id = mg.muscleGroupID " +
            "WHERE eq.equipment_name IN  ( :equipmentNames) AND mg.muscle_name IN ( :muscleNames) " +
            "AND  e.difficulty == :difficulty " +
            "ORDER BY e.metScore ASC";

    final String getNextScheduleQuery = "SELECT * from schedule WHERE email_id = :emailID";

    final String deleteScheduleQuery = "DELETE from schedule where email_id = :emailID";

    @Insert
    void insertExercise(Exercise exercise);

    @Insert
    long insertMuscleGroup(MuscleGroup muscleGroup);

    @Insert
    long insertEquipment(Equipment equipment);

    @Insert
    long insertSchedule(Schedule schedules);

    @Insert
    void insertEquipmentList(List<Equipment> equipmentList);

    @Insert
    void insertMuscleGroupList(List<MuscleGroup> muscleGroupList);

    @Query("SELECT count(*) FROM exercise")
    int countRows();

    @Query("SELECT * FROM exercise")
    List<Exercise> getAllExercises();

    @Query("SELECT * FROM musclegroup")
    List<MuscleGroup> getAllMuscleGroups();

    @Query("SELECT equipment_name FROM equipment")
    List<String> getAllEquipmentNames();

    @Query("SELECT equipmentId FROM equipment WHERE equipment_name= :equipmentName")
    int getEquipmentIdByName(String equipmentName);

    @Query("SELECT muscleGroupID FROM MuscleGroup WHERE muscle_name= :muscleName")
    int getMuscleIdByName(String muscleName);

    @Query(exerciseFilterSortedQuery)
    List<Exercise> getSortedFilteredExercise(List<String> equipmentNames, List<String> muscleNames, DifficultyLevel difficulty);

    @Query("SELECT count(*) from schedule where email_id = :emailID")
    int getEntriesInScheduleForUser(String emailID);

    @Query(getNextScheduleQuery)
    List<Schedule> getNextScheduleForUser(String emailID);

    @Query(deleteScheduleQuery)
    void deleteCompletedScheduleForUser(String emailID);
}
