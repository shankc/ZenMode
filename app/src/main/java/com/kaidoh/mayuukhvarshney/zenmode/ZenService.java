package com.kaidoh.mayuukhvarshney.zenmode;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
/**
 * Created by mayuukhvarshney on 13/10/16.
 */
public class ZenService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor StepSensor;
    private Chronometer StillTimer;
    private String Wake_up_Time,STILL="",MOVE="",STOP_TIME="";
    private final IBinder ZenBind = new ZenBinder();
    HashMap<String,String> ZenMode = new HashMap<>();
    HashMap<String,String> MoveMOde = new HashMap<>();
    private long ELAPSED_TIME;
    private boolean SensorState = false;

    public void onCreate(){
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
      StepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, StepSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(ZenService.this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        SharedPreferences shared = getSharedPreferences("Settings",Context.MODE_PRIVATE);
       STILL= shared.getString("WakeTime",null);
        Wake_up_Time=STILL;


    }
 public class ZenBinder extends Binder {
        ZenService getService(){
            return ZenService.this;
        }
    }
public void WakeUpTime(String time)
{
    this.Wake_up_Time = time;
    Log.d("ZenService","the wake up time is "+Wake_up_Time);

}
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return ZenBind;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {


       if (!SensorState) {


            /*String CurrentTime = get_Time();

            if (!IsDeviceInMotion(event)) {
                if (ZenMode.get(STILL) == null) {
                    ZenMode.put(STILL, CurrentTime);
                    MOVE = CurrentTime;
                }

            }
            else {
                if (MoveMOde.get(MOVE) == null) {
                    MoveMOde.put(MOVE, CurrentTime);
                    STILL = CurrentTime;
                }
            }

            for (String i : ZenMode.keySet()) {
              //  Log.d("ZenService", "the Zen MOde HashMap is " + i + " " + ZenMode.get(i));
            }*/
           IsDeviceInMotion(event);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void IsDeviceInMotion(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // float vectorsum = (event.values[0]*event.values[0]) + (event.values[1]*event.values[1])+(event.values[2]*event.values[2]);
            String CurrentTime = get_Time();
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float normalize = (float) Math.sqrt(x * x + y * y + z * z);

            x = (x / normalize);
            y = (y / normalize);
            z = (z / normalize);

            int incline = (int) Math.round(Math.toDegrees(Math.acos(z)));
            if (incline < 25 || incline > 155) {
              //  Log.d("ZenService", "the device is flat");
                if(!(MOVE).equals("")) {

                    if (!MoveMOde.containsKey(MOVE)) {
                        MoveMOde.put(MOVE, CurrentTime);
                        STILL = CurrentTime;

                    }
                }
                SendSensorState(true);

            }
            else
            {
                if(!ZenMode.containsKey(STILL))
                {
                    ZenMode.put(STILL,CurrentTime);
                    MOVE = CurrentTime;
                    SendSensorState(false);
                }

            }

        }
    }
    public String get_Time()
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat datetime= new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return datetime.format(date);
    }
    public String getDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public long ConvertToMiliSeconds(String text)
    {
        DateFormat datetime = new SimpleDateFormat("HH:mm:ss");
        try {
            Date date = datetime.parse(text);
            return date.getTime();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return 0;
        }

    }
    public int ConvertToHours(long x)
    {
        long rem = (24*60*60*1000);
        int hours = (int)(x/rem);

        return hours;
    }
    public void DisplayZenTimes(boolean flag)
    {

        if(flag)
        {
            Log.d("ZenService","the flag is true");

            if(!ZenMode.isEmpty()) {

                ZenDataBase db = new ZenDataBase(this);

               Map<String,String> Sorted_ZenMode = new TreeMap<String,String>(ZenMode);

                for (String i : Sorted_ZenMode.keySet()) {


                    Log.d("ZenService", "the Zen timings are => " + i + " " + Sorted_ZenMode.get(i));
                }

           //add all the values to the data base here.... calculate the total zen hours and movement hours and add it to the database table.
          db.addZenDate(getDate());
               // Log.i("ZenService"," the added date is "+getDate());
                long Hours24 = (24*60*60*1000);

                long MovementHours = Hours24-ELAPSED_TIME;
              // Log.d("ZenService","the added MovementHours "+MovementHours);
             db.addMovementhours(String.valueOf(ConvertToHours(MovementHours)));

                db.addZenHours(String.valueOf(ELAPSED_TIME));
                //Log.d("ZenService","the added ZenHours "+ELAPSED_TIME);
            }
            else
            {
                Toast.makeText(this,"The Phone Has been lying still for 24 hours ",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void Time_Elapsed(long time)
    {
        this.ELAPSED_TIME = time;
    }

    public void StopSensor(boolean flag )
    {
        SensorState = flag;
    }

    public void SendSensorState(boolean SensorState)
    {
        Intent intent = new Intent("State");
        intent.putExtra("Movement",SensorState);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}