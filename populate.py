import pandas as pd

# Mapping from exercise number to new exercise category
exercise_mapping = {
    'Exercise 4': 'Boxing, Running',
    'Exercise 3': 'Sports, Skipping',
    'Exercise 7': 'Sports, Skipping',
    'Exercise 8': 'Gym (Mix of Weight Training and Cardio)',
    'Exercise 10': 'Swimming, Cycling',
    'Exercise 9': 'Swimming, Cycling',
    'Exercise 5': 'Swimming, Cycling',
    'Exercise 6': 'Swimming, Cycling',
    'Exercise 2': 'Swimming, Cycling',
    'Exercise 1': 'Walking, Yoga'
}

def transform_exercise_column(file_path):
    # Read the CSV file
    df = pd.read_csv(file_path)

    # Transform the 'Exercise' column based on the mapping
    df['Exercise'] = df['Exercise'].map(exercise_mapping)

    return df

# Example usage:
df_transformed = transform_exercise_column("exercise_dataset.csv")
df_transformed.to_csv("transformed__excercise_dataset.csv", index=False)
