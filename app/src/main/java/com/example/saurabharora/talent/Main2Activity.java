package com.example.saurabharora.talent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.graphics.*;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.*;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private  static String url  = "http://10.0.3.2:8000/posts/";
    String Token = null;
    ArrayList<HashMap<String, Object >> DataList;
    private ListView lv;
    private ProgressDialog pDialog;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String message = intent.getStringExtra("Token");
        Token = "Token" + message.substring(9).replace("}","").replace("\"","");

        DataList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list_item);
        new GetData().execute();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Main2Activity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url,Token);



            if (jsonStr != null) {
                try {

                    JSONArray ListData = new JSONArray(jsonStr);
                    JSONObject jsonObj = ListData.getJSONObject(0);

                    // Getting JSON Array node
                    Log.i("json", jsonObj.getString("title"));


                    // looping through All Data
                    for (int i = 0; i < ListData.length(); i++) {
                        JSONObject c = ListData.getJSONObject(i);

                        String title = c.getString("title");
                        String description = c.getString("description");
                        String media = c.getString("mediaFile");
                        Log.i("df","fd");

                        // tmp hash map for single contact
                        HashMap<String, Object> UrlData = new HashMap<>();
                        try {
                            URL url = new URL (media);
                            try {
                                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        // adding each child node to HashMap key => value

                        ImageView image = null;
                        try {
                            image = getImage(media);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        UrlData.put("media",image);
                        UrlData.put("title", title);
                        UrlData.put("description", description);

                        // adding contact to contact list
                        DataList.add(UrlData);
                    }
                } catch (final JSONException e) {

                        }


                }
             else {



            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    Main2Activity.this, DataList,
                    R.layout.list_item, new String[]{"description", "media",
                    "owner"}, new int[]{R.id.listTitle,
                    R.id.listImage, R.id.listDescription});

            lv.setAdapter(adapter);

        }

    }



public ImageView getImage(String im) throws IOException {

    URL url = new URL(im);
    ImageView imageView = null;
    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
    imageView.setImageBitmap(bmp);
    return imageView;
}




}
