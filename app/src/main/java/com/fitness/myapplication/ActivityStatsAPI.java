package com.fitness.myapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// import okhttp3.FormBody;
// import okhttp3.OkHttpClient;
// import okhttp3.Request;
// import okhttp3.RequestBody;
// import okhttp3.Response;

public class ActivityStatsAPI {
    // OkHttpClient httpClient = new OkHttpClient();
    // private final String stravaUrl = "https://www.strava.com/api/v3";
    // private String accessToken = null;
    // private long athleteId;

    public ActivityStatsAPI() {

    }

    // private JSONArray getActivities(long startTime, long endTime) {
    //     RequestBody requestBody = new FormBody.Builder()
    //             .addEncoded("before", String.valueOf(endTime * 1000L))
    //             .addEncoded("after", String.valueOf(startTime * 1000L))
    //             .addEncoded("per_page", "100")
    //             .build();

    //     Request request = new Request.Builder()
    //             .url(stravaUrl + "/athlete/activities")
    //             .addHeader("Authorization", "Bearer " + accessToken)
    //             .get()
    //             .build();

    //     try (Response response = httpClient.newCall(request).execute()) {
    //         if (!response.isSuccessful())
    //             throw new IOException("List Activities Failed: " + response.body().string());

    //         return new JSONArray(response.body().string());

    //     } catch (IOException | JSONException e) {
    //         throw new RuntimeException(e);
    //     }
    // }

    // public List<Float> getAvgHeartRate(List<Pair<Integer, Integer>> timeIntervals) throws JSONException {
    //     timeIntervals = timeIntervals
    //             .stream()
    //             .sorted((v1, v2) -> v1.first.compareTo(v2.first))
    //             .collect(Collectors.toList());
    //     Integer startTime = timeIntervals.get(0).first - 3600;
    //     Integer endTime = timeIntervals.get(timeIntervals.size() - 1).second + 3600;

    //     JSONArray activities = getActivities(startTime, endTime);
    //     List<Long> activityForInterval = new ArrayList<>();
    //     Map<Long, Long> activityStartTimes = new HashMap<>();
    //     Map<Long, Long> activityEndTimes = new HashMap<>();

    //     for (int i = 0; i < timeIntervals.size(); i++) {
    //         Pair<Integer, Integer> interval = timeIntervals.get(i);
    //         activityForInterval.add(0L);

    //         for (int j = activities.length() - 1; j >= 0; j--) {
    //             JSONObject activityJson = activities.getJSONObject(j);
    //             Long activityStartTime = OffsetDateTime.parse(activityJson.getString("start_date")).toEpochSecond();
    //             Long activityEndTime = activityStartTime + activityJson.getInt("elapsed_time");

    //             if (interval.second < activityStartTime || interval.first > activityEndTime)
    //                 continue;

    //             Long activityId = activityJson.getLong("id");
    //             activityForInterval.set(i, activityId);
    //             activityStartTimes.putIfAbsent(activityId, activityStartTime);
    //             activityEndTimes.putIfAbsent(activityId, activityEndTime);
    //             break;
    //         }
    //     }

    //     Map<Long, JSONObject> activityStreamsById = new HashMap<>();
    //     List<Float> avgHeartRates = new ArrayList<>();
    //     for(int i = 0; i < timeIntervals.size(); i++) {
    //         Long activityId = activityForInterval.get(i);
    //         Pair<Integer, Integer> interval = timeIntervals.get(i);

    //         if (activityId == 0) {
    //             avgHeartRates.add(0.0f);
    //         } else {
    //             activityStreamsById.putIfAbsent(activityId, getHeartRateStream(activityId));
    //             JSONObject activity = activityStreamsById.get(activityId);
    //             JSONArray heartBeats = activity.getJSONObject("heartrate").getJSONArray("data");
    //             JSONArray times = activity.getJSONObject("time").getJSONArray("data");
    //             Long activityStartTime = activityStartTimes.get(activityId);
    //             int heartBeatsCount = 0;
    //             float heartBeatsAvg = 0.0f;

    //             for (int j = interval.first; j <= interval.second; j++) {
    //                 for (int k = 0; k < times.length(); k++) {
    //                     if (activityStartTime + times.getInt(k) == j) {
    //                         heartBeatsAvg += heartBeats.getInt(k);
    //                         heartBeatsCount += 1;
    //                     }
    //                 }
    //             }

    //             heartBeatsAvg = heartBeatsAvg / heartBeatsCount;
    //             avgHeartRates.add(heartBeatsAvg);
    //             Log.i("HeartRateCalculation", "Calculated avgHeartRate for interval " + i + ": " + avgHeartRates.get(i));
    //         }
    //     }

    //     return avgHeartRates;

    // }

    // private JSONObject getHeartRateStream(Long activityId) {
    //     String url = Uri.parse(stravaUrl + "/activities/" + activityId + "/streams")
    //             .buildUpon()
    //             .appendQueryParameter("key_by_type", "true")
    //             .appendQueryParameter("keys", "heartrate")
    //             .build().toString();

    //     Request request = new Request.Builder()
    //             .url(url)
    //             .addHeader("Authorization", "Bearer " + accessToken)
    //             .addHeader("ContentType", "application/x-www-form-urlencoded")
    //             .get()
    //             .build();

    //     JSONObject heartRateStream;
    //     try (Response response = httpClient.newCall(request).execute()) {
    //         if (!response.isSuccessful())
    //             throw new IOException("Get Activity Stream Failed: " + response.body().string());

    //         heartRateStream = new JSONObject(response.body().string());
    //     } catch (IOException | JSONException e) {
    //         throw new RuntimeException(e);
    //     }

    //     return heartRateStream;
    // }

    public static List<Float> getAvgBodyTemperatures(Context context, List<Pair<Integer, Integer>> timeIntervals) {
        List<Float> avgTemperatures = new ArrayList<>();
        String CSV_FILE_NAME = "body_temperature_data.csv";

        try {
            // Get InputStream from assets
            InputStream inputStream = getInputStreamFromAsset(context, CSV_FILE_NAME);

            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String csvLine;
                List<Pair<Long, Float>> temperatureData = new ArrayList<>();

                // Read and parse the CSV file
                while ((csvLine = reader.readLine()) != null) {
                    String[] row = csvLine.split(",");
                    if (row.length == 2) {
                        Long timestamp = Long.parseLong(row[0]);
                        Float temperature = Float.parseFloat(row[1]);
                        temperatureData.add(new Pair<>(timestamp, temperature));
                    }
                }

                // Calculate average temperatures for each time interval
                for (Pair<Integer, Integer> interval : timeIntervals) {
                    float sum = 0;
                    int count = 0;

                    for (Pair<Long, Float> data : temperatureData) {
                        if (data.first >= interval.first && data.first <= interval.second) {
                            sum += data.second;
                            count++;
                        }
                    }

                    if (count > 0) {
                        avgTemperatures.add(sum / count);
                    } else {
                        avgTemperatures.add(0.0f); // Handle the case where no data is found in the interval
                    }
                }

                // Close the reader
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return avgTemperatures;
    }

    public static List<Float> getAvgHeartRateValues(Context context, List<Pair<Integer, Integer>> timeIntervals) {
        List<Float> avgHeartRates = new ArrayList<>();
        String CSV_FILE_NAME = "heart_rate_data.csv";

        try {
            // Get InputStream from assets
            InputStream inputStream = getInputStreamFromAsset(context, CSV_FILE_NAME);

            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String csvLine;
                List<Pair<Long, Float>> heartRateData = new ArrayList<>();

                // Read and parse the CSV file
                while ((csvLine = reader.readLine()) != null) {
                    String[] row = csvLine.split(",");
                    if (row.length == 2) {
                        Long timestamp = Long.parseLong(row[0]);
                        Float heartRate = Float.parseFloat(row[1]);
                        heartRateData.add(new Pair<>(timestamp, heartRate));
                    }
                }

                // Calculate average heart rates for each time interval
                for (Pair<Integer, Integer> interval : timeIntervals) {
                    float sum = 0;
                    int count = 0;

                    for (Pair<Long, Float> data : heartRateData) {
                        if (data.first >= interval.first && data.first <= interval.second) {
                            sum += data.second;
                            count++;
                        }
                    }

                    if (count > 0) {
                        avgHeartRates.add(sum / count);
                    } else {
                        avgHeartRates.add(0.0f); // Handle the case where no data is found in the interval
                    }
                }

                // Close the reader
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return avgHeartRates;
    }


    // Utility method to get InputStream from assets
     private static InputStream getInputStreamFromAsset(Context context, String fileName) {
         try {
             AssetManager assetManager = context.getAssets();
             return assetManager.open(fileName);
         } catch (IOException e) {
             e.printStackTrace();
             return null;
         }
     }

    // public void createStravaClient(String code) {
    //     RequestBody requestBody = new FormBody.Builder()
    //             .addEncoded("client_id", "117349")
    //             .addEncoded("client_secret", "78b6cb5075e259a4b9734ca0825812ea41ca789f")
    //             .addEncoded("code", code)
    //             .addEncoded("grant_type", "authorization_code")
    //             .build();

    //     Request request = new Request.Builder()
    //             .url(stravaUrl + "/oauth/token")
    //             .addHeader("ContentType", "application/x-www-form-urlencoded")
    //             .post(requestBody)
    //             .build();

    //     try (Response response = httpClient.newCall(request).execute()) {
    //         if (!response.isSuccessful()) throw new IOException("Refresh token failed");

    //         JSONObject responseJson = new JSONObject(response.body().string());
    //         accessToken = responseJson.getString("access_token");
    //         athleteId = responseJson.getJSONObject("athlete").getLong("id");
    //         Log.i("API", "Access Token: " + accessToken + " Athlere Id: " + athleteId);
    //     } catch (IOException | JSONException e) {
    //         throw new RuntimeException(e);
    //     }
    // }

    // public String getAccessToken() {
    //     return accessToken;
    // }
}
