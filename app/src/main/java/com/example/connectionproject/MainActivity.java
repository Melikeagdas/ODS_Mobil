package com.example.connectionproject;

import static java.lang.Math.subtractExact;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    TextView x2, y2, z2, z5, z6, z7, z8, z9, zilk, yilk, xilk, textView4, z10;
    EditText txt_dizi, txt_veri, txt_number;
    Button btngoster, button;
    FusedLocationProviderClient fusedLocationProviderClient;
    SensorManager sensorManager;
    SensorManager sensorManager2;
    Context context = this;
    Sensor sensor;
    Sensor sensor2;
    MediaPlayer play;
    int counter2 = 0;

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

    boolean isActive = false;
    boolean btnilklendir = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        btngoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String value = txt_dizi.getText().toString();
                    maxcount = new Integer(value).intValue();

                    String value2 = txt_veri.getText().toString();
                    counter2 = new Integer(value2).intValue();

                    isActive = true;
                } catch (Exception e) {
                    if (maxcount == 0 || counter2 == 0) {
                        Toast.makeText(getApplicationContext(), "Dizi boyutu veya veri sıklığı boş geçilemez !", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

    float[] orientations = new float[5];
    float sumY = 0;
    float sumZ = 0;
    float avgZ = 0;
    float avg = 0;
    int count = 0;
    int counter = 0;
    int maxcount = 0;
    int y3first;
    int z3first;
    int derece = 0;


    public SensorEventListener rvlistener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (isActive) {
                //
                ArrayClass arrayClass = new ArrayClass(maxcount); // ArrayClasstan bir yeni nesne ürettim ve constructor sayesinde size değerini verdim
                float[] currentArrayY = new float[arrayClass.getSize()];
                counter++;
                if (counter == counter2) {
                    counter = 0;
                    float[] rotationMatrix = new float[16];
                    SensorManager.getRotationMatrixFromVector(
                            rotationMatrix, sensorEvent.values
                    );
                    float[] remappRotationMatrix = new float[16];
                    SensorManager.remapCoordinateSystem(rotationMatrix,
                            SensorManager.AXIS_X,
                            SensorManager.AXIS_Z,
                            remappRotationMatrix);
                    int x3 = (int) orientations[0];
                    int y3 = (int) orientations[1];
                    int z3 = (int) orientations[2];
                    SensorManager.getOrientation(remappRotationMatrix, orientations);
                    for (int i = 0; i < orientations.length; i++) {
                        orientations[i] = (float) (Math.toDegrees(orientations[i]));
                        MainActivity.this.x2.setText("X : " + (int) x3 + " derece");
                        MainActivity.this.y2.setText("Y : " + (int) y3 + " derece");
                        MainActivity.this.z2.setText("Z : " + (int) z3 + " derece");
                    }
                    String derece1=txt_number.getText().toString();
                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            btnilklendir = true;
                            if (!derece1.matches("")) {
                                MainActivity.this.xilk.setText(String.valueOf(x3));
                                MainActivity.this.yilk.setText(String.valueOf(y3));
                                MainActivity.this.zilk.setText(String.valueOf(z3));

                                y3first = Integer.valueOf(String.valueOf(yilk.getText()));
                                yilk.setText(String.valueOf((int) y3first));

                                z3first = Integer.valueOf(String.valueOf(zilk.getText()));
                                zilk.setText(String.valueOf((int) z3first));

                                    derece = Integer.valueOf(String.valueOf(txt_number.getText()));
                                    txt_number.setText(String.valueOf((int) derece));
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Derece değer, boş geçilemez !",Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                    if (count == maxcount) {
                        avg = sumY / currentArrayY.length;
                        z9.setText("ort Y :" + avg);
                        if (btnilklendir == true) {
                                if ((y3first - avg) > derece || (y3first - avg) < -derece) {
                                    ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
                                    new AlertDialog.Builder(MainActivity.this).setTitle("").setMessage("" +
                                            "Y yönüne dikkat ediniz").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                            .show();
                                    play = MediaPlayer.create(MainActivity.this, R.raw.song);
                                    play.start();
                                }
                                avgZ = sumZ / currentArrayY.length;
                                z10.setText("ort Z: " + avgZ);
                                if ((z3first - avgZ) > derece || (z3first - avgZ < -derece)) {
                                    ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
                                    new AlertDialog.Builder(MainActivity.this).setTitle("").setMessage("" +
                                            "Z yönüne dikkat ediniz").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                            .show();
                                    play = MediaPlayer.create(MainActivity.this, R.raw.song);
                                    play.start();
                                }


                            sumZ = 0;
                            sumY = 0;
                            count = 0;
                        }
                    } else {
                        currentArrayY[count] = orientations[1];
                        sumY += orientations[1];
                        sumZ += orientations[2];
                        z8.setText("count: " + count);
                        count++;

                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    public void init() {
        zilk = findViewById(R.id.zilk);
        xilk = findViewById(R.id.xilk);
        yilk = findViewById(R.id.yilk);
        button = findViewById(R.id.button);
        x2 = findViewById(R.id.x2);
        y2 = findViewById(R.id.y2);
        z2 = findViewById(R.id.z2);
        z8 = findViewById(R.id.z8);
        z9 = findViewById(R.id.z9);
        z10 = findViewById(R.id.z10);
        btngoster = findViewById(R.id.btngoster);
        txt_veri = findViewById(R.id.txt_veri);
        txt_dizi = findViewById(R.id.txt_dizi);
        txt_veri = findViewById(R.id.txt_veri);
        txt_number = findViewById(R.id.txt_number);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        sensorManager2 = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor2 = sensorManager2.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.sensorManager2 = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        this.sensor2 = this.sensorManager2.getDefaultSensor(4);
        Sensor rotationVectorSensor = sensorManager2.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager2.registerListener(rvlistener, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}