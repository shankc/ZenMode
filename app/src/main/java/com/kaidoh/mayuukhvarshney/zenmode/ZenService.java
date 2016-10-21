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
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

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
        sensorManager.registerListener(ZenService.this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        SharedPreferences shared = getSharedPreferences("Settings",Context.MODE_PRIVATE);
    //   STILL= shared.getString("WakeTime",null);


        STILL = getDateTimeFromString("10/21/2016 20:00:00");
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
           IsDeviceInMotion(event);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void IsDeviceInMotion(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // float vectorsum = (event.values[0]*event.values[0]) + (event.values[1]*event.values[1])+(event.values[2]*event.values[2]);

            String CurrentTime=get_Time();


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
                SendSensorState(true,0);

            }
            else
            {

                if(!ZenMode.containsKey(STILL))
                {
                    ZenMode.put(STILL,CurrentTime);
                    MOVE = CurrentTime;
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    long temp;
                    try {
                        Log.d("ZenService ","the values entering for difference calculation "+CurrentTime+" "+STILL);
                        temp = getDifferenceBetweenTimes(format.parse(CurrentTime), format.parse(STILL));
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        temp =0;
                    }
                            //Math.abs(ConvertToMiliSeconds(MOVE)-ConvertToMiliSeconds(STILL));
                    Log.d("ZenService","the new time difference in milli "+temp);
                    //Log.d("ZenService","the converted seconds is "+STILL+" "+MOVE+" "+temp+" "+ConvertToMiliSeconds(STILL)+" "+ConvertToMiliSeconds(CurrentTime));

                    SendSensorState(false,temp); //convert time and send it through broadcast
                }


            }

        }
    }
    public String get_Time()
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public String get_simple_date()
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public String getDateTimeFromString(String text)
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss" );

       try{ Date date = dateFormat.parse(text);
            return dateFormat.format(date);
       }
       catch(Exception e)
       {
           e.printStackTrace();
           return null;
       }

    }
    public long ConvertToMiliSeconds(String text)
    {
        /*DateFormat datetime = new SimpleDateFormat("HH:mm:ss");
        try {
            Date date = datetime.parse(text);
            long x =  date.getTime();
            return x;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return 0;
        }*/

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = sdf.parse("1970-01-01" + text);
            long x = date.getTime();
            return x;
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


            if(!ZenMode.isEmpty()) {

                ZenDataBase db = new ZenDataBase(this);

               Map<String,String> Sorted_ZenMode = new TreeMap<String,String>(ZenMode);

                for (String i : Sorted_ZenMode.keySet()) {


                    Log.d("ZenService", "the Zen timings are => " + i + " " + Sorted_ZenMode.get(i));
                }

           //add all the values to the data base here.... calculate the total zen hours and movement hours and add it to the database table.
          db.addZenDate(get_simple_date());
               // Log.i("ZenService"," the added date is "+getDate());
                long Hours24 = (24*60*60*1000);

                long MovementHours = Hours24-ELAPSED_TIME;
              // Log.d("ZenService","the added MovementHours "+MovementHours);
             db.addMovementhours(String.valueOf(FinalValueToBeInserted(MovementHours)));



                db.addZenHours(String.valueOf(FinalValueToBeInserted(ELAPSED_TIME)));
               // Log.d("ZenService","the added ZenHours "+ELAPSED_TIME);
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

    public void SendSensorState(boolean SensorState,long StillTime)
    {

        Intent intent = new Intent("State");
        intent.putExtra("Movement", SensorState);
        if(!SensorState) {
            intent.putExtra("StillTime", StillTime);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    public String GenericTimeConversion(long millis)
    {
        /*int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millis) %60;
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(millis) %60;
        int hours = (int) TimeUnit.MILLISECONDS.toHours(millis)%24;
        String H = (hours<10)? "0"+String.valueOf(hours) : String.valueOf(hours);
        String S = (seconds<10) ? "0"+String.valueOf(seconds):String.valueOf(seconds);
        String M = (minutes<10) ? "0"+String.valueOf(minutes):String.valueOf(minutes);
       String time = H+":"+M+":"+S;
        return time;*/
        String text =   String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        text = get_simple_date()+" "+text;
        return text;

    }

    public String FinalValueToBeInserted(long millis)
    {
        String text =   String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        return text;
    }

    public long getDifferenceBetweenTimes(Date d1,Date d2)
    {
        Log.d("ZenService","calculating the respective dates "+d1.getDate()+" "+d2.getDate()+" "+d1.getTime()+" "+d2.getTime());

        long diff = Math.abs(d2.getTime() - d1.getTime());
        return diff;
    }




}