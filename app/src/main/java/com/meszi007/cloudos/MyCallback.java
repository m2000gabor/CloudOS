package com.meszi007.cloudos;

import org.chromium.base.Log;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

class MyCallback extends UrlRequest.Callback {
    private final boolean followRedirect=true;
    private final ByteBuffer myBuffer;
    private Map<String, List<String>> mResponseHeaders;

    public MyCallback() {
        myBuffer=ByteBuffer.allocateDirect(210);
    }

    void doSomethingWithResponseData(ByteBuffer byteBuffer){
        System.out.println("sth happened");
        mResponseHeaders.forEach((k,v)->{
            System.out.println(k+" "+v.get(0)+" "+v.size());
        });
        System.out.println(byteBuffer.array().length);

    }

    @Override
    public void onRedirectReceived(UrlRequest request,
                                   UrlResponseInfo responseInfo, String newLocationUrl) {
        if (followRedirect) {
            // Let's tell Cronet to follow the redirect!
            request.followRedirect();
        } else {
            // Not worth following the redirect? Abandon the request.
            request.cancel();
        }
    }

    @Override
    public void onResponseStarted(UrlRequest request,
                                  UrlResponseInfo responseInfo) {
        // Now we have response headers!
        int httpStatusCode = responseInfo.getHttpStatusCode();
        if (httpStatusCode == 200) {
            // Success! Let's tell Cronet to read the response body.
            request.read(myBuffer);
        } else if (httpStatusCode == 503) {
            // Do something. Note that 4XX and 5XX are not considered
            // errors from Cronet's perspective since the response is
            // successfully read.
        }
        mResponseHeaders = responseInfo.getAllHeaders();
    }

    @Override
    public void onReadCompleted(UrlRequest request,
                                UrlResponseInfo responseInfo, ByteBuffer byteBuffer) {
        // Response body is available.
        doSomethingWithResponseData(byteBuffer);
        // Let's tell Cronet to continue reading the response body or
        // inform us that the response is complete!
        request.read(myBuffer);
    }

    @Override
    public void onSucceeded(UrlRequest request,
                            UrlResponseInfo responseInfo) {
        // Request has completed successfully!
    }

    @Override
    public void onFailed(UrlRequest request,
                         UrlResponseInfo responseInfo, CronetException error) {
        // Request has failed. responseInfo might be null.
        Log.e("MyCallback", "Request failed. " + error.getMessage());
        // Maybe handle error here. Typical errors include hostname
        // not resolved, connection to server refused, etc.
    }
}