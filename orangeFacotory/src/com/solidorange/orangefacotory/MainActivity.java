package com.solidorange.orangefacotory;

import java.util.EmptyStackException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */

	String tag = MainActivity.class.getName();
	private String url;
	String stat="kaju";
	String score="0";
	private Handler handler = new Handler();
	private LocationManager mgr=null;
	private float bearing;
	private Location deviceLocation;
	private Location destinationLocation;
	private MyCompassView myCompassView;
	private static SensorManager mySensorManager;
	private boolean sersorrunning;
	public boolean firsttimeflag=true;

	public void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mgr=(LocationManager)getSystemService(LOCATION_SERVICE);

		Intent intent;
		url="http://notarealemailaddress.com/data.php?id="+getAccountID();



		myCompassView = (MyCompassView)findViewById(R.id.mycompassview);

		mySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> mySensors = mySensorManager.getSensorList(Sensor.TYPE_ORIENTATION);

		if(mySensors.size() > 0){
			mySensorManager.registerListener(mySensorEventListener, mySensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
			sersorrunning = true;
			Toast.makeText(this, "Start ORIENTATION Sensor", Toast.LENGTH_LONG).show();

		}
		else{
			Toast.makeText(this, "No ORIENTATION Sensor", Toast.LENGTH_LONG).show();
			sersorrunning = false;
			finish();
		}

		final Button home = (Button) findViewById(R.id.Button01);
		home.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				url="0";
				Intent intent = new Intent(MainActivity.this, MainActivity.class);
				intent.putExtra("url",url);
				Toast.makeText(MainActivity.this, "ID '" + 1 + "' was clicked.", Toast.LENGTH_LONG).show();
				//startActivity(intent);
				//finishscreen();
			}
		});
		final Button start = (Button) findViewById(R.id.buttonrefresh);
		start.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				url="0";
				Intent intent = new Intent(MainActivity.this, MainActivity.class);
				intent.putExtra("url",url);
				Toast.makeText(MainActivity.this, "ID '" + url + "' was clicked.", Toast.LENGTH_LONG).show();
				//startActivity(intent);
				//finishscreen();
			}
		});
		AlertDialog.Builder bd = new AlertDialog.Builder(this);
		AlertDialog ad = bd.create();
		ad.setTitle("Error: Network");
		if(!isOnline())
		{
			ad.setMessage("No Data Connection: \n" +
			"This Application requires an Active data Connection");
			ad.show();
		}
		else if(stat.equals("ClientProtocolException"))
		{
			ad.setMessage("Cannot Connect1: "+stat);
			ad.show();
		}else if(stat.equals("IOException"))
		{
			ad.setMessage("Cannot Connect:\nConnection cannot be established: "+stat);
			ad.show();
		}

		else
		{
			Log.d(tag,"Cool got the device online");
			if(url!=null)
			{
				handler.postDelayed(sendUpdatesToUI, 3000); // 1 second
			}
			else
			{
				ad.setTitle("Error: RED");
				ad.setMessage("This should have Never Happened.\n Un-accounted for scenario. Please contact arpit@clemson.edu urgently");
				ad.show();
			}
		}

		//finishscreen();
	}
	private SensorEventListener mySensorEventListener = new SensorEventListener(){

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			try{
				myCompassView.updateDirection(-deviceLocation.bearingTo(getDestinationLocation()) + (float)event.values[0]);
			}catch(Exception e){}
		}
	};

	private void finishscreen() {
		this.finish();
	}
	public Boolean isOnline()	
	{
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni != null && ni.isConnected())
			return true;
		return false;
	}
	public String getAccountID()
	{
		TelephonyManager mTelephonyMgr =(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		String imei = mTelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
		Log.i(tag,"Device ID"+imei);
		return imei;
	}
	private Runnable sendUpdatesToUI = new Runnable() {
		int i=0;
		public void run() {
			//DisplayLoggingInfo();
			new GetDataTask().execute();
			handler.postDelayed(this, 4000); // 1 seconds

		}
	};    

	public Location getDestinationLocation()
	{
		Location DestinationLocation = new Location("");
		DestinationLocation.setLatitude(32.900103);
		DestinationLocation.setLongitude(-79.916544);
		return DestinationLocation;
	}

	@Override
	public void onResume() {
		super.onResume();
		mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER , 3000, 2, onLocationChange);
	}

	@Override
	public void onPause() {
		super.onPause();
		mgr.removeUpdates(onLocationChange);
	}

	float heading;
	//private serverLocation;
	LocationListener onLocationChange = new LocationListener() {
		public void onLocationChanged(Location location) {

			deviceLocation = location;



			/*
			Location dest = getDestinationLocation();  

			bearing = location.bearingTo(dest);

			float myBearing = bearing;

			Log.i("Location", "bear: " + myBearing);

			GeomagneticField geoField = new GeomagneticField(
					Double.valueOf(location.getLatitude()).floatValue(),
					Double.valueOf(location.getLongitude()).floatValue(),
					Double.valueOf(location.getAltitude()).floatValue(),
					System.currentTimeMillis()
					);
			Log.i("Location", "Geo: "+geoField.getDeclination());

			heading += geoField.getDeclination();
			Log.i("Location", "heading: " + heading);


			heading = myBearing - (myBearing + heading); 
			Log.i("Location", "heading 2: " + heading);


			Log.i("Location", "final loc: "+Math.round(-heading / 360.0 + 180.0));
			 */

		}

		public void onProviderDisabled(String provider) {
			// required for interface, not used
		}

		public void onProviderEnabled(String provider) {
			// required for interface, not used
		}

		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			// required for interface, not used
		}
	};

	private class GetDataTask extends AsyncTask<Void, Void, Integer> 
	{

		@Override
		public Integer doInBackground(Void... params) 
		{
			// Simulates some delay
			try{Thread.sleep(2000);}
			catch (InterruptedException e1){e1.printStackTrace();}
			if(deviceLocation!=null)
			{
				if(firsttimeflag==true)
				{
					Log.i(tag,"Here to register");
					String nurl=url="http://notarealemailaddress.com/add_player.php?id="+getAccountID()+"&location-x="+deviceLocation.getLatitude()+"&location-y="+deviceLocation.getLongitude()+"&current_score="+score;;
					String xml=XMLfunctions.getXML(url);
					if(xml==null)
						Log.i(tag,"Registered");
					firsttimeflag=false;
				}
				else
				{
					String nurl=url="http://notarealemailaddress.com/getcurrentstats.php";
					String xml=null;
					xml=XMLfunctions.getXML(url);
					Log.e(tag,url+" "+xml);
					Document doc=XMLfunctions.XMLfromString(xml);
					if(doc!=null)
					{
						String idTag = "";
						NodeList nodes = doc.getElementsByTagName("id");
						if(nodes.getLength()>=1)
						{
							idTag = nodes.item(0).getTextContent();
						}


						if (idTag == getAccountID()){

							nodes = doc.getElementsByTagName("future_longitude");
							
							String future_longitude = "";
							String future_latitude = "";
							
							if(nodes.getLength()>=1)
							{

								future_longitude = nodes.item(0).getTextContent();
							}
							nodes = doc.getElementsByTagName("future_latitude");
							if(nodes.getLength()>=1)
							{
								future_latitude = nodes.item(0).getTextContent();
							}
							
							String timestamp = "";
							
							
							nodes = doc.getElementsByTagName("timestamp");
							if(nodes.getLength()>=1)
							{
								timestamp = nodes.item(0).getTextContent();
							}
							String player_one_score = ""; 
							nodes = doc.getElementsByTagName("player_one_score");
							if(nodes.getLength()>=1)
							{
								player_one_score = nodes.item(0).getTextContent();
							}
							Location futureLocation = new Location("");
							futureLocation.setLongitude(Double.parseDouble(future_longitude));
							futureLocation.setLatitude(Double.parseDouble(future_latitude));
							long timeStamp2 = Long.parseLong(timestamp);
							if (System.currentTimeMillis() >= timeStamp2){
								if (deviceLocation.distanceTo(futureLocation) < 50){
																		
									score = score + 1;
									url="http://notarealemailaddress.com/updatecurrentstats.php?id="+getAccountID()+"&location-x="+deviceLocation.getLatitude()+"&location-y="+deviceLocation.getLongitude()+"&current_score="+score;
									xml = XMLfunctions.getXML(url);
									if(xml==null)
										Log.i(tag,"Registered");	
									
									Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
						    		Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
						    		r.play();
										
								}
								else {
									
									score = score ;
									url="http://notarealemailaddress.com/updatecurrentstats.php?id="+getAccountID()+"&location-x="+deviceLocation.getLatitude()+"&location-y="+deviceLocation.getLongitude()+"&current_score="+score;
									xml = XMLfunctions.getXML(url);
									if(xml==null)
										Log.i(tag,"Registered");	
										
									
								}
								
							}

						}

					}
					else
						throw new EmptyStackException();

				}
			}
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) 
		{
			super.onPostExecute(result);
		}
	}

}

