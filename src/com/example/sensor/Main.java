package com.example.sensor;


import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class Main extends Activity implements SensorEventListener {

	Sensor accelerometer;
	SensorManager sm;
	
	TextView acceleration;
	TextView gyroscope;
	TextView imu;
	
	Sensor gyro;
	
	static float accel_X;
	static float accel_Y;
	static float accel_Z;
	
	static float gyro_X;
	static float gyro_Y;
	
	IMU go;
	
	List<Sensor> sensorList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		gyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		
		// Register a listener for accelerometer and gyroscope
		sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sm.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
		
		// Create TextViews
		acceleration = (TextView) findViewById(R.id.acceleration);
		gyroscope = (TextView) findViewById(R.id.gyroscope);
		imu = (TextView) findViewById(R.id.imu);
		
		// Setup IMU Algorithm
		go = new IMU();
		go.setup();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
        Sensor sensor = event.sensor;
        
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        	accel_X = event.values[0];
    		accel_Y = event.values[1];
    		accel_Z = event.values[2];
    		
    		acceleration.setText("Mobile Accel - X: "+ accel_X +
    				"\nY: " + accel_Y +
    				"\nZ: " + accel_Z);
        }
        else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
        	
        	gyro_X = event.values[0];
    		gyro_Y = event.values[1];
    		
    		gyroscope.setText("Mobile Gyro - X: "+ gyro_X +
    				"\nY: " + gyro_Y +
    				"\nZ: " + accel_Z);
            
        }
        
        
        /* Set TextView of IMU Results
           Inclination XYZ axis (as measured by accelerometer)
           Inclination XYZ axis (estimated / filtered)
        */
        
        //go.getEstimatedInclination();
        //imu.setText("Measured  X Y Z axis " + go.RwAcc[0] + ", " + go.RwAcc[1] + ", " + go.RwAcc[2] + "\n" +
        //		      "Estimated X Y Z axis " + go.RwEst[0] + ", " + go.RwEst[1] + ", " + go.RwEst[2]); 
        
        
	}

}
