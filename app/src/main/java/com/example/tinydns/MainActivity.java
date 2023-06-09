package com.example.tinydns;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "tinydns";
    private TextView textfield;

    private String ServerURL = "https://172.17.0.2:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Starting app.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textfield = (TextView) findViewById(R.id.textfield);
        textfield.setText("reqContent");

        Thread httpCall = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = makeHttpRequest(ServerURL + "/authenticate");
                    Log.d(TAG, "response" + response);
                    // Perform UI updates on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textfield.setText(response); // Update the UI element
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        httpCall.start();
    }

    private String makeHttpRequest(String urlStr) throws IOException {
        Log.d(TAG, "Starting request.");
        String response = "";
        URL url = new URL(urlStr);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setHostnameVerifier((hostname, session)-> true);
        // Set request method to POST
        connection.setRequestMethod("POST");

        // Enable output for sending data
        connection.setDoOutput(true);

/*        try {
            KeyStore keyStore = getKeyStore(context);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "pasword".toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyManagementException e) {
            e.printStackTrace();
        }*/

        // Optional: Set headers or parameters if needed
        // connection.setRequestProperty("Authorization", "Bearer your_token");

        int responseCode = connection.getResponseCode();
        Log.d(TAG, "Response code: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            response = stringBuilder.toString();
        }

        Log.d(TAG, "finished request");

        connection.disconnect();
        return response;
    }

}