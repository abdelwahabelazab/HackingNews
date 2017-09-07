package com.abdelwahabelazab.hackingnews;


        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteException;
        import android.database.sqlite.SQLiteStatement;
        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.webkit.WebView;
        import android.webkit.WebViewClient;
        import android.widget.Toast;

public class Article extends AppCompatActivity {

    WebView webView;
    String SameTitle;
    String SameUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=this.getIntent();
        MainActivity.articlDB.execSQL("CREATE TABLE IF NOT EXISTS savedarticles(id INTEGER PRIMARY KEY, articleId INTEGER, url VARCHAR, title VARCHAR)");
        final String url=intent.getStringExtra("url");
        SameUrl=url;
        final String title=intent.getStringExtra("title");
        SameTitle=title;
        webView=(WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if (Saved.savedTitles.contains(title)){
                    Toast.makeText(getApplicationContext(),"Already saved",Toast.LENGTH_LONG).show();

                }
                else {
                    Saved.savedTitles.add(title);
                    Saved.savedUrls.add(url);
                    String sql="INSERT INTO savedarticles (url, title) VALUES (?, ?)";
                    SQLiteStatement statement=MainActivity.articlDB.compileStatement(sql);
                    statement.bindString(1,url);
                    statement.bindString(2,title);
                    statement.execute();
                    Toast.makeText(getApplicationContext(),"Successfully Saved",Toast.LENGTH_LONG).show();
                }

            }


        });
    }


    public static void update(){
        Saved.savedTitles.clear();


        try {

            Cursor cursor = MainActivity.articlDB.rawQuery("SELECT * FROM savedarticles", null);
            int savedTitleIndex = cursor.getColumnIndex("title");
            int savedUrlIndex = cursor.getColumnIndex("url");
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                Saved.savedTitles.add(cursor.getString(savedTitleIndex));
                Saved.savedUrls.add(cursor.getString(savedUrlIndex));
            }
        }
        catch (Exception e){

        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if (id == R.id.save){
            if (Saved.savedTitles.contains(SameTitle)){
                Toast.makeText(getApplicationContext(),"Already saved",Toast.LENGTH_LONG).show();

            }
            else {
                Saved.savedTitles.add(SameTitle);
                Saved.savedUrls.add(SameUrl);
                String sql="INSERT INTO savedarticles (url, title) VALUES (?, ?)";
                SQLiteStatement statement=MainActivity.articlDB.compileStatement(sql);
                statement.bindString(1,SameUrl);
                statement.bindString(2,SameTitle);
                statement.execute();
                Toast.makeText(getApplicationContext(),"Successfully Saved",Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
