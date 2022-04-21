package com.example.connectionproject;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    TextView txt, txt1, txt2, txt3, txt4,x,y,z;
    LocationManager locationManager;
    String provider;
    Button btn;
    FusedLocationProviderClient fusedLocationProviderClient;
SensorManager sensorManager;
Context context=this;
Sensor sensor;
    SensorTrack track;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION}, 44
                    );
                }
            }

            private void getLocation() {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {

                            try {
                                Geocoder geocoder = new Geocoder(MainActivity.this,
                                        Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1
                                );
                                txt.setText(Html.fromHtml(
                                        "<font color=#6200EE><b>Latitude :</b><br></font>" +
                                                addresses.get(0).getLatitude()
                                ));
                                txt1.setText(Html.fromHtml(
                                        "<font color=#6200EE><b>Longitude :</b><br></font>" +
                                                addresses.get(0).getLongitude()
                                ));
                                txt4.setText(Html.fromHtml(
                                        "<font color=#6200EE><b>Country :</b><br></font>" +
                                                addresses.get(0).getCountryName()
                                ));
                                txt2.setText(Html.fromHtml(
                                        "<font color=#6200EE><b>Address :</b><br></font>" +
                                                addresses.get(0).getAddressLine(0)
                                ));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
    }
});
    }
    public void init(){
        txt = findViewById(R.id.txt);
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt4 = findViewById(R.id.txt4);
        x=findViewById(R.id.x);
        y=findViewById(R.id.y);
        z=findViewById(R.id.z);
        btn = findViewById(R.id.button);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        track=new SensorTrack();
        sensorManager.registerListener(track,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(track);
    }

    public class SensorTrack implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float sensor_data[]= sensorEvent.values;
            x.setText("X: "+sensor_data[0]);
            y.setText("Y: "+sensor_data[1]);
            z.setText("Z: "+sensor_data[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
switch (i){
    case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
        Log.i("onAccuracyChanged","Yüksek doğruluk :"+i);
        break;
    case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
        Log.i("onAccuracyChanged","Orta doğruluk :"+i);
        break;
    case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
        Log.i("onAccuracyChanged","Düşük doğruluk :"+i);
        break;
}
        }
    }
}