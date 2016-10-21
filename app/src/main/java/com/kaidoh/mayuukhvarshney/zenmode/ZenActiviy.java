package com.kaidoh.mayuukhvarshney.zenmode;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kaidoh.mayuukhvarshney.zenmode.ZenService.ZenBinder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ZenActiviy extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
   protected Intent ZenIntent;
    protected ZenService ZEN = new ZenService();
    private ViewPager mViewPager;
   private String STOP_TIME ="30000";
    private static String Today_date="";
    private Context context;
    protected Toolbar toolbar;
    private long ChronoMeterTime=0,LastStop=0;
    private  long CURRENT_ZEN_TIME=0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null)
        {
          Today_date = savedInstanceState.getString("LastSavedDate");
        }
        else
        {
            Today_date = getDate();
        }
        setContentView(R.layout.zen_mode_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
       context = ZenActiviy.this;
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //setting the wakeup time

        SharedPreferences shared = getSharedPreferences("Settings",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("WakeTime","21:30:00");
        editor.apply();



    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putString("LastSavedDate",getDate());
        super.onSaveInstanceState(savedInstanceState);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_zen_activiy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
    private int mIndex;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                   // toolbar.setTitle("Timeline");
                    return new TimeLine();

                case 1:

                    return new ZenTimerFragment();

            }

    return null;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0 :
                     return "Timeline";
                case 1:
                    String title ="Zen Timer";
                    return title;
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object)
        {
            if(object instanceof TimeLine){
                ((TimeLine)object).Reload();
            }
            return super.getItemPosition(object);
        }



        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            if(position==0)
            {
                if(getSupportActionBar()!=null)
                getSupportActionBar().setTitle("Timeline");

            }
            else
            {
                if(getSupportActionBar()!=null)
                getSupportActionBar().setTitle("Zen Counter");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

       private ServiceConnection ZenConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ZenBinder binder = (ZenBinder)service;
                ZEN = binder.getService();
                // add ZenBound is true here and contrary on service disconnected.
              ZenTimerFragment fragment = (ZenTimerFragment) getSupportFragmentManager().findFragmentById(R.id.container);

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    @Override
    public void onStart()
    {
        super.onStart();

        ZenIntent = new Intent(this,ZenService.class);
        LocalBroadcastManager.getInstance(this).registerReceiver(MotionStateReceiver,
                new IntentFilter("State"));
        bindService(ZenIntent,ZenConnection, Context.BIND_AUTO_CREATE);
        startService(ZenIntent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ZEN.StopSensor(true);
        stopService(ZenIntent);

        unbindService(ZenConnection);
    }


        private BroadcastReceiver MotionStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle extras = intent.getExtras();
                boolean state = extras.getBoolean("Movement");
                long StopWatchTime = extras.getLong("StillTime");

                 ZenTimerFragment fragment = (ZenTimerFragment) getSupportFragmentManager().findFragmentById(R.id.container);

                //  Log.d("ZenActivity","the elapsed time "+Time_Elapsed);
               if(state)
               {
                   SharedPreferences shared = getSharedPreferences("Settings",Context.MODE_PRIVATE);
                   String TimeToRefresh = shared.getString("WakeTime",null);
                   String CurrentTime = getTime();

                 if(CurrentTime.equals(TimeToRefresh) && !(Today_date.equals(getDate()))) //final stop and refresh condiition.



                   {   //Log.d("ZenActivity","final counter stop mehtod entered " +SystemClock.elapsedRealtime()+" "+fragment.zen_mode_timer.getBase()+" "+Elapsed_Time);
                      ZEN.Time_Elapsed(CURRENT_ZEN_TIME);
                       ZEN.DisplayZenTimes(true);  // displayed values should be a total of the recived time and a static variable !!
                   fragment.Timer.setText(ZenTimerFragment.STRING_DEFAULT);
                     mViewPager.getAdapter().notifyDataSetChanged();
                     //  fragment.zen_mode_timer.start();
                       Today_date = getDate();

                   }


                   //Log.d("ZenActivity","the activity has been stopped");
               }
                else
               {
                   Log.d("ZenActivity","state false reached");
                   if(StopWatchTime!=0)
                   {
                       //update text view with the converted time in mili seconds.
                     //Log.d("ZenActivity","converting miliseconds to hours  "+MilliSecondCOnversion(StopWatchTime));
                    CURRENT_ZEN_TIME+=StopWatchTime;
                       Log.d("ZenActivity"," the stopwatch time is "+StopWatchTime+" "+CURRENT_ZEN_TIME +" "+GenericTimeConversion(CURRENT_ZEN_TIME));
                      String ZEN_TIMER= GenericTimeConversion(CURRENT_ZEN_TIME); // the value to be displayed in text view.
                       fragment.Timer.setText(ZEN_TIMER);

                   }

               }

            }
        };
    public String getTime()
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


    public String MilliSecondCOnversion(long millis)
    {
        DateFormat datetime= new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(millis);
        String x = datetime.format(date);
        return x;
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
      return   String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }


    }


