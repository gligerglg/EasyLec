package com.example.gayanlakshitha.easylec;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Gayan Lakshitha on 27/06/2017.
 */

public class UpdateEntry extends Activity {
    private Button btn_Update;
    private Button btn_Remove;
    private Spinner spin_week;
    private Spinner spin_id;
    private ListView lst_data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_tblentry);

        btn_Update = (Button)findViewById(R.id.btn_Update);
        btn_Remove = (Button)findViewById(R.id.btn_Remove);
        spin_week = (Spinner)findViewById(R.id.spin_week);
        lst_data = (ListView)findViewById(R.id.lst_data);
        spin_id = (Spinner) findViewById(R.id.spin_id);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.week_days,android.R.layout.simple_spinner_dropdown_item);
        spin_week.setAdapter(adapter);

        final SQLiteDatabase db = openOrCreateDatabase("easylec_db.db",MODE_PRIVATE,null);

        spin_week.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String weekday = spin_week.getSelectedItem().toString();
                    Cursor cursor = db.rawQuery("SELECT * FROM tbl_Schedule WHERE weekday='" + weekday + "'",null);
                    String list[] = new String[cursor.getCount()];
                    String[] ids = new String[cursor.getCount()];
                    int count=0;
                    for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
                    {
                        list[count] = "\tID : " + cursor.getString(0) +"\t\tTime : " + cursor.getString(2) + ":" + cursor.getString(3) + "\t\t\tDuration : " + cursor.getString(4) + "H";
                        ids[count] = cursor.getString(0);
                        count++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,list);
                    lst_data.setAdapter(adapter);
                    ArrayAdapter<String> adapter_id = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner,ids);
                    spin_id.setAdapter(adapter_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        btn_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String id = spin_id.getSelectedItem().toString();
                    Intent intent = new Intent(UpdateEntry.this, UpdateSelected.class);
                    intent.putExtra("_id", "" + id);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"No Entry is Selected!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(spin_id.getCount()==0)
                        Toast.makeText(getApplicationContext(),"No Entry is Selected!",Toast.LENGTH_SHORT).show();
                    else {

                        AlertDialog.Builder aleart = new AlertDialog.Builder(UpdateEntry.this);
                        aleart.setTitle("Remove Item");
                        aleart.setMessage("Do You Want To Remove Selected Item?");

                        aleart.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int id = Integer.parseInt(spin_id.getSelectedItem().toString());
                                    db.delete("tbl_Schedule", "id=" + id, null);
                                } catch (SQLException e) {
                                    Toast.makeText(getApplicationContext(), "Database Error!", Toast.LENGTH_SHORT).show();
                                } finally {
                                    Toast.makeText(getApplicationContext(), "Entry Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    finish();
                                    startActivity(getIntent());
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
            }
        });
    }
}
