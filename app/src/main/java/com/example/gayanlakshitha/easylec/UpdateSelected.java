package com.example.gayanlakshitha.easylec;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;

/**
 * Created by Gayan Lakshitha on 29/06/2017.
 */

public class UpdateSelected extends Activity{
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_selected);

        Intent intent = getIntent();
        String id = intent.getExtras().getString("_id");
        final int _id = Integer.parseInt(id);
        final TimePicker tp = (TimePicker)findViewById(R.id.timePicker);
        Button btn_update = (Button)findViewById(R.id.btn_UpdateSelected);
        final Spinner spin_duration = (Spinner)findViewById(R.id.duration);

        ArrayAdapter<CharSequence> adp_dur = ArrayAdapter.createFromResource(this,R.array.durations,android.R.layout.simple_spinner_dropdown_item);
        spin_duration.setAdapter(adp_dur);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int duration = 0;
                int hour,minute;
                switch (spin_duration.getSelectedItem().toString())
                {
                    case "1 Hour": duration=1; break;
                    case "2 Hour": duration=2; break;
                    case "3 Hour": duration=3; break;
                    case "4 Hour": duration=4; break;
                }

                if(Build.VERSION.SDK_INT>=23)
                {
                    hour = tp.getHour();
                    minute = tp.getMinute();
                }
                else
                {
                    hour = tp.getCurrentHour();
                    minute = tp.getCurrentMinute();
                }

               try{
                    SQLiteDatabase db = openOrCreateDatabase("easylec_db.db",MODE_PRIVATE,null);
                    db.execSQL("UPDATE tbl_Schedule SET hour=" + hour + ",minute=" + minute + ",duration=" + duration + " WHERE id=" + _id);
                    Toast.makeText(getApplicationContext(),"Data Updated Successfully!",Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Error in Update Data",Toast.LENGTH_LONG);
                }
            }
        });
    }
}
