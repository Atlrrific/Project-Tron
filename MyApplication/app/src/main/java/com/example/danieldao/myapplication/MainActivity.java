package com.example.danieldao.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.EntityUtils;


public class MainActivity extends Activity {
    public static final String URL = "http://104.131.15.54:5000/";
    public static final String TAG = "TRONMainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProjectTronTask task = new ProjectTronTask();
        task.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class ProjectTronTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls){
            String response = "";
            DefaultHttpClient client= new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL);

            try{
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));

                String s = "";

                while((s = buffer.readLine()) != null){
                    response += s;
                }

            }

            catch (Exception e){
                Log.d(TAG, e.toString());
            }

            Log.d(TAG + " RESPONSE - ", response);
            return response;
        }
    }
}
