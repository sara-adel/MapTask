package com.sara.maptask;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Marker firstMarker = null, secondMarker = null;
    boolean swap;
    private Button btnGo;
    Polyline line;

    boolean permission = false;
    private static final String[] LOCATION_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btnGo = findViewById(R.id.go);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CheckGooglePlay();

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 line = mMap.addPolyline(new PolylineOptions()
                        .add(firstMarker.getPosition() , secondMarker.getPosition())
                        .width(10)
                        .color(Color.RED));
            }
        });
    }

    private void CheckGooglePlay() {
        GooglePlayServicesManager.checkPlayServices(this);
        if (GooglePlayServicesManager.checkPlayServices(this)) {
            System.out.println("1");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
            } else {
                permission = true;
                mapFragment.getMapAsync(this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                MarkerOptions options = new MarkerOptions();

                if (firstMarker == null) {
                    options.position(latLng);
                    firstMarker = mMap.addMarker(options);
                    firstMarker.showInfoWindow();
                    firstMarker.setPosition(latLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
                    return;
                }

                if (secondMarker == null) {
                    options.position(latLng);
                    secondMarker = mMap.addMarker(options);
                    secondMarker.showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
                    swap =true;
                    return;
                }

                if (swap){
                    secondMarker.remove();
                    line.remove();
                }
                options.position(latLng);
                secondMarker = mMap.addMarker(options);
                secondMarker.showInfoWindow();

//                Polyline line = mMap.addPolyline(new PolylineOptions()
//                        .add(firstMarker.getPosition() , secondMarker.getPosition())
//                        .width(10)
//                        .color(Color.RED));

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (swap){
                    secondMarker.remove();
                }
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                secondMarker = mMap.addMarker(options);
                secondMarker.showInfoWindow();
                //secondMarker.setPosition(latLng);
            }
        });
    }
}
