package com.kaidoh.mayuukhvarshney.zenmode;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mayuukhvarshney on 13/10/16.
 */
public class ZenTimerFragment extends Fragment
{
    protected Button StartButton,StopButton;
    protected TextView Timer;
    protected Chronometer zen_mode_timer;
    protected  String  Wake_Up_Time="07:30:00";
    protected long startTime = 0L,timeInMilliseconds=0L,timeswap=0L,updatedTime=0L;
    protected Handler handler = new Handler();
    protected static final String STRING_DEFAULT="00:00:00";

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstaceSate)
    {
        View view = inflater.inflate(R.layout.zen_timer,container,false);
        Timer = (TextView) view.findViewById(R.id.timerValue);
        Timer.setText(STRING_DEFAULT);

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
