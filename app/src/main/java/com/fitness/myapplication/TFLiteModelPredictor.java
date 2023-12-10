package com.fitness.myapplication;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import android.view.View;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteModelPredictor {

    private Interpreter tflite;

    public TFLiteModelPredictor(Context context) {
        try {
            if(this.checkModelFile(context)){
                tflite = new Interpreter(loadModelFile(context));
            }else{
                Log.d("TFLiteModelPredictor"," model not present in the assets folder");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkModelFile(Context context) {
        String[] assetFiles;
        try {
            assetFiles = context.getAssets().list("");
        } catch (IOException e) {
            Log.e("TFLiteModelPredictor", "Error listing asset files", e);
            return false;
        }

        for (String file : assetFiles) {
            if (file.equals("model.tflite")) {
                Log.d("TFLiteModelPredictor", "Model file found in assets.");
                return true;
            }
        }

        Log.e("TFLiteModelPredictor", "Model file not found in assets.");
        return false;
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public float predict(float age, float weight, float duration, float dreamWeight, float bmi,
                         float intensityDuration, float heartRate, float intensity,
                         float[] gender) {
        // Define the mean and standard deviation for each feature
        final float ageMean = 39.632158f, ageStd = 12.613468f;
        final float weightMean = 75.276863f, weightStd = 14.832395f;
        final float durationMean = 40.057263f, durationStd = 11.776426f;
        final float dreamWeightMean = 75.176499f, dreamWeightStd = 14.543041f;
        final float bmiMean = 26.854417f, bmiStd = 4.749255f;
        final float intensityDurationMean = 219.433517f, intensityDurationStd = 137.155537f;
        final float heartRateMean = 139.692980f, heartRateStd = 23.387773f;
        final float intensityMean = 5.458428f, intensityStd = 2.860920f;

        // Standardize the input features
        float standardizedAge = (age - ageMean) / ageStd;
        float standardizedWeight = (weight - weightMean) / weightStd;
        float standardizedDuration = (duration - durationMean) / durationStd;
        float standardizedDreamWeight = (dreamWeight - dreamWeightMean) / dreamWeightStd;
        float standardizedBmi = (bmi - bmiMean) / bmiStd;
        float standardizedIntensityDuration = (intensityDuration - intensityDurationMean) / intensityDurationStd;
        float standardizedHeartRate = (heartRate - heartRateMean) / heartRateStd;
        float standardizedIntensity = (intensity - intensityMean) / intensityStd;

        float[][] input = new float[1][10];
        input[0][0] = standardizedAge;
        input[0][1] = standardizedWeight;
        input[0][2] = standardizedDuration;
        input[0][3] = standardizedDreamWeight;
        input[0][4] = standardizedBmi;
        input[0][5] = standardizedIntensityDuration;
        input[0][6] = standardizedHeartRate;
        input[0][7] = standardizedIntensity;
        input[0][8] = gender[0];
        input[0][9] = gender[1];

        // Output array
        float[][] output = new float[1][1];

        // Run inference
        tflite.run(input, output);
        Log.d("TFLiteModelPredictor", "predicted calories is" + output[0][0]);

        return output[0][0]; // Return the predicted calories
    }
}
