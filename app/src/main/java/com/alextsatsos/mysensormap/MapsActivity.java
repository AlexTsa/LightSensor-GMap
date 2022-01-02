package com.alextsatsos.mysensormap;

import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

//κάνουμε implement OnMarkerClickListener για την διαχείριση των κλικ στους μαρκερς
//κάνουμε implement OnMapReadyCallback interface το οποιο καλεί την μέθοδο onMapReady για όταν ο χάρτης είναι έτοιμος για χρήση
public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    private double latitude; //ορισμός double μεταβλητής
    private double longitute; //ορισμός double μεταβλητής
    private GoogleMap mMap; // ορισμός αντικειμένου της τάξης googleMap
    private String markerTxt;  // ορισμός String μεταβλητής
    LocationManager locationManager;  // Ορισμός αντικειμένου της κλασής LocationManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);    //Δημιουργία του fragment
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); // αρχικοποίηση αντικειμένου LocationManger με κλήση της μεθόδου getSystemService με παράμετρο την υπερεσία εντοπισμού της συσκευής

        //Ελεγχος για τα permission granted
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) { //έλεγχος της κατάστασης του provider εαν έχει ενεργοποιηθει απο τον Network_provider

            //έλαχιστο χρονικό διάστηματα ανανέωσης της θέσης ,ελάχιστη απόσταση για την ανανέωση της θέσης, Listener για τον εντοπισμό της καινούργια θέσης
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() { // χρησιμοποιούμε τον LocationManager για ζητήσουμε την νεα Θέση με τον NetWork_provider
                @Override
                public void onLocationChanged(Location location) {    //η μέθοδος onLocationChanged μας παρέχει την θέση της συσκευής
                    latitude = location.getLatitude();  //παίρνουμε το γεωγραφικό πλάτος
                    longitute = location.getLongitude(); //παίρνουμε το γεωγραφικό μήκος

                    LatLng latLng = new LatLng(latitude, longitute); //Δημιουργία αντικείμενο latLng της Τάξης  LatLng με τις τιμες latitude, longitude
                    Geocoder geocoder = new Geocoder(getApplicationContext());  // Δημιουργία αντικειμένου της Τάξης  Geocoder
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitute, 1); //Δημιουγία μιας λιστας με διευθυνσείς με βάση latitude, longitude
                        String locality = addressList.get(0).getLocality();   //παιρνούμε την περιοχή
                        String countryname = addressList.get(0).getCountryName(); //παιρουμε το ονομα της χώρας
                        markerTxt = locality + " , " + countryname;
                        mMap.addMarker(new MarkerOptions().position(latLng).title(markerTxt));  // με το αντικείμενο mMap προσθέτουμε ενα marker με θεση το  αντικείμενο latLng και τιτλο το markerTxt
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.2f));   //εστιασμός της κάμερας στο σημείο latLng  και ζουμ Χ 10
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { //η μέθοδος onStatusChanged καλείται για να μας δώσει πληροφορίες για την κατάσταση την συνδέσης  του παρέχου

                }

                @Override
                public void onProviderEnabled(String provider) { //η μέθοδος onProviderEnabled καλείται οταν ο provider ενεργοποιηθεί απο τον χρήστη

                }

                @Override
                public void onProviderDisabled(String provider) { //η μέθοδος onProviderDisabled καλείται οταν ο provider απενεργοποιηθεί απο τον χρήστη

                }
            });

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { //έλεγχος της κατάστασης του provider εαν έχει ενεργοποιηθεί απο τον GPS_PROVIDER
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() { // χρησιμοποιούμε τον LacationManager για ζητήσουμε την νεα Θέση με τον GPS_PROVIDER
                    @Override
                    public void onLocationChanged(Location location) {
                        double latitude = location.getLatitude();
                        double longitute = location.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitute);
                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(latitude, longitute, 1);
                            String locality = addressList.get(0).getLocality();
                            String countryname = addressList.get(0).getCountryName();
                            markerTxt = locality + " , " + countryname;
                            mMap.addMarker(new MarkerOptions().position(latLng).title(markerTxt));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.2f));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
            }
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);  //ορισμός ενός Click Listener για τους markers

    }

    @Override
    public boolean onMarkerClick(Marker marker) { // οταν γίνει κλικ σε ενα Μαρκερ
        Intent intent = new Intent(MapsActivity.this, TestActivity.class); //κανούμε intent στην TestActivity
        intent.putExtra("Latitude", latitude);  // προσθέτουμε πληροφορίες στην intent με ονομα Latitude και τιμή την τιμή της μεταβλητής latitude
        intent.putExtra("Longitude", longitute); // προσθέτουμε πληροφορίες στην intent με ονομα Longitude και τιμή την τιμή της μεταβλητής longitute
        intent.putExtra("markerTxt", markerTxt);// προσθέτουμε πληροφορίες στην intent με ονομα markerText και τιμή την τιμή της μεταβλητής markerText
        startActivity(intent);  // Ξεκινάμε την intent
        return false;

    }

}
