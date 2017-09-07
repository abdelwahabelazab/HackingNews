package com.abdelwahabelazab.hackingnews;


        import android.app.Fragment;
        import android.content.ContentResolver;
        import android.content.Context;
        import android.content.Intent;
        import android.database.CharArrayBuffer;
        import android.database.ContentObserver;
        import android.database.Cursor;
        import android.database.DataSetObserver;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteException;
        import android.database.sqlite.SQLiteStatement;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.speech.tts.Voice;
        import android.support.annotation.Nullable;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.concurrent.ExecutionException;

/**
 * Created by Alazab on 7/25/2017.
 */
public class Recent extends Fragment {
    public Recent() {
    }

    ListView listView;

    ArrayList<Integer> articlesIDs = new ArrayList<Integer>();
    Map<Integer, String> articlesTitles = new HashMap<Integer, String>();
    Map<Integer, String> articlesUrls = new HashMap<Integer, String>();

    ArrayList<String> titles=new ArrayList<String>();
    ArrayList<String> urls=new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recent, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getActivity().findViewById(R.id.listView);

        adapter= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,titles);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(getActivity(),Article.class);
                i.putExtra("url",urls.get(position));
                i.putExtra("title",titles.get(position));

                startActivity(i);
            }
        });

        MainActivity.articlDB.execSQL("CREATE TABLE IF NOT EXISTS articles(id INTEGER PRIMARY KEY, articleId INTEGER, url VARCHAR, title VARCHAR, content VARCHAR)");

        DownloadTask task = new DownloadTask();
        try {
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
        }
        try {
            Article.update();
        }
        catch (SQLiteException e){

        }

    }


    class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            String result = "";
            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                JSONArray jsonArray = new JSONArray(result);
                MainActivity.articlDB.execSQL("DELETE FROM articles");
                for (int i = 0; i < 15; i++) {
                    String articleId = jsonArray.getString(i);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/"+articleId+".json?print=pretty");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    in = urlConnection.getInputStream();
                    reader = new InputStreamReader(in);
                    data = reader.read();
                    String articleInfo = "";
                    while (data != -1) {
                        char current = (char) data;
                        articleInfo += current;
                        data = reader.read();
                    }
                    JSONObject jsonObject = new JSONObject(articleInfo);
                    String articleTitle = jsonObject.getString("title");
                    String articleUrl = jsonObject.getString("url");
                    articlesIDs.add(Integer.valueOf(articleId));
                    articlesTitles.put(Integer.valueOf(articleId), articleTitle);
                    articlesUrls.put(Integer.valueOf(articleId), articleUrl);

                    String sql = "INSERT INTO articles (articleId, url, title) VALUES (?, ?, ?)";
                    SQLiteStatement statement = MainActivity.articlDB.compileStatement(sql);
                    statement.bindString(1, articleId);
                    statement.bindString(2, articleUrl);
                    statement.bindString(3, articleTitle);
                    statement.execute();

                }

            } catch (Exception e) {
            //    Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            updateListView();
        }
    }

    public void updateListView() {
        Cursor c = MainActivity.articlDB.rawQuery("SELECT * FROM articles ORDER BY articleId DESC",null);
        int articleIDindex=c.getColumnIndex("articleId");
        int urlIndex=c.getColumnIndex("url");
        int titleIndex=c.getColumnIndex("title");
        c.moveToFirst();
        while (c.moveToNext())
        {
            titles.add(c.getString(titleIndex));
            urls.add(c.getString(urlIndex));
        }

        adapter.notifyDataSetChanged();

    }
}
