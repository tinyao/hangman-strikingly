package im.ycz.hangman.api;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by tinyao on 4/24/15.
 */
public class NetClient {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    static OkHttpClient client = new OkHttpClient();

    public static void post(String url, String json, HangmanApi.ACTION actionTag,
                            Callback callback) throws IOException {
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        Log.d("DEBUG", "Request: " + json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .tag(actionTag)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void cancel(HangmanApi.ACTION action) {
        client.cancel(action);
    }
}
