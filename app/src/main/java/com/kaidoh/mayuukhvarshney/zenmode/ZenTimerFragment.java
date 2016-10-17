package com.kaidoh.mayuukhvarshney.zenmode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mayuukhvarshney on 13/10/16.
 */
public class ZenTimerFragment extends Fragment
{
    protected Button StartButton,StopButton;
    protected Chronometer zen_mode_timer;
    protected  String  Wake_Up_Time="07:30:00";

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstaceSate)
    {
        View view = inflater.inflate(R.layout.zen_timer,container,false);
        StartButton = (Button) view.findViewById(R.id.start_button);
        StopButton = (Button) view.findViewById(R.id.stop_button);
        zen_mode_timer= (Chronometer) view.findViewById(R.id.zen_watch);
      //  zen_mode_timer.setBase(SystemClock.elapsedRealtime());
        zen_mode_timer.start();
        ((ZenActiviy)getActivity()).ZEN.WakeUpTime(Wake_Up_Time);
        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                zen_mode_timer.start();
            }
        });
        StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zen_mode_timer.stop();
            }
        });

        return view;

    }
    public String getTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat datetime= new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return datetime.format(date);
    }


}
