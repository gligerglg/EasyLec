package com.example.gayanlakshitha.easylec;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


/**
 * Created by Gayan Lakshitha on 25/06/2017.
 */

public class AddEntry extends Activity {
    private String weekDay;
    private int duration;
    private Spinner spinner;
    private Spinner durations;
    private Button btnAdd;
    private Button btnTime;
    private TextView txtTime;
    private TimePicker timePicker;
    int hour,minute;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tblentry);

        spinner = (Spinner)findViewById(R.id.weekDays);
        durations = (Spinner)findViewById(R.id.duration);
        btnAdd = (Button)findViewById(R.id.btn_UpdateSelected);
        timePicker = (TimePicker)findViewById(R.id.timePicker);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.week_days,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        ArrayAdapter<CharSequence> adp_dur = ArrayAdapter.createFromResource(this,R.array.durations,android.R.layout.simple_spinner_dropdown_item);
        durations.setAdapter(adp_dur);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekDay = spinner.getSelectedItem().toString();

                switch (durations.getSelectedItem().toString())
                {
                    case "1 Hour": duration=1; break;
                    case "2 Hour": duration=2; break;
                    case "3 Hour": duration=3; break;
                    case "4 Hour": duration=4; break;
                }

                if(Build.VERSION.SDK_INT>=23)
                {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                }
                else
                {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }

                try{
                    SQLiteDatabase db = openOrCreateDatabase("easylec_db.db",MODE_PRIVATE,null);
                    db.execSQL("INSERT INTO tbl_Schedule (weekday,hour,minute,duration) VALUES('" + weekDay + "'," + hour + "," + minute + "," + duration + ")");
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Database Error",Toast.LENGTH_LONG);
                }finally {
                    Toast.makeText(getApplicationContext(),"Weekday : "+ weekDay+"\nTime : " + hour + ":" + minute + "\nDuration : " + durations.getSelectedItem().toString() +"\nAdded to Schedule Successfully!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
