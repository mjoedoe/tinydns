package com.example.tinydns;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

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
        textfield = (TextView) findViewById(R.id.textfield);
        textfield.setText("reqContent");
        // Create Cronet Enginge for HTTP requests.
        this.cronetEngine = new CronetEngine.Builder(this).build();
        croExecutor = Executors.newSingleThreadExecutor();
        ByteBuffer reqBuffer = ByteBuffer.allocate(400);
        CronetHTTPCall cronetCallback = new CronetHTTPCall() {
            @Override
            public void onSucceeded(UrlRequest request, UrlResponseInfo info, byte[] bodyBytes) {
                // Parse the request result and update the UI.
            }
        };
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder("https://www.example.com", cronetCallback, croExecutor);
        UrlRequest request = requestBuilder.build();
        request.start();
    }
    public CronetEngine getCronetEngine() {
        return cronetEngine;
    }
}