package com.abdelwahabelazab.hackingnews;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;

/**
 * Created by Alazab on 7/25/2017.
 */
public class Saved extends Fragment {
    public Saved(){}
    ListView listView2;
    AlertDialog.Builder builder;
    static ArrayList<String> savedTitles=new ArrayList<String>();
    static ArrayList<String> savedUrls=new ArrayList<String>();
    ArrayAdapter<String> adapter2;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.saved,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView2=(ListView) getActivity().findViewById(R.id.listView2);
        adapter2=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,savedTitles);
        listView2.setAdapter(adapter2);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String url=savedUrls.get(position);
                String title=savedTitles.get(position);
                Intent intent=new Intent(getActivity(),Article.class);
                intent.putExtra("url",url);
                intent.putExtra("title",title);
                startActivity(intent);
            }
        });


        Article.update();

        listView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(R.drawable.ha)
                        .setTitle("Confirmation")
                        .setMessage("Delete anyway")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String deleting_titles=savedTitles.get(position);
                                savedTitles.remove(position);
                                savedUrls.remove(position);
                                adapter2.notifyDataSetChanged();
                              int count=Delete(deleting_titles);
                                Toast.makeText(getActivity(),count+"deleted",Toast.LENGTH_LONG).show();


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        })
                        .show();
                return true;
            }
        });
    }

    public int Delete(String title){
        String whereArgs[]={title};
        int count=MainActivity.articlDB.delete("savedarticles","title" + " =? ",whereArgs);
        return count;
    }

}
