package com.example.tinydns;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private TextView textfield;
    private CronetEngine cronetEngine;
    private Executor croExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textfield = (TextView) findViewById(R.id.textfield);
        textfield.setText("reqContent");

        CronetEngine.Builder builder = new CronetEngine.Builder(this);
        croExecutor = Executors.newSingleThreadExecutor();
        ByteBuffer reqBuffer = ByteBuffer.allocate(400);

        // Load custom X.509 server certificate.
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            FileInputStream certificateFileInputStream = new FileInputStream("/path/to/your/certificate.crt");
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(certificateFileInputStream);
            builder.addRootCertificate(certificate);

        } catch (CertificateException | IOException e) {
            e.printStackTrace();
        }

        // Create Cronet Enginge and executor for HTTP requests.
        this.cronetEngine = builder.build();
        CronetHTTPCall cronetCallback = new CronetHTTPCall() {
            @Override
            public void onSucceeded(UrlRequest request, UrlResponseInfo info, byte[] bodyBytes) {
                // Parse the request result and update the UI.
                //String dataStr = new String(bodyBytes, StandardCharsets.UTF_8);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textfield.setText("Request finished with " + info.getHttpStatusCode());

                        //textfield.setText(dataStr);
                    }
                });

            }
            @Override
            public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textfield.setText("Request finished with " + info.getHttpStatusCode());
                    }
                });
            }
        };
        // @todo cronet supports only https traffic. So server needs to run https. -> Does now but still fails.

        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder("https://https://172.17.0.2:5000", cronetCallback, croExecutor);
        // Add server certificate.

        UrlRequest request = requestBuilder.build();
        request.start();
    }
    public CronetEngine getCronetEngine() {
        return cronetEngine;
    }
}