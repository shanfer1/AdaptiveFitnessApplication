package com.fitness.myapplication;

import android.content.res.AssetFileDescriptor;
import android.content.Context;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class CaloriesBurntPredictor {

    private Interpreter tflite;
    private Context context;

    public CaloriesBurntPredictor(Context context) {
        this.context = context;
        loadModel();
    }

    private void loadModel() {
        try {
            Interpreter.Options options = new Interpreter.Options();
            options.setNumThreads(5); // You can tune this as needed

            AssetFileDescriptor fileDescriptor = context.getAssets().openFd("calories-model.tflite");
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();

            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
            tflite = new Interpreter(buffer, options);
        } catch (Exception e) {
            throw new RuntimeException("Error loading TensorFlow Lite model.", e);
        }
    }

    public float predictCalories(String gender, float age, float height, float weight, float duration, float heartRate, float temperature) {
        float[][] inputData = prepareInputData(gender, age, height, weight, duration, heartRate, temperature);
        float[][] output = new float[1][1];

        tflite.run(inputData, output);

        return output[0][0]; // Return the predicted calories burnt
    }

    private float[][] prepareInputData(String gender, float age, float height, float weight, float duration, float heartRate, float temperature) {
        float[][] input = new float[1][7];
        input[0][0] = gender.equalsIgnoreCase("male") ? 0 : 1;
        input[0][1] = age;
        input[0][2] = height;
        input[0][3] = weight;
        input[0][4] = duration;    // Duration of workout
        input[0][5] = heartRate;   // Average heart rate
        input[0][6] = temperature; // Body temperature

        return input;
    }

    public void close() {
        tflite.close();
    }
}
