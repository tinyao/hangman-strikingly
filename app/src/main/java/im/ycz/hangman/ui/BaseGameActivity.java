package im.ycz.hangman.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;

import im.ycz.hangman.api.HangmanApi;
import im.ycz.hangman.api.HangmanResponse;

/**
 * Created by tinyao on 4/24/15.
 */
public abstract class BaseGameActivity extends Activity implements Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
    }

    /**
     * Called when request failed, the original response from okhttp
     * @param request
     * @param e
     */
    @Override
    public void onFailure(Request request, IOException e) {
        Message msg = new Message();
        msg.what = -1;
        if (e.getMessage().equals("sessionId is missing")) {
            msg.obj = "Please login first !";
        } else {
            msg.obj = e.getMessage();
        }
        gameHandler.sendMessage(msg);
        Log.d("DEBUG", "onFailure: " + e.getMessage() +  e.getStackTrace());
    }

    /**
     * Called when request sucess, the original response from okhttp
     * @param response
     * @throws IOException
     */
    @Override
    public void onResponse(Response response) throws IOException {
        if (response.code() == 200) {
            String body = response.body().string();
            Log.d("DEBUG", "Reponse: " + body);
            HangmanApi.ACTION actionTag = (HangmanApi.ACTION) response.request().tag();

            HangmanResponse hmResponse = new Gson().fromJson(
                    body, HangmanResponse.class);
            hmResponse.requst = actionTag;

            Message msg = gameHandler.obtainMessage();
            msg.obj = hmResponse;
            msg.what = 0;
            gameHandler.sendMessage(msg);
        } else {

        }
    }


    MainHandler gameHandler = new MainHandler(this);

    /**
     * Called when request success with json parsed
     * @param hmResponse
     */
    public abstract void onSuccess(HangmanResponse hmResponse);

    /**
     * Called when request failed with error parsed
     * @param msg
     */
    public abstract void onFailed(String msg);


    /**
     * User inner class handler with weak reference to activity, to prevent reference leak
     */
    private static class MainHandler extends Handler {
        private final WeakReference<BaseGameActivity> mActivity;

        public MainHandler(BaseGameActivity activity) {
            mActivity = new WeakReference<BaseGameActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseGameActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 0) {
                    HangmanResponse hmResponse = (HangmanResponse) msg.obj;
                    activity.onSuccess(hmResponse);
                } else if (msg.what == -1) {
                    activity.onFailed((String) msg.obj);
                }

            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUI();
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
