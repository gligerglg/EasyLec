package com.example.gayanlakshitha.easylec;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    int id;
    int min_hour;
    String day;
    Calendar calendar = Calendar.getInstance();
    int dayint = calendar.get(Calendar.DAY_OF_WEEK);
    Button btn_Start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button add_Schedule = (Button)findViewById(R.id.btn_Add);
        Button update_Schedule = (Button)findViewById(R.id.btn_List);
        final Button btn_reset = (Button)findViewById(R.id.btn_Reset);
        btn_Start = (Button)findViewById(R.id.btn_Start);
        Button btn_About = (Button)findViewById(R.id.btn_About);

        SharedPreferences sharedPreferences = getSharedPreferences("reclec",0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final SQLiteDatabase easylec_db = openOrCreateDatabase("easylec_db.db",MODE_PRIVATE,null);
        easylec_db.execSQL("CREATE TABLE IF NOT EXISTS tbl_Schedule(id INTEGER PRIMARY KEY AUTOINCREMENT,weekday text,hour int,minute int,duration int)");

        add_Schedule.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent_addEntry = new Intent(MainActivity.this,AddEntry.class);
                startActivity(intent_addEntry);
            }
        });

        update_Schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_update = new Intent(MainActivity.this,UpdateEntry.class);
                startActivity(intent_update);
            }
        });

        btn_About.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_about = new Intent(MainActivity.this,About.class);
                startActivity(intent_about);
            }
        });

        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(),LecService.class));
                btn_Start.setEnabled(false);
                editor.putBoolean("status",true);
                editor.commit();
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder aleart = new AlertDialog.Builder(MainActivity.this);
                aleart.setTitle("Reset Schedule");
                aleart.setMessage("Do You Want To Reset Data?");

                aleart.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            easylec_db.execSQL("DROP TABLE IF EXISTS tbl_Schedule");
                            easylec_db.execSQL("CREATE TABLE IF NOT EXISTS tbl_Schedule(id INTEGER PRIMARY KEY AUTOINCREMENT,weekday text,hour int,minute int,duration int)");
                            Toast.makeText(getApplicationContext(),"All Entries Deleted Successfully!",Toast.LENGTH_SHORT).show();
                        }catch (SQLException e){
                            Toast.makeText(getApplicationContext(),"Database Error!",Toast.LENGTH_SHORT).show();
                        }
                        finally {
                            dialog.dismiss();
                        }

                    }
                });

                aleart.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                aleart.create().show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("reclec",0);
        boolean status = sharedPreferences.getBoolean("status",false);
        if(status)
            btn_Start.setEnabled(false);
        else
            btn_Start.setEnabled(true);
        }

}


