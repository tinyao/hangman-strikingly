package im.ycz.hangman.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.okhttp.Callback;

import java.io.IOException;

import im.ycz.hangman.api.HangmanApi;

/**
 * Created by tinyao on 4/24/15.
 */
public class Hangman {

    /**
     * Session for for every hangman game
     */
    private String sessionId;

    /**
     * Player id
     */
    private String playerId;

    /**
     * Time the game session initial start
     */
    private long startTime;

    /**
     * Last Time when exit game
     */
    private long exitTime;

    /**
     * Whether score submit, that's whether the session game is over
     */
    private boolean gameOver = false;

    /**
     * Wrong tries for the current word
     */
    private int currentWordIncorretGuess = 0;

    /**
     * Score for this session
     */
    public Score mScore = new Score();

    /**
     * Callback for action response
     */
    private Callback callback;

    /**
     * Single hangman instance
     */
    private static Hangman instance;

    public static synchronized Hangman getInstance() {
        if (instance == null) {
            instance = new Hangman();
        }
        return instance;
    }

    /**
     * Abort the previous instance, create a new single instance
     * @return Hangman
     */
    public static Hangman newGame() {
        return instance = new Hangman();
    }

    public void setPlayerId(String player) {
        this.playerId = player;
    }

    public void setCallback(Callback cback) {
        this.callback = cback;
    }

    public void setSessionId(String session) {
        this.sessionId = session;
    }

    public int getRemainingWrongGuess() {
        return 10 - currentWordIncorretGuess;
    }

    /**
     * Wrong Guess to increase
     */
    public void wrongGuess() {
        currentWordIncorretGuess++;
    }

    public void startGame() {
        HangmanApi.startGame(playerId, callback);
    }

    public void nextWord() {
        currentWordIncorretGuess = 0;
        if (isLogin()) {
            callback.onFailure(null, new IOException("sessionId is missing"));
        }
        HangmanApi.nextWord(sessionId, callback);
    }

    public void guessWord(String letter) {
        if (isLogin()) {
            callback.onFailure(null, new IOException("sessionId is missing"));
        }
        HangmanApi.guessWord(sessionId, letter, callback);
    }

    public void getResult() {
        if (isLogin()) {
            callback.onFailure(null, new IOException("sessionId is missing"));
        }
        HangmanApi.getResult(sessionId, callback);
    }

    public void submitResult() {
        if (isLogin()) {
            callback.onFailure(null, new IOException("sessionId is missing"));
        }
        HangmanApi.submitResult(sessionId, callback);
    }

    public boolean isDead() {
        return currentWordIncorretGuess == 10;
    }


    private boolean isLogin() {
        return sessionId == null || sessionId.isEmpty();
    }

    public boolean isGameOver() {
        return gameOver;
    }


    /**
     * Cache the current game session
     * @param context
     */
    public void save(Context context) {
        SharedPreferences sp = context.getSharedPreferences("game", Context.MODE_PRIVATE);
        sp.edit().putString("sessionId", sessionId)
                .putString("playerId", playerId)
                .putInt("score", mScore.score)
                .putInt("totalWordCount", mScore.totalWordCount)
                .putInt("correctWordCount", mScore.correctWordCount)
                .putInt("totalWrongGuessCount", mScore.totalWrongGuessCount)
                .putLong("startTime", startTime)
                .putLong("exitTime", exitTime)
                .putBoolean("gameOver", gameOver)
                .commit();
    }

    /**
     * Recover a hangman session from local cache
     * @param context
     */
    public void fromCache(Context context) {
        SharedPreferences sp = context.getSharedPreferences("game", Context.MODE_PRIVATE);
        sessionId = sp.getString("sessionId", "");
        playerId = sp.getString("playerId", "");
        mScore.score = sp.getInt("score", 0);
        mScore.totalWordCount = sp.getInt("totalWordCount", 0);
        mScore.correctWordCount = sp.getInt("correctWordCount", 0);
        mScore.totalWrongGuessCount = sp.getInt("totalWrongGuessCount", 0);
        gameOver = sp.getBoolean("gameOver", true);
        startTime = sp.getLong("startTime", 0);
        exitTime = sp.getLong("exitTime", 0);
    }

}
