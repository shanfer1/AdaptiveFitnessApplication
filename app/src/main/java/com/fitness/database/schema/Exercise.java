package com.fitness.database.schema;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "Exercise", foreignKeys = {
        @ForeignKey(entity = MuscleGroup.class, parentColumns = "muscleGroupID", childColumns = "muscle_id", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Equipment.class, parentColumns = "equipmentId", childColumns = "equipment_id", onDelete = ForeignKey.CASCADE)
            }
        )
public class Exercise implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name="description")
    public String description;

    @ColumnInfo(name="type")
    public String type;

    @ColumnInfo(name="muscle_id")
    public int muscleId;

    @ColumnInfo(name="equipment_id")
    public int equipmentID;

    @ColumnInfo(name="rating")
    public double rating;

    @ColumnInfo(name="metScore")
    public double metScore;

    @ColumnInfo(name="weightFactor")
    public double weightFactor;

    @ColumnInfo(name = "Difficulty")
    @TypeConverters(DifficultyLevelConverter.class)
    public DifficultyLevel difficultyLevel;

    public Exercise(String name, String description, String type, DifficultyLevel difficultyLevel,
                    int muscleId, int equipmentID, double rating, double metScore, double weightFactor){
        this.name = name;
        this.description = description;
        this.type = type;
        this.difficultyLevel = difficultyLevel;
        this.muscleId = muscleId;
        this.equipmentID = equipmentID;
        this.rating = rating;
        this.metScore = metScore;
        this.weightFactor = weightFactor;
    }

    public Exercise(){
        this.name = "Exercise";
        this.description = "BASE DESCRIPTION";
        this.type = "Cardio";
        this.difficultyLevel = DifficultyLevel.BEGINEER;
    }

    public String getName(){ return this.name; }

    public void setName(String name){ this.name = name; }

    public String getDescription(){ return this.description; }

    public void setDescription(String description){ this.description = description; }

    public String getType(){ return this.type; }

    public void setType(String type){ this.type = type; }

    public int getMuscleId(){ return this.muscleId; }

    public void setMuscleId(int muscleId){ this.muscleId = muscleId; }

    public int getEquipmentID(){ return this.equipmentID; }

    public void setEquipmentID(int equipmentID){ this.equipmentID = equipmentID; }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public double getRating(){return this.rating;}

    public void setRating(double rating) { this.rating = rating; }

    public double getMetScore(){return this.metScore;}

    public void setMetScore(double score) { this.metScore = score; }

    public double getWeightFactor(){return this.weightFactor;}

    public void setWeightFactor(double weightFactor) { this.weightFactor = weightFactor; }

    protected Exercise(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        type = in.readString();
        muscleId = in.readInt();
        equipmentID = in.readInt();
        rating = in.readDouble();
        metScore = in.readDouble();
        weightFactor = in.readDouble();
        difficultyLevel = DifficultyLevel.valueOf(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeInt(muscleId);
        dest.writeInt(equipmentID);
        dest.writeDouble(rating);
        dest.writeDouble(metScore);
        dest.writeDouble(weightFactor);
        dest.writeString(difficultyLevel.name());
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };
}
