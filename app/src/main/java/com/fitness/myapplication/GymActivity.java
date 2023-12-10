package com.fitness.myapplication;
import android.util.Log;
import android.Manifest;
import java.util.HashMap;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GymActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap googleMap;
    private LatLng currentLatLng;
    private RecyclerView recyclerView;
    private GymAdapter gymAdapter;
    private HashMap<Gym, Marker> markerHashMap = new HashMap<>();
    private double userLat;
    private double userLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Check if both FINE and COARSE location permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            // If permissions are not granted, request them
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            userLat = location.getLatitude();
                            userLng = location.getLongitude();
                            LatLng currentLatLng = new LatLng(userLat, userLng);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));

                            // Fetch nearby gyms based on the obtained location
                            fetchNearbyGyms(userLat, userLng);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void fetchNearbyGyms(double latitude, double longitude) {
        // Replace "YOUR_API_KEY" with your actual Google Places API key
        String apiKey = "AIzaSyD98W3nFLRSUG0G7nodfzg-W4qIYsPfwD8";
        String placesApiUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + latitude + "," + longitude +
                "&radius=15000" +
                "&types=gym" +
                "&key=" + apiKey;

        // Use the PlacesApiRequestTask to fetch nearby gyms
        new PlacesApiRequestTask().execute(placesApiUrl);
    }

    // This method is called when the map is ready to be used
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

    }

    // This method is called when the PlacesApiRequestTask completes
    private void updateUI(List<Gym> gyms) {
        if (gyms != null) {
            gymAdapter = new GymAdapter(gyms, new GymAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Gym gym) {
                    highlightMarkerForGym(gym);
                }
            });
            recyclerView.setAdapter(gymAdapter);

            for (Gym gym : gyms) {
                if (gym != null) {
                    LatLng gymLatLng = new LatLng(gym.getLatitude(), gym.getLongitude());

                    // Add a null check for gymLatLng
                    if (gymLatLng != null) {
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(gymLatLng)
                                .title(gym.getName())
                                .snippet(gym.getAddress());

                        Marker marker = googleMap.addMarker(markerOptions);
                        marker.setTag(gym);

                        // Add the marker to the markerHashMap
                        markerHashMap.put(gym, marker);
                    } else {
                        Log.e("updateUI", "gymLatLng is null for gym: " + gym.getName());
                    }
                } else {
                    Log.e("updateUI", "gym is null");
                }
            }
        } else {
            Log.e("updateUI", "gyms is null");
        }
    }


    private void highlightMarkerForGym(Gym gym) {
        // Find the marker associated with the selected gym using markerHashMap
        Marker marker = markerHashMap.get(gym);

        // Check if the marker is found and update the UI accordingly
        if (marker != null) {
            // Move camera to the marker's position and zoom in
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));

            // Highlight the marker (you can change the marker color or icon)
            marker.showInfoWindow();
        }
    }

    private Marker findMarkerForGym(Gym gym) {
        for (Marker marker : markerHashMap.values()) {
            if (marker.getTag() instanceof Gym) {
                Gym markerGym = (Gym) marker.getTag();
                if (markerGym != null && markerGym.equals(gym)) {
                    return marker;
                }
            }
        }
        return null;
    }

    // Override onRequestPermissionsResult to handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Both permissions granted, retry getting the current location
                getCurrentLocation();
            } else {
                // Permission denied, handle accordingly (e.g., inform the user)
            }
        }
    }

    // AsyncTask to handle the Google Places API request
    private class PlacesApiRequestTask extends AsyncTask<String, Void, List<Gym>> {

        @Override
        protected List<Gym> doInBackground(String... urls) {
            if (urls.length == 0) {
                return null;
            }

            String apiUrl = urls[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream inputStream = urlConnection.getInputStream();
                    Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                    boolean hasInput = scanner.hasNext();
                    if (hasInput) {
                        String jsonResponse = scanner.next();
                        return parsePlacesApiResponse(jsonResponse);
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Gym> gyms) {
            updateUI(gyms);
        }

        private List<Gym> parsePlacesApiResponse(String jsonResponse) {
            List<Gym> gyms = new ArrayList<>();
            try {
                JSONObject rootObject = new JSONObject(jsonResponse);
                JSONArray resultsArray = rootObject.getJSONArray("results");
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject placeObject = resultsArray.getJSONObject(i);

                    String name = placeObject.getString("name");
                    String address = placeObject.getString("vicinity");

                    JSONObject geometryObject = placeObject.getJSONObject("geometry");
                    JSONObject locationObject = geometryObject.getJSONObject("location");
                    double lat = locationObject.getDouble("lat");
                    double lng = locationObject.getDouble("lng");
                    float distance = calculateDistance(userLat, userLng, lat, lng);
                    gyms.add(new Gym(name, address, lat, lng,distance));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return gyms;
        }

        private float calculateDistance(double userLat, double userLng, double gymLat, double gymLng) {
            // Radius of the Earth in meters
            final double R = 6371000;

            double dLat = Math.toRadians(gymLat - userLat);
            double dLng = Math.toRadians(gymLng - userLng);

            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(gymLat)) *
                            Math.sin(dLng / 2) * Math.sin(dLng / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            // Distance in meters
            double distance = R * c;

            return (float) distance/1000;
        }
    }
}