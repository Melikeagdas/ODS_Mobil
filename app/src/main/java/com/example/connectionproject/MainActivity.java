package com.example.connectionproject;

import static java.lang.Math.subtractExact;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    TextView x2, y2, z2, z5, z6, z7, z8, z9, zilk, yilk, xilk, z10, z, mesaj;
    EditText txt_dizi, txt_veri, txt_number, txt_number2, txt_mesaj;
    Button btngoster, btnilklendir;
    ImageButton imgbtn_dizi, imgbtn_veri, imgbtn_y, imgbtn_z, imgbtn_genel,imgbtn_mesaj;
    FusedLocationProviderClient fusedLocationProviderClient;
    SensorManager sensorManager;
    SensorManager sensorManager2;
    Context context = this;
    Sensor sensor;
    Sensor sensor2;
    MediaPlayer play;
    int counter2 = 0;
    RelativeLayout mainlayout;

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
    boolean ilklendir = false;

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
        imgbtn_dizi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(mainlayout,"Dizi boyutu girmeye yarar.",Snackbar.LENGTH_LONG).show();

            }
        });
        imgbtn_veri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(mainlayout,"Veri boyutu girmeye yarar.",Snackbar.LENGTH_LONG).show();
            }
        });
        imgbtn_y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(mainlayout,"Y düzleminde derece açıklığını girmeye yarar.",Snackbar.LENGTH_LONG).show();
            }
        });
        imgbtn_z.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(mainlayout,"Z düzleminde derece açıklığını girmeye yarar.",Snackbar.LENGTH_LONG).show();
            }
        });
        imgbtn_genel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(mainlayout,"X,Y ve Z koordinatları için ilk,mevcut ve ortalama değeri verir. ",Snackbar.LENGTH_LONG).show();
            }
        });
        imgbtn_mesaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(mainlayout,"Koordinat sapmalarında verilecek mesajın sıklığını ifade eder. ",Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void openpopupWindow() {
        Intent popupwindow=new Intent(MainActivity.this, PopupWindow.class);
        startActivity(popupwindow);
    }

    float[] orientations = new float[5];
    float sumY = 0;
    float sumZ = 0;
    float avgZ = 0;
    float avg = 0;
    float sumX = 0;
    float avgX = 0;
    int count = 0;
    int counter = 0;
    int maxcount = 0;
    int y3first;
    int z3first;
    int dereceY = 0;
    int dereceZ = 0;
    int mesaj_sıklıgı = 0;
    int anlik_mesajY = 0;
    int anlik_mesajZ = 0;

    public SensorEventListener rvlistener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (isActive) {
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
                        MainActivity.this.x2.setText("X : " + (int) x3 + " °");
                        MainActivity.this.y2.setText("Y : " + (int) y3 + " °");
                        MainActivity.this.z2.setText("Z : " + (int) z3 + " °");
                    }
                    String derece1 = txt_number.getText().toString();
                    String derece2 = txt_number2.getText().toString();
                    String mesajsikligi = txt_mesaj.getText().toString();
                    btnilklendir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ilklendir = true;
                            if (!derece1.matches("") && !derece2.matches("") && !mesajsikligi.matches("")) {
                                MainActivity.this.xilk.setText(String.valueOf(x3));
                                MainActivity.this.yilk.setText(String.valueOf(y3));
                                MainActivity.this.zilk.setText(String.valueOf(z3));

                                y3first = Integer.valueOf(String.valueOf(yilk.getText()));
                                yilk.setText(String.valueOf((int) y3first));

                                z3first = Integer.valueOf(String.valueOf(zilk.getText()));
                                zilk.setText(String.valueOf((int) z3first));

                                dereceY = Integer.valueOf(String.valueOf(txt_number.getText()));
                                txt_number.setText(String.valueOf((int) dereceY));

                                dereceZ = Integer.valueOf(String.valueOf(txt_number2.getText()));
                                txt_number2.setText(String.valueOf((int) dereceZ));

                                mesaj_sıklıgı = Integer.valueOf(String.valueOf(txt_mesaj.getText()));
                                txt_mesaj.setText(String.valueOf((int) mesaj_sıklıgı));
                            } else {
                                Toast.makeText(getApplicationContext(), "Derece veya mesaj sıklığı değeri boş geçilemez !", Toast.LENGTH_LONG).show();
                                ilklendir = false;
                            }
                        }
                    });
                    if (count == maxcount) {
                        if (ilklendir == true && !derece1.matches("") && !derece2.matches("")) {
                            avg = sumY / currentArrayY.length;
                            z9.setText("ort Y :" + avg);
                            if ((y3first - avg) > dereceY || (y3first - avg) < -dereceY) {
                                anlik_mesajY++;
                                if (anlik_mesajY == mesaj_sıklıgı) {
                                    ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
                                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("UYARI");
                                    builder.setMessage(""+"Y yönüne dikkat ediniz");
                                    builder.setCancelable(false);
                                    final AlertDialog dialog=builder.create();
                                    dialog.show();
                                    final Timer timer2=new Timer();
                                    timer2.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            timer2.cancel();
                                        }
                                    },3000);
                           play = MediaPlayer.create(MainActivity.this, R.raw.error);
                                    play.start();

                                    anlik_mesajY = 0;
                                }
                            }
                            avgZ = sumZ / currentArrayY.length;
                            z10.setText("ort Z: " + avgZ);
                            if ((z3first - avgZ) > dereceZ || (z3first - avgZ < -dereceZ)) {
                                anlik_mesajZ++;
                                if (anlik_mesajZ == mesaj_sıklıgı) {
                                    ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
                                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("UYARI");
                                    builder.setMessage(""+"Z yönüne dikkat ediniz");
                                    builder.setCancelable(false);
                                    final AlertDialog dialog=builder.create();
                                    dialog.show();
                                    final Timer timer3=new Timer();
                                    timer3.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            timer3.cancel();
                                        }
                                    },3000);
                                    play = MediaPlayer.create(MainActivity.this, R.raw.song);
                                    play.start();
                                    anlik_mesajZ = 0;
                                }
                            }

                            avgX = sumX / currentArrayY.length;
                            z.setText("ort X: " + avgX);

                        }
                        sumX = 0;
                        sumZ = 0;
                        sumY = 0;
                        count = 0;

                    } else {
                        currentArrayY[count] = orientations[1];
                        sumX += orientations[0];
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
        x2 = findViewById(R.id.x2);
        y2 = findViewById(R.id.y2);
        z2 = findViewById(R.id.z2);
        z8 = findViewById(R.id.z8);
        z9 = findViewById(R.id.z9);
        z10 = findViewById(R.id.z10);
        z = findViewById(R.id.z);
        btngoster = findViewById(R.id.btngoster);
        btnilklendir = findViewById(R.id.btnilklendir);
        txt_veri = findViewById(R.id.txt_veri);
        txt_dizi = findViewById(R.id.txt_dizi);
        txt_number = findViewById(R.id.txt_number);
        txt_number2 = findViewById(R.id.txt_number2);
        txt_mesaj = findViewById(R.id.txt_mesaj);
        mesaj = findViewById(R.id.textView9);
        imgbtn_dizi = findViewById(R.id.imgbtn_dizi);
        imgbtn_veri = findViewById(R.id.imgbtn_veri);
        imgbtn_y = findViewById(R.id.imgbtn_y);
        imgbtn_z = findViewById(R.id.imgbtn_z);
        imgbtn_genel = findViewById(R.id.imgbtn_genel);
        imgbtn_mesaj = findViewById(R.id.imgbtn_mesaj);
        mainlayout=findViewById(R.id.mainlayout);
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