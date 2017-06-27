package com.example.saurabharora.talent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private TextView Error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button clickButton = (Button) findViewById(R.id.button);
        clickButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                 username = (EditText) findViewById(R.id.editText);

                 password =  (EditText) findViewById (R.id.editText2);

                 Error  = (TextView) findViewById(R.id.textView);
                try {
                    String LoginResponse = new FetchLoginResponse(username.getText().toString() , password.getText().toString()).execute().get();

                    if (LoginResponse == null)
                    {
                     Error.setText("Invalid User or Password");
                    }
                    else
                    {

                        Intent intent = new Intent(MainActivity.this , Main2Activity.class);
                        intent.putExtra("Token",LoginResponse);
                        startActivity(intent);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    private class FetchLoginResponse extends AsyncTask<Void, Void,String>
    {
             String user = null;
             String pass = null;

        public FetchLoginResponse(String username, String password) {
            user = username;
            pass = password;
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;


            String LoginResponse = null;
            String urlParameters  = "username="+user+"&password="+pass;
            byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );



            try
            {
                URL url = new URL("http://10.0.3.2:8000/login/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                try(DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                    wr.write( postData );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (urlConnection.getResponseCode() >= 200 && urlConnection.getResponseCode() <= 299)
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));

                    StringBuilder sb = new StringBuilder();
                    String output;
                    while((output = br.readLine()) != null)
                    {
                        sb.append(output);
                    }
                    LoginResponse = sb.toString();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return LoginResponse;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }
    }
}
