package com.fitness.myapplication;

import java.util.List;

public interface CallbackListener {
    void onCallback(List<List<ExerciseScheduler.Result>> result);
}
