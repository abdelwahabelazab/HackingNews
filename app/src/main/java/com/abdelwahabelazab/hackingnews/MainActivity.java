package com.abdelwahabelazab.hackingnews;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    static SQLiteDatabase articlDB;

    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout=(RelativeLayout) findViewById(R.id.main_layout);
        MainActivity.articlDB = this.openOrCreateDatabase("Articles", Context.MODE_PRIVATE, null);

        displayRecent();
        try {
            Article.update();
        }
       catch (SQLiteException e){

    }
}

    public void buRecent(View view) {

        displayRecent();


    }

    public void buSaved(View view) {

        Fragment savedFragment=new Saved();
        FragmentManager fragmentManager=getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.relative,savedFragment)
                .commit();
    }
    public void displayRecent(){
        Fragment recentFragment=new Recent();
        FragmentManager fragmentManager=getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.relative,recentFragment)
                .commit();
    }


}
