package com.fitness.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.fitness.database.schema.DifficultyLevel;
import com.fitness.database.schema.Equipment;

import com.fitness.database.schema.Exercise;
import com.fitness.database.schema.MuscleGroup;
import com.fitness.fuzzy.MetEstimator;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FitnessStore {
    private final ExerciseDatabase db;
    private final Context context;
    private final int MUSCLE_GROUP_INDEX = 3;
    private final int EQUIPMENT_INDEX = 4;
    private final int DIFFICULTY_INDEX = 5;
    public FitnessStore(ExerciseDatabase db, Context context)
    {
        this.db = db;
        this.context = context;
    }

    public void prePopulateFitnessStore(){

        AssetManager assetManager = context.getAssets();
        try(InputStreamReader inputStreamReader = new InputStreamReader(assetManager.open("ExerciseDataset.csv"));
            CSVReader reader = new CSVReaderBuilder(inputStreamReader)
                    .withCSVParser(new CSVParserBuilder().withQuoteChar('"').build())
                    .withSkipLines(1)
                    .build()){

            List<String[]> lines = reader.readAll();
            reader.close();

            Map<String, Long> muscleGroupToIdMap = populateMuscleGroups(lines.stream()
                    .map(line -> line[MUSCLE_GROUP_INDEX])
                    .collect(Collectors.toSet()));

            Map<String, Long> equipmentToIdMap = populateEquipments(lines.stream()
                    .map(line -> line[EQUIPMENT_INDEX])
                    .collect(Collectors.toSet()));

            populateExercise(muscleGroupToIdMap, equipmentToIdMap, lines);
            Log.i("FitnessStore", "Finished updating contents");
        }
        catch (IOException | CsvException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void populateExercise(Map<String, Long> muscleGroupToIdMap, Map<String, Long> equipmentToIdMap, List<String[]> lines) {
        MetEstimator mes = new MetEstimator(context);
        for(String[] line: lines){
            String muscleGroupValue = line[MUSCLE_GROUP_INDEX];
            String equipmentValue = line[EQUIPMENT_INDEX];
            String difficultyValue = line[DIFFICULTY_INDEX];
            String name = line[0];
            String desc = line[1];
            String type = line[2];
            String rating = line[6].isEmpty() ? "5.0" : line[6];
            String intensity = line[9];
            double weightFactor = Double.parseDouble(line[8]);

            long muscleGroupId = muscleGroupToIdMap.get(muscleGroupValue);
            long equipmentId = equipmentToIdMap.get(equipmentValue);

            DifficultyLevel difficultyLevel = DifficultyLevel.valueOf(difficultyValue.toUpperCase());
            double metScore = mes.getMetScore(intensity, difficultyValue);

            Exercise ex = new Exercise(name, desc, type, difficultyLevel, (int) muscleGroupId, (int) equipmentId, Double.parseDouble(rating), metScore, weightFactor);
            db.fitnessDao().insertExercise(ex);
        }
    }

    private Map<String, Long> populateEquipments(Set<String> equipmentSet) {
        Map<String, Long> equipmentToIdMap = new HashMap<>();
        for (String equipment: equipmentSet){
            long equipmentId = db.fitnessDao().insertEquipment(new Equipment(equipment));
            equipmentToIdMap.put(equipment, equipmentId);
        }
        return equipmentToIdMap;
    }

    private Map<String, Long> populateMuscleGroups(Set<String> muscleGroupSet) {
        Map<String, Long> muscleGroupToIdMap = new HashMap<>();
        for(String muscleGroup: muscleGroupSet){
            long muscleGroupId = db.fitnessDao().insertMuscleGroup(new MuscleGroup(muscleGroup));
            muscleGroupToIdMap.put(muscleGroup, muscleGroupId);
        }
        return muscleGroupToIdMap;
    }

}
