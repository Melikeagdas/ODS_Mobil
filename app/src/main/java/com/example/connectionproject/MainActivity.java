package com.example.connectionproject;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
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


public class MainActivity extends AppCompatActivity implements MainActivitys {
    TextView txt, txt1, txt2, txt3, txt4, x, y, z, x2, y2, z2, z5,z6,z7;
    LocationManager locationManager;
    String provider;
    Button btn;
    FusedLocationProviderClient fusedLocationProviderClient;
    SensorManager sensorManager;
    SensorManager sensorManager2;
    Context context = this;
    Sensor sensor;
    SensorTrack track;
    Sensor sensor2;
    MediaPlayer play;

    public static final int UPSIDE_DOWN = 3;
    public static final int LANDSCAPE_RIGHT = 4;
    public static final int PORTRAIT = 1;
    public static final int LANDSCAPE_LEFT = 2;
    public int mOrientationDeg; //last rotation in degrees
    public int mOrientationRounded; //last orientation int from above
    private static final int _DATA_X = 0;
    private static final int _DATA_Y = 1;
    private static final int _DATA_Z = 2;
    private int ORIENTATION_UNKNOWN = -1;
    public int tempOrientRounded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        txt.setVisibility(View.INVISIBLE);
        txt1.setVisibility(View.INVISIBLE);
        txt2.setVisibility(View.INVISIBLE);
        txt4.setVisibility(View.INVISIBLE);
    }

    public void init() {
        txt = findViewById(R.id.txt);
        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt4 = findViewById(R.id.txt4);
        x = findViewById(R.id.x);
        y = findViewById(R.id.y);
        z = findViewById(R.id.z);
        x2 = findViewById(R.id.x2);
        y2 = findViewById(R.id.y2);
        z2 = findViewById(R.id.z2);
        z5 = findViewById(R.id.z5);
        z6 = findViewById(R.id.z6);
        z7 = findViewById(R.id.z7);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        track = new SensorTrack();
        sensorManager.registerListener(track, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager2 = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor2 = sensorManager2.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.sensorManager2 = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        this.sensor2 = this.sensorManager2.getDefaultSensor(4);

        Sensor rotationVectorSensor = sensorManager2.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager2.registerListener(rvlistener, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public SensorEventListener rvlistener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] rotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(
                    rotationMatrix, sensorEvent.values
            );
            float[] remappRotationMatrix = new float[16];
            SensorManager.remapCoordinateSystem(rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    remappRotationMatrix);

            float[] orientations = new float[5];
            SensorManager.getOrientation(remappRotationMatrix, orientations);

            for (int i = 0; i < 5; i++) {
                orientations[i] = (float) (Math.toDegrees(orientations[i]));

                if (orientations[1] > 45) {
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                } else if (orientations[1] < -45) {
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                } else if (Math.abs(orientations[1]) < 10) {
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                }
                float x3 = orientations[0];
                float y3 = orientations[1];
                float z3 = orientations[2];
                MainActivity.this.x2.setText("X : " + (int) x3 + " derece");
                MainActivity.this.y2.setText("Y : " + (int) y3 + " derece");
                MainActivity.this.z2.setText("Z : " + (int) z3 + " derece");

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(track);
    }


    public class SensorTrack implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float sensor_data[] = sensorEvent.values;
            x.setText("X: " + sensor_data[0] + " m/s2");
            y.setText("Y: " + sensor_data[1] + " m/s2");
            z.setText("Z: " + sensor_data[2] + " m/s2");
            int xx = (int) sensor_data[0];
          /*  if(xx>=9 || xx<0){
                ( (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
                new AlertDialog.Builder(MainActivity.this).setTitle("").setMessage("" +
                        "x yönüne dikkat ediniz").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                        .show();
                play=MediaPlayer.create(MainActivity.this,R.raw.song);
                play.start();
                //sıfırlandığında durma kodu yazılacak
            }*/
            txt.setVisibility(View.VISIBLE);
            txt1.setVisibility(View.VISIBLE);
            txt2.setVisibility(View.VISIBLE);
            txt4.setVisibility(View.VISIBLE);
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, 44
                );
            }
            float[] values=sensorEvent.values;
             int orientation=ORIENTATION_UNKNOWN;
            int orientation2=ORIENTATION_UNKNOWN;
            int orientation3=ORIENTATION_UNKNOWN;
            float X=-values[_DATA_X];
            float Y=-values[_DATA_Y];
            float Z=-values[_DATA_Z];
            float magnitude=X*X+Y*Y;

            if(magnitude*4>= Z*Z){
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float)Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int)Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }

                float angle2 = (float)Math.atan2(-Z, X) * OneEightyOverPi;
                orientation2 = 90 - (int)Math.round(angle2);
                // normalize to 0 - 359 range
                while (orientation2 >= 360) {
                    orientation2 -= 360;
                }
                while (orientation2 < 0) {
                    orientation2 += 360;
                }

                float angle3 = (float)Math.atan2(-Z, Y) * OneEightyOverPi;
                orientation3 = 90 - (int)Math.round(angle3);
                // normalize to 0 - 359 range
                while (orientation3 >= 360) {
                    orientation3 -= 360;
                }
                while (orientation3 < 0) {
                    orientation3 += 360;
                }


            }
            z5.setText("Oreientation X="+orientation);
            z6.setText("Oreientation Y="+orientation2);
            z7.setText("Oreientation Z="+orientation3);
            Log.d("Oreientation",""+orientation);
            if(orientation!=mOrientationDeg){
                mOrientationRounded=orientation;
                if(orientation==-1){}
                     else if(orientation <= 45 || orientation > 315){//round to 0
                        tempOrientRounded = 1;//portrait
                    }
                    else if(orientation > 45 && orientation <= 135){//round to 90
                        tempOrientRounded = 2; //lsleft
                    }
                    else if(orientation > 135 && orientation <= 225){//round to 180
                        tempOrientRounded = 3; //upside down
                    }
                    else if(orientation > 225 && orientation <= 315){//round to 270
                        tempOrientRounded = 4;//lsright
                    }
                }
            if(mOrientationRounded!=tempOrientRounded){
                mOrientationRounded=tempOrientRounded;
            }
        //    z6.setText("tempOrientRounded="+tempOrientRounded);
            }


        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            switch (i) {
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    Log.i("onAccuracyChanged", "Yüksek doğruluk :" + i);
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    Log.i("onAccuracyChanged", "Orta doğruluk :" + i);
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    Log.i("onAccuracyChanged", "Düşük doğruluk :" + i);
                    break;
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
    }
}