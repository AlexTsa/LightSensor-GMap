package com.alextsatsos.mysensormap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class TestActivity extends AppCompatActivity implements SensorEventListener {
   private EditText titleText, snippetText;  // ορισμός δυο EditText
    private Spinner spinner;    // ορισμος Spinner
    public  FirebaseFirestore db;       // ορισμός αντικειμένου της τάξης FirebaseFirestore
    private SensorManager sensorManager;  // ορισμός ενος Sensor manager
    private Sensor lightSensor;  //ορισμος αντικεμένου της τάξης sensor
    private double sensorValue;  //ορισμος μιας double μεταβλητής
    private TextView measurementResult; // ορισμος TextView

    private double longitude;  //ορισμος μιας double μεταβλητής
    private double latitude;  //ορισμος μιας double μεταβλητής
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        db = FirebaseFirestore.getInstance();  //αρχικοποίηση της FirebaseFirestore και με την μέθοδο  getInstance κανούμε την σύνδεση με το project(My project ) και το collection Marker
        sensorManager =(SensorManager)getSystemService(Context.SENSOR_SERVICE);  //ορισμός αντικειμέμου sensorManager και προσδιορισμό του αισθητήρα που έχει συσκευή χρησιμοποιοντας την μέθοδο getSystemService
        titleText = findViewById(R.id.titleText); // αντιστοιχήσει με στοιχειά που βρίσκονται activity_text.xml..
        snippetText = findViewById(R.id.snippetText);// ..
        spinner = findViewById(R.id.spinner);//..
        measurementResult = findViewById(R.id.measurementResult);//..
        Intent intent = getIntent();     // ανακτούμε την intent που ξεκίνησε την TestActivity με την getIntent()
        latitude = intent.getDoubleExtra("Latitude", 0);   //με την μέθοδο getDoubleExtra παιρνούμε μια double τιμή που προέρχεται απο την intent και την βαζούμε στην μεταβλήτη  latitude
        longitude = intent.getDoubleExtra("Longitude", 0); //με την μέθοδο getDoubleExtra παιρνούμε μια double τιμή που προέρχεται απο την intent και την βαζούμε στην μεταβλήτη  longitude
        titleText.setText(intent.getStringExtra("markerTxt"));   // με την μέθοδο getStringExtra παιρνούμε μια String τιμη  και την τοποθετούμε στο titleText
    }

    public void UploadToFireStore(View view) {
        String color = (String)spinner.getSelectedItem();  // Παίρνουμε στην String μεταβλητή color την τιμή που εχει επιλεγμένη το Spinner
        try {
            MarkerClass markerClass = new MarkerClass();   // Δημιουργία ενος αντικειμένου της Τάξης MarkerClass
            markerClass.setColor(color);   //με την μεθόδο setColor παιρνάμε στο marketClass το χρώμα που έχει το spinner
            markerClass.setSensorValue(Double.parseDouble(measurementResult.getText().toString())); //με την μεθόδο setSensorValue παιρνάμε στο marketClass την τιμη του αισθητήρα φωτός
            markerClass.setSnippet(snippetText.getText().toString());  //με την μέθοδο setSnippet παιρνάμε στο marketClass την τιμή για το snippet
            markerClass.setTitle(titleText.getText().toString()); // με την μέθοδο setTitle παιρναμε στο markerClass την τιμή για το title;
            markerClass.setGeoPoint(new GeoPoint(latitude, longitude)); //με την μέθοδο setGeoPoint δημιουργούμε ενα αντικείμενο Geopoint με τιμές τα κατάλληλα latitude, longitude και το  παιρνάμε στο marketClass
            db.collection("Marker")      // χρησιμοποιώντας την βαση db για αναφορά στο collection Marker
                    .document()     // ορισμος του auto-κλειδιού απο την firebasrfirestore
                    .set(markerClass)   // κάνουμε σετ το αντικείμενο marketClass με τις αντιστοιχές τιμές του
                    .addOnCompleteListener(new OnCompleteListener<Void>() { // Δημιουργία του OnCompleteListener για την εκτέλεση του set(marketClass) και εμφάνιζει μηνυμάτων toast,
                                                                           // αναλόγα με το εαν πηγε κάλα η καταχώρηση των δεδομένων ή οχι
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(TestActivity.this, "Everything is Fine", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TestActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();

                }
            });
        } catch (Exception e) {
            Toast.makeText(TestActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }


    }
    @Override
    protected  void onPause(){    //η onPause() μεθοδος τρέχει οταν η Activity σταματάει να ειναι στο προσκήνιο
        super.onPause();
        sensorManager.unregisterListener(this); //ακύρωση του EventListener για να μην παίρνει δεδομένα
    }
    @Override
    protected  void onResume(){  // οταν η Activity είναι στο προσκήνιο κανούμε register για το  φωτόμετρο τον EventListener
        super.onResume();
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // προσδιορισμός του προεπιλεγμένου φωτόμετρου με την μέθοδο getDefaultSensor()
        if(lightSensor != null) { //εαν υπάρχει light Sensor κανε register τον EventListener
            sensorManager.registerListener(this,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) { // όταν άλλαζουν τα δεδομένα του αισθητήρα καλείται η μεθόδος OnSensorChanged
       if(event.sensor.getType() == Sensor.TYPE_LIGHT){ // εαν ο τύπος του sensor είναι φωτόμετρο
           sensorValue = event.values[0];    //τότε παίρνουμε με την event.value[0] την τιμή του αισθητηρα και την βάζουμε στην μεταβλητή  sensorValue

       }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void takeMeasurement(View view) {   // Οταν πατηθεί το κουμπί με τίτλο Measurement τοτε κανούμε setText στο TextView measurementResult την τιμή της μεταβλητής sensorValue
        measurementResult.setText(String.valueOf(sensorValue));
    }
}
