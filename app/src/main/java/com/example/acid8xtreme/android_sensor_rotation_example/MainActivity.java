package com.example.acid8xtreme.android_sensor_rotation_example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    SensorManager sm;
    float lastValue = -1, min = 1000, max = -1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sm.getSensorList(Sensor.TYPE_GAME_ROTATION_VECTOR).size()!=0){
            Sensor s = sm.getSensorList(Sensor.TYPE_GAME_ROTATION_VECTOR).get(0);
            sm.registerListener(this,s, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sm.getSensorList(Sensor.TYPE_GAME_ROTATION_VECTOR).size()!=0){
            Sensor s = sm.getSensorList(Sensor.TYPE_GAME_ROTATION_VECTOR).get(0);
            sm.registerListener(this,s, SensorManager.SENSOR_DELAY_FASTEST);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        sm.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_GAME_ROTATION_VECTOR) return;
        float m_azimuth_radians = event.values[0];
        float m_pitch_radians = event.values[1];
        if (m_azimuth_radians > 0) {
            if (m_azimuth_radians < min) {
                min = m_azimuth_radians;
                m_azimuth_radians = lastValue;
            }
            if (m_azimuth_radians > max) {
                max = m_azimuth_radians;
                m_azimuth_radians = lastValue;
            }
            lastValue = m_azimuth_radians;
        }
        else if (lastValue != -1 && min != 1000 && max != -1000) {
            if (lastValue < 1.5f) {
                float f = m_azimuth_radians * -1;
                f -= min;
                m_azimuth_radians = min - f;
            } else {
                float f = m_azimuth_radians * -1;
                f = max - f;
                m_azimuth_radians = max + f;
            }
        }
        updateTextView(m_azimuth_radians,m_pitch_radians);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateTextView(final float x, final float y) {
        runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                String a = ""+x;
                String b = ""+y;
                TextView t1 = (TextView) findViewById(R.id.textView1);
                t1.setText(a);
                TextView t2 = (TextView) findViewById(R.id.textView2);
                t2.setText(b);
            }
        });
    }

    /*
    public float map(float x, float in_min, float in_max, float out_min, float out_max)
    {
        float f = (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
        if (f < out_min) f = out_min;
        else if (f > out_max) f = out_max;
        return f;
    }
    */
}