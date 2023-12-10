import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
import tensorflow as tf

calories = pd.read_csv('data/calories.csv')
exercise_data = pd.read_csv('data/exercise.csv')

calories_data = pd.concat([exercise_data, calories['Calories']], axis=1)
calories_data.replace({"Gender":{'male':0,'female':1}}, inplace=True)

X = calories_data.drop(columns=['User_ID','Calories'], axis=1)
Y = calories_data['Calories']

X_train, X_test, Y_train, Y_test = train_test_split(X, Y, test_size=0.2, random_state=2)

model = tf.keras.Sequential([
    tf.keras.layers.Dense(1, input_shape=(X_train.shape[1],))
])

model.compile(optimizer='adam', loss='mean_squared_error')
model.fit(X_train, Y_train, epochs=100)

# print('Training model')
model.fit(X_train, Y_train)
test_data_prediction = model.predict(X_test)

model.save('model/calories-model-tf')

converter = tf.lite.TFLiteConverter.from_saved_model('model/calories-model-tf')
tflite_model = converter.convert()

# Save the model
with open('./../app/src/main/assets/calories-model.tflite', 'wb') as f:
    f.write(tflite_model)

# # Test
# interpreter = tf.lite.Interpreter(model_path="./../app/src/main/assets/calories-model.tflite")
# interpreter.allocate_tensors()

# # Get input and output tensors
# input_details = interpreter.get_input_details()
# output_details = interpreter.get_output_details()

# test_in = [[0, 68, 190.0, 94.0, 29.0, 105.0, 40.8]]

# input_data = np.array(test_in, dtype=np.float32)
# interpreter.set_tensor(input_details[0]['index'], input_data)
# interpreter.invoke()

# output_data = interpreter.get_tensor(output_details[0]['index'])
# print(output_data)