package lk.steps.breakdownassistpluss.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.Log;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import lk.steps.breakdownassistpluss.Models.Interruption;
import lk.steps.breakdownassistpluss.R;

/**
 * Created by JagathPrasanga on 2/13/2018.
 */

public class BasicActivity extends CalenderActivity {

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        if(interruptions!=null && interruptions.size()>0){
            //Log.e("onMonthChange", "interruptions Number-" + interruptions.size());
            int id = 1;
            for(Interruption interruption : interruptions){
                //Log.e("onMonthChange", "interruptions -" +id+ interruptions.get(0).Description);

                Calendar startTime = Calendar.getInstance();
                startTime.setTime(GetDate(interruption.StartTime));
                startTime.set(Calendar.MONTH, newMonth-1);
                startTime.set(Calendar.YEAR, newYear);

                Calendar endTime = (Calendar) startTime.clone();
                endTime.setTime(GetDate(interruption.EndTime));
                endTime.set(Calendar.MONTH, newMonth-1);
                endTime.set(Calendar.YEAR, newYear);

                //Random rnd = new Random();
               // int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                String title = interruption.InterruptionTypeName+"\n"+interruption.Description;

                WeekViewEvent  event = new WeekViewEvent(id, title, startTime, endTime);
                event.setColor(getResources().getColor(R.color.pink));
                //event.setColor(color);
                events.add(event);
                id++;
            }
        }

        return events;
    }


    private Date GetDate(String dtStart){
        //Log.e("GetDate", dtStart);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = format.parse(dtStart);
            //Log.e("GetDate", date.toString());
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
