package com.example.sensor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.widget.TextView;

public class Main extends Activity implements SensorEventListener {

	// Sensor variables for accelerometer and gyroscope
	SensorManager sm;
	Sensor gyro;
	Sensor accelerometer;
	
	// Activity TextView
	TextView acceleration;
	TextView gyroscope;
	TextView imu;
	TextView cf;
	
	// Accelerometer Variables
	static float accel_X;
	static float accel_Y;
	static float accel_Z;
	
	// Gyroscope Variables
	static float gyro_X;
	static float gyro_Y;
	static float gyro_Z;
	
	// Counter that keeps track of # of sensor changes
	static int counter = 0;
	
	// Time program to run for a certain amount of time
	int interval = 5; // 5 seconds
    Date timeToRun = new Date(System.currentTimeMillis() + interval);
	Timer timer = new Timer();
	
	// Classes that computer the IMU and Complimentary Filter
	IMU go;
	CF filter;
	
	// Variables used to record data and store it on android SD Card
	File file;
	FileWriter fw;
	BufferedWriter bw;
	String logdata = null;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// Open main.xml file
		setContentView(R.layout.main);		
		
		// Set up onsensor for accelerometer and gyroscope
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
		cf = (TextView) findViewById(R.id.cf);

		// Setup IMU Algorithm
		go = new IMU();
		go.setup();
		
		// Setup Comp Filter
		filter = new CF();
	
		// Log Data after program runs for a certain amount of time
		timer.schedule(new TimerTask() {			
			
	        public void run() {
	        	try {
	    			
	    			File sdCard = Environment.getExternalStorageDirectory();
	    			File dir = new File (sdCard.getAbsolutePath() + "/dir1/dir2");
	    			dir.mkdirs();
	    			
	    			String FILENAME = "datalog.txt";
	    			File file = new File(dir, FILENAME);

	    			FileOutputStream fos = new FileOutputStream (file);
	    			fos.write(logdata.getBytes());
	    			fos.close();
	    			
	    		} catch (IOException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	        	
	        	// terminate application after logging data
	        	finish();
	        }
		}, timeToRun);
			
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	public void onSensorChanged(SensorEvent event) {
				
		// event.timestamp; returns a long
		
	    Sensor sensor = event.sensor;
	    
	    // Record timestamp
	    logdata += "TimeStamp: " + Long.toString(event.timestamp) + "\n\n";
	        
	    	/////////////////////// Begin Sensor data given by phone   ////////////////////////////////
	    
	    	// if the onchange sensor is accelerometer record its data
	        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	        	accel_X = event.values[0];
	    		accel_Y = event.values[1];
	    		accel_Z = event.values[2];
	    		
	    		acceleration.setText("Mobile Accel -\nX: " + accel_X +
	    				"\nY: " + accel_Y +
	    				"\nZ: " + accel_Z + "\n\n");
	    		
	    		// Log accelerometer data; will be written to text file when program terminates
	    		logdata += Main.counter + " Accelerometer X " + Float.toString(accel_X) + "\n";
	    		logdata += Main.counter + " Accelerometer Y " + Float.toString(accel_Y) + "\n";
	    		logdata += Main.counter + " Accelerometer Z " + Float.toString(accel_Z) + "\n\n";
	        }
	        // else is when onsensor change is gyrsocope. Record gyroscope data.
	        else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
	        	
	        	gyro_X = event.values[0];
	    		gyro_Y = event.values[1];
	    		gyro_Z = event.values[2];
	    		
	    		gyroscope.setText("Mobile Gyro - \nX:"+ gyro_X +
	    				"\nY: " + gyro_Y + "\nZ" + gyro_Z + "\n\n");
	    		
	    		// Log gyroscope data; will be written to text file when program terminates
	    		logdata += Main.counter + " Gyro X " + Float.toString(gyro_X) + "\n";
	    		logdata += Main.counter + " Gyro Y " + Float.toString(gyro_Y) + "\n";
	    		logdata += Main.counter + " Gyro Z " + Float.toString(gyro_Z) + "\n\n";
	            
	        }
	        ////////////////////// End Sensor data given by phone   ////////////////////////////////
	        
	        
	        ////////////////////// Begin IMU Algos   ////////////////////////////////
	        
	    
	        // call IMU function to get estimation
	        go.getEstimatedInclination();
	     
	        /* Set TextView of IMU Results
	           Inclination XYZ axis (as measured by accelerometer)
	           Inclination XYZ axis (estimated / filtered)
	        */
	        imu.setText("Measured  X Y Z axis \n" + go.RwAcc[0] + "\n" + go.RwAcc[1] + "\n" + go.RwAcc[2] + "\n" +
	        		      "Estimated X Y Z axis \n" + go.RwEst[0] + "\n" + go.RwEst[1] + "\n" + go.RwEst[2]); 
	             
	        // Log Measurements and Estimations data from IMU; will be written to text file when program terminates
	        logdata += Main.counter + " Measured X " + Float.toString(go.RwAcc[0]) + "\n";
	        logdata += Main.counter + " Measured Y " + Float.toString(go.RwAcc[1]) + "\n";
	        logdata += Main.counter + " Measured Z " + Float.toString(go.RwAcc[2]) + "\n\n";
	        
	        logdata += Main.counter + " Estimated X " + Float.toString(go.RwEst[0]) + "\n";
	        logdata += Main.counter + " Estimated Y " + Float.toString(go.RwEst[1]) + "\n";
	        logdata += Main.counter + " Estimated Z " + Float.toString(go.RwEst[2]) + "\n\n";	
	        //////////////////////End IMU Algos   ////////////////////////////////
	        
	        //////////////////////Begin Complimentary Algos   ////////////////////////////////
	        
	        float dt = (float) 0.01;	// Constant given by Complimentary Filter
	        
	        // parameters for CompFilterWithPI
	        // CompFilterWithPI(gyroX, gyroY, gyro Z, accelX, accelY, dt)
	        
	        // Call the complimentary function for measured data (RwAcc) or estimated data (RwEst)
	        // filter.CompFilterWithPI(gyro_X, gyro_Y, gyro_Z, go.RwEst[0], go.RwEst[1], dt);
	        filter.CompFilterWithPI(gyro_X, gyro_Y, gyro_Z, go.RwAcc[0] , go.RwAcc[1], dt);

	        /* Set TextView of Complimentary Filter Results*/
	        cf.setText("Measured phi, theta, psi \n" + filter.phi + "\n" + filter.theta + "\n" + filter.psi); 
	        
	        // Log data for Complimentary Filter; will be written to text file when program terminates
	        logdata += Main.counter + " phi " + Float.toString(filter.phi) + "\n";
	        logdata += Main.counter + " theta " + Float.toString(filter.theta) + "\n";
	        logdata += Main.counter + " psi " + Float.toString(filter.psi) + "\n\n\n\n";	
	        
	        //////////////////////End Complimentary Algos   ////////////////////////////////

	        
	        // Increment the number of sensor changes
	        ++Main.counter;
		}
}
