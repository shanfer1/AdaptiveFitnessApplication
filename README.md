# Adaptive Fitness Guide Application

## Introduction
The Adaptive Fitness Guide Application is a cutting-edge fitness platform that combines advanced computing with user-specific data to create tailored fitness routines. Designed with a user-centric approach, this application offers personalized fitness solutions by considering individual preferences and physical conditions. This guide will help you set up and understand the application's functionalities.

## Features
- **DataStore Creation and Pre-population**: Creation of datastore and populating datastore with all required dataset after installation
- **Account Management**: Secure login and account creation for managing personal data.
- **Personal Details Input**: Users can input vital information like age, weight, and height for personalized fitness plans.
- **Workout Preferences**: Customizable workout settings based on environment, goals, and available equipment.
- **Profile Manager**: Allows users to update their details to keep fitness recommendations relevant.
- **Calorie Prediction**: Utilizes a Neural Network to predict calorie burn, enhancing workout effectiveness.
- **Workout Recommendation**: The workout recommendation manager serves as the heartbeat of the system rolling out a weekly regime of exercises that are to be performed for users

## Getting Started

### Prerequisites
Ensure you have the following installed:
- Android Studio
- Java Development Kit (JDK)
- An Android emulator or a physical Android device
### Needed for pre-population of database -- ensure files are there
- jFuzzyLogic jar (added in libs folder of project -- ensure it is there)
- Ensure ExerciseDataset.csv and METFuzzy.fcl are present in assets folder (src/main/assets).

### Installation
1. **Clone the Repository**:  

git clone <url>

2. **Open the Project**:  
Open Android Studio and select 'Open an Existing Project', then navigate to your cloned repository.

3. **Sync Gradle**:  
Allow Android Studio to sync the project with Gradle files. This might take a few minutes.

4. **Run the Application**:  
Choose an emulator or connect your Android device and run the application.

## Testing
- **Unit Testing**:  
Unit tests are written for individual components. Run them via Android Studio to ensure component integrity.
- **Integration Testing**:  
Using Espresso for UI testing and end-to-end application functionality.


