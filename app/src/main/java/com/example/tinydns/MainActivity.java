package com.example.tinydns;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private TextView textfield;
    private CronetEngine cronetEngine;
    private CronetHTTPCall croHTTP;
    private Executor croExecutor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create Cronet Enginge for HTTP requests.
        this.cronetEngine = new CronetEngine.Builder(this).build();

        croExecutor = Executors.newSingleThreadExecutor();
        ByteBuffer reqBuffer = ByteBuffer.allocate(400);
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                "https://www.example.com", new CronetHTTPCall(), croExecutor);
        UrlRequest request = requestBuilder.build();
        request.start();
        request.read(reqBuffer);
        byte[] data = new byte[reqBuffer.remaining()]; // Create a byte array to hold the data
        reqBuffer.get(data); // Read bytes into the byte array
        String reqContent = new String(data, StandardCharsets.UTF_8);

        textfield = (TextView) findViewById(R.id.textfield);
        textfield.setText(reqContent);

//        try {
//            textfield.setText("now here");
//            URL url = new URL("http://127.0.0.1:5000");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.getResponseCode();
//
//            StringBuilder response = new StringBuilder();
//            if (respCode == HttpURLConnection.HTTP_OK){
//                textfield.setText("then here" + respCode);
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String line;
//                textfield.setText("then here" + 12);
//                textfield.setText("we actually get here");
//                while ((line = reader.readLine()) != null) {
//                    response.append(line);
//                }
//            } else {
//                response.append("Error" + respCode);
//            }
//            textfield.setText(response);
//
//        }   catch (IOException e) {
//            System.out.println(e);
//        }


    }
    public CronetEngine getCronetEngine() {
        return cronetEngine;
    }
}