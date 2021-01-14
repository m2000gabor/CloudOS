package com.meszi007.cloudos;

import android.content.Context;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Output {
    private final String url="http://webprogramozas.inf.elte.hu/hallgatok/sbzbxr/androidTest/hello.php";

    public void start(Context context){
        CronetEngine.Builder engineBuilder = new CronetEngine.Builder(context);
        CronetEngine engine = engineBuilder.build();
        Executor executor = Executors.newSingleThreadExecutor();
        MyCallback callback = new MyCallback();
        UrlRequest.Builder requestBuilder = engine.newUrlRequestBuilder(
                url, callback, executor);
        UrlRequest request = requestBuilder.build();
        request.start();
    }

}
