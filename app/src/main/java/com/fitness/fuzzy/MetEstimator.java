package com.fitness.fuzzy;

import android.content.Context;
import android.content.res.AssetManager;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

import org.antlr.runtime.RecognitionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MetEstimator {
    private static final String FUZZY_FILE_NAME = "METFuzzy.fcl";
    FIS fis;
    // Enum for Intensity
    enum Intensity {
        LOW(1), MODERATE(2), HIGH(3);

        private final int value;
        Intensity(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Intensity fromString(String text) {
            for (Intensity b : Intensity.values()) {
                if (b.name().equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null; // or throw an exception
        }
    }

    // Enum for Difficulty
    enum Difficulty {
        BEGINEER(1), INTERMEDIATE(2), EXPERT(3);

        private final int value;
        Difficulty(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Difficulty fromString(String text) {
            for (Difficulty b : Difficulty.values()) {
                if (b.name().equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null; // or throw an exception
        }
    }

    public MetEstimator(Context context){
        this.fis = loadFISFile(context);
    }

    private FIS loadFISFile(Context context){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(FUZZY_FILE_NAME)));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            reader.close();
            return FIS.createFromString(stringBuilder.toString(), true);
        }
        catch (IOException | RecognitionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public double getMetScore(String intensity, String difficulty){
        FunctionBlock functionBlock = fis.getFunctionBlock(null);

        // Set inputs using enums
        functionBlock.setVariable("intensity", Intensity.fromString(intensity).getValue());
        functionBlock.setVariable("difficulty", Difficulty.fromString(difficulty).getValue());

        // Evaluate
        functionBlock.evaluate();

        return functionBlock.getVariable("met").getValue();
    }


}
