package im.ycz.hangman.api;

import android.util.Log;
import android.view.View;

import com.squareup.okhttp.Callback;

import java.io.IOException;

/**
 * Created by tinyao on 4/24/15.
 */
public class HangmanApi {

    public static final String BASE_URL = "https://strikingly-hangman.herokuapp.com/game/on";

    public static enum ACTION {
        START_GAME("startGame"),
        NEXT_WORD("nextWord"),
        GUESS_WORD("guessWord"),
        GET_RESULT("getResult"),
        SUBMIT_RESULT("submitResult");

        private final String text;

        private ACTION(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private static void request(String json, HangmanApi.ACTION action, Callback callback) {
        try {
            NetClient.post(HangmanApi.BASE_URL, json, action, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startGame(String playerId, Callback callback) {
        String json = "{\"playerId\": \"" + playerId + "\","
                + "\"action\": \"" + HangmanApi.ACTION.START_GAME + "\""
                + "}";
        Log.d("DEBUG", "json: " + json);
        request(json, HangmanApi.ACTION.START_GAME, callback);
    }

    public static void nextWord(String sessionId, Callback callback){
        String json = "{\"sessionId\": \"" + sessionId + "\","
                + "\"action\": \"" + HangmanApi.ACTION.NEXT_WORD + "\""
                + "}";
        request(json, HangmanApi.ACTION.NEXT_WORD, callback);
    }

    public static void guessWord(String sessionId, String letter, Callback callback) {
        String json = "{\"sessionId\": \"" + sessionId + "\","
                + "\"action\": \"" + HangmanApi.ACTION.GUESS_WORD + "\","
                + "\"guess\": \"" + letter + "\""
                + "}";
        request(json, HangmanApi.ACTION.GUESS_WORD, callback);
    }

    public static void getResult(String sessionId, Callback callback) {
        String json = "{\"sessionId\": \"" + sessionId + "\","
                + "\"action\": \"" + HangmanApi.ACTION.GET_RESULT + "\""
                + "}";
        request(json, HangmanApi.ACTION.GET_RESULT, callback);
    }

    public static void submitResult(String sessionId, Callback callback) {
        String json = "{\"sessionId\": \"" + sessionId + "\","
                + "\"action\": \"" + HangmanApi.ACTION.GET_RESULT + "\""
                + "}";
        request(json, HangmanApi.ACTION.SUBMIT_RESULT, callback);
    }

}
