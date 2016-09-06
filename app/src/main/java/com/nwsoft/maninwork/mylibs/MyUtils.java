package com.nwsoft.maninwork.mylibs;

import android.util.Log;

/**
 * Created by admin on 2016-09-06.
 */
public class MyUtils {
    /**Test module
     Log.i("i->s:", MyUtils.convertIntTime2String(250));
     Log.i("i->s:", MyUtils.convertIntTime2String(0));
     Log.i("s-i:","" + MyUtils.convertString2IntTime("1:30"));
     Log.i("s-i:","" + MyUtils.convertString2IntTime("0:00"));
     **/
    public static String convertIntTime2String(long intTime) {
        String timeformat ="2:30";
        Log.i("intTime-in:",intTime+"");
        float f = intTime / 100f; //-->2.50
        String[] ahm = String.format("%.2f",f).split("\\.");
        //x/60 = 50/100; x=50 * 60/100;
        int minute = Integer.parseInt(ahm[1]) * 60 / 100; //30
        timeformat=ahm[0] + String.format(":%02d", minute);
        Log.i("timeformat-out:",timeformat);
        return timeformat;
    }
    public static long convertString2IntTime(String timeformat) {
        long intTime = 0;    //will be 2:30 -> 2.50 -> 250;
        Log.i("timeformat-in:",timeformat);
        String[] ahm = timeformat.split(":");
        //30:60 = x:100, x=30/60
        int x = (int)(Integer.parseInt(ahm[1])/60f * 100);
        intTime= Integer.parseInt(ahm[0]) *100 + x ;
        Log.i("intTime-out:", intTime+"");
        return intTime;
    }
}
