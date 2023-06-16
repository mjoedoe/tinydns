package com.example.tinydns;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class tinyHttpRequest {
    private static final String TAG = "tinydns";
    private final String ServerURL;
    tinyHttpRequest(String serverUrl){
        this.ServerURL = serverUrl;
    }

    public String makeHttpRequest(String urlStr) {

        String response = "";

        try {
            URL url = new URL(ServerURL + urlStr);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setHostnameVerifier((hostname, session)-> true);

            if (Objects.equals(urlStr, "/authenticate")){
                Log.d(TAG, "makeHttpRequest: authenticate");
                // Set request method to POST
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                // Enable output for sending data
                connection.setDoOutput(true);
                String parameters = "";
                try {
                    // Create a JSON object with the authentication data
                    JSONObject jsonParams = new JSONObject();
                    jsonParams.put("username", "admin");
                    jsonParams.put("password", "password");

                    // Convert the JSON object to a string
                    parameters = jsonParams.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Write parameter data to the connection
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(parameters);
                outputStream.flush();
                outputStream.close();

            } else if ( Objects.equals(ServerURL, "https://www.virustotal.com")) {
                Log.d(TAG, "makeHttpRequest: vt api");
                connection.setRequestMethod("GET");
                connection.setRequestProperty("x-apikey", "8b8678da15a4914750bb15197396717147bb9af6b11461651ecb2b47ff263a06");
                connection.setRequestProperty("Content-Type", "application/json");
                Log.d(TAG, "makeHttpRequest: "+ url.toString());
            } else {
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
            }

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Response code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response content.
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

        } catch (IOException e) {

            Log.d(TAG, "request failed");
            e.printStackTrace();
        }

        return response;
    }
}
