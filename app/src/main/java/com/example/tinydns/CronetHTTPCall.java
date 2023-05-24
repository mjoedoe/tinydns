package com.example.tinydns;

import android.util.Log;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

// Implementation taken from https://developer.android.com/guide/topics/connectivity/cronet/start#java

public class CronetHTTPCall extends UrlRequest.Callback {
    private static final String TAG = "tinydns Cronet Stack";


    private static final int BYTE_BUFFER_CAPACITY_BYTES = 64 * 1024;
    private final ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private final WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);


    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {
        Log.i(TAG, "onRedirectReceived method called.");
        // You should call the request.followRedirect() method to continue
        // processing the request.
        request.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) throws Exception {
        int httpStatusCode = info.getHttpStatusCode();

        Log.i(TAG, "onResponseStarted method called. HTTPCode: " + httpStatusCode);
        // You should call the request.read() method before the request can be
        // further processed. The following instruction provides a ByteBuffer object
        // with a capacity of 102400 bytes for the read() method. The same buffer
        // with data is passed to the onReadCompleted() method.
        request.read(ByteBuffer.allocateDirect(BYTE_BUFFER_CAPACITY_BYTES));
    }

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {
        Log.i(TAG, "onReadCompleted method called.");
        // This gets called once the first read in onResponseStarted is completed and will keep
        // getting called until the transmission is completed.
        // You should keep reading the request until there's no more data.
        // Flip the buffer from network order.
        byteBuffer.flip();

        // Read into the channel.
        try {
            receiveChannel.write(byteBuffer);
        } catch (IOException e) {
            android.util.Log.i(TAG, "IOException during ByteBuffer read. Details: ", e);
        }
        // Prepare buffer for next read.
        byteBuffer.clear();
        request.read(byteBuffer);
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        Log.i(TAG, "onSucceeded method called.");
        //val bodyBytes = bytesReceived.toByteArray()
        byte[] bodyBytes = bytesReceived.toByteArray();
        onSucceeded(request, info, bodyBytes);
        //onSucceeded(request, info, bodyBytes)
    }

    public void onSucceeded(UrlRequest request, UrlResponseInfo info, byte[] bodyBytes) {
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {

    }
}
