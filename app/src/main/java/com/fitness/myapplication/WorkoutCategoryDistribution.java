package com.fitness.myapplication;

import com.fitness.database.schema.DifficultyLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutCategoryDistribution {
    private double beginnerPercentage;
    private double intermediatePercentage;
    private double expertPercentage;

    public WorkoutCategoryDistribution(){
        beginnerPercentage = 0.0;
        intermediatePercentage = 0.0;
        expertPercentage = 0.0;
    }

    public Map<DifficultyLevel, Double> getPercentageMap(){
       Map<DifficultyLevel, Double> percMap = new HashMap<>();
       percMap.put(DifficultyLevel.BEGINEER, beginnerPercentage);
       percMap.put(DifficultyLevel.INTERMEDIATE, intermediatePercentage);
       percMap.put(DifficultyLevel.EXPERT, expertPercentage);

       return percMap;
    }
    public double getBeginnerPercentage() {
        return beginnerPercentage;
    }

    public double getExpertPercentage() {
        return expertPercentage;
    }

    public double getIntermediatePercentage() {
        return intermediatePercentage;
    }

    public void setBeginnerPercentage(double beginnerPercentage) {
        this.beginnerPercentage = beginnerPercentage;
    }

    public void setExpertPercentage(double expertPercentage) {
        this.expertPercentage = expertPercentage;
    }

    public void setIntermediatePercentage(double intermediatePercentage) {
        this.intermediatePercentage = intermediatePercentage;
    }
}
