package com.example.rohithkumar.cameramapsapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MyMapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final int PLACE_PICKER_REQUEST = 3;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPlacePicker();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        final LatLng umkcPosition = new LatLng(39.03477389, -94.57705747);
        mMap.addMarker(new MarkerOptions().position(umkcPosition).title("UMKC"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(umkcPosition, 12));
    }

    private void setUpMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        //Locate my location
        mMap.setMyLocationEnabled(true);
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
                placeMarkerOnMap(currentLocation);
            }
        }
    }

    protected void placeMarkerOnMap(final LatLng location) {
        final MarkerOptions markerOptions = new MarkerOptions().position(location);
        final String titleStr = getAddress(location);

        markerOptions.title(titleStr);
        mMap.addMarker(markerOptions);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                final Place place = PlacePicker.getPlace(this, data);

                placeMarkerOnMap(place.getLatLng());
            }
        }
    }

    private void loadPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(MyMapsActivity.this), PLACE_PICKER_REQUEST);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private String getAddress(final LatLng Location) {

        String myaddress = "";
        final Geocoder geocoder = new Geocoder(MyMapsActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(Location.latitude, Location.longitude, 1);
            myaddress = addresses.get(0).getAddressLine(0);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return myaddress;
    }
}