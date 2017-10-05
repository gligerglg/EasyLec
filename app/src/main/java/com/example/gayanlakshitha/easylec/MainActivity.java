package com.example.gayanlakshitha.easylec;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    TextView txt_Status;
    Button btn_Start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button add_Schedule = (Button)findViewById(R.id.btn_Add);
        Button update_Schedule = (Button)findViewById(R.id.btn_List);
        Button btn_reset = (Button)findViewById(R.id.btn_Reset);
        btn_Start = (Button)findViewById(R.id.btn_Start);
        Button btn_About = (Button)findViewById(R.id.btn_About);
        txt_Status = (TextView)findViewById(R.id.txt_status);

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
                Toast.makeText(getApplicationContext(),"Service is Started!\nDo not Close the Application",Toast.LENGTH_SHORT).show();
                txt_Status.setText("Status : Service is Started");
                setAlarm();
                //btn_Start.setEnabled(true);
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

    public void setAlarm()
    {
        btn_Start.setEnabled(false);
        Calendar calendar = Calendar.getInstance();
        int dayint = calendar.get(Calendar.DAY_OF_WEEK);
        String day="";

        try
        {
            switch (dayint) {
                case Calendar.MONDAY:
                    day = "Monday";
                    break;
                case Calendar.TUESDAY:
                    day = "Tuesday";
                    break;
                case Calendar.WEDNESDAY:
                    day = "Wednesday";
                    break;
                case Calendar.THURSDAY:
                    day = "Thursday";
                    break;
                case Calendar.FRIDAY:
                    day = "Friday";
                    break;
                case Calendar.SATURDAY:
                    day = "Friday";
                    break;
                case Calendar.SUNDAY:
                    day = "Friday";
                    break;
            }

            SQLiteDatabase db = openOrCreateDatabase("easylec_db.db", MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("SELECT * FROM tbl_Schedule WHERE weekday='" + day + "'", null);

            final ArrayList<Integer> array_diff = new ArrayList<>();
            final ArrayList<Integer> array_dur = new ArrayList<>();



            //Create New ArrayList
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int total_sec = cursor.getInt(2) * 3600 + cursor.getInt(3) * 60;
                int now_time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 3600 + Calendar.getInstance().get(Calendar.MINUTE) * 60;

                if (now_time < total_sec) {
                    array_diff.add(total_sec - now_time);
                    //System.out.println((total_sec - now_time) + "Added to list");
                    array_dur.add(cursor.getInt(4) * 3600);
                }
            }


            int count_rows = array_diff.size();
            int min = (Integer) array_diff.get(0);
            int j = 0;

            //Find minimum value
            for (j = 0; j < count_rows; j++) {
                //System.out.println("Finding Minimum Vlaues :" + array_diff.get(j));
                if ((Integer) array_diff.get(j) < min)
                    min = (Integer) array_diff.get(j);
            }

            System.out.println(min + "Seconds for next Session");
            j = 0;
            while ((Integer) array_diff.get(j) != min) {
                //System.out.println("Searching min Value of : " + array_diff.get(j));
                j++;
            }

            final int index = j;
            //System.out.println(index + "is the index of array");
            new CountDownTimer((Integer) array_diff.get(index) * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    System.out.println("Counting in Normal Mode" + millisUntilFinished);
                    String rem_time = String.format(Locale.getDefault(),"Status : \t%02d H: %02d M: %02d S to Vibrate Mode", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)%60,TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)%60,TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)%60);
                    txt_Status.setText(rem_time);
                }

                @Override
                public void onFinish() {
                    final AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                    new CountDownTimer((Integer) array_dur.get(index) * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            System.out.println("Counting in Vibrate Mode");
                            String rem_time = String.format(Locale.getDefault(),"Status : \t%02d H: %02d M: %02d S to Normal Mode", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)%60,TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)%60,TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)%60);
                            txt_Status.setText(rem_time);
                        }

                        @Override
                        public void onFinish() {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            setAlarm();
                            System.out.println("Index changed as " + array_diff.get(index));
                            txt_Status.setText("Status : Service Stopped");

                        }
                    }.start();
                }
            }.start();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Service is Completed Successfully",Toast.LENGTH_SHORT).show();
            btn_Start.setEnabled(true);
        }
        finally {
            txt_Status.setText("Status : Service Stopped");
        }

    }

}


