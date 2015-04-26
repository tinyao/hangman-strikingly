package im.ycz.hangman.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import im.ycz.hangman.R;
import im.ycz.hangman.api.HangmanApi;
import im.ycz.hangman.api.HangmanResponse;
import im.ycz.hangman.api.NetClient;
import im.ycz.hangman.module.Hangman;


public class HomeActivity extends BaseGameActivity {

    private Hangman cachedHangman;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // When came/back to home activity, load cache hangman
        // keep the "RESUME GAME" the latest hangman ever played
        cachedHangman = Hangman.getInstance();
        cachedHangman.setCallback(this);
        cachedHangman.fromCache(this);
        if (!cachedHangman.isGameOver()) {
            findViewById(R.id.btn_resume_game).setVisibility(View.VISIBLE);
        }
    }

    public void startGame(View view) {
        SharedPreferences sp = this.getSharedPreferences("game", MODE_PRIVATE);
        String playerId = sp.getString("playerId", "");
        if (playerId.isEmpty()) {
            final EditText editText = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.hint_enter_playerId)
                    .setView(editText)
                    .setPositiveButton(R.string.game_start, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newGame(editText.getText().toString());
                        }
                    })
                    .setNegativeButton(R.string.hint_cancel, null)
                    .create();
            dialog.show();
        } else {
            newGame(playerId);
        }
    }

    /**
     * Start a new game with playerID
     * @param playerId
     */
    public void newGame(String playerId) {
        // show progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.loading_new_game));
        pd.show();

        // start a new hangman
        final Hangman hangman = Hangman.newGame();
        hangman.setPlayerId(playerId);
        hangman.setCallback(this);
        hangman.startGame();

        // Cancel "start game" when progress dialog was canceled
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                NetClient.cancel(HangmanApi.ACTION.START_GAME);
            }
        });
    }

    /**
     * Jump to the cached hangman, with existed session
     * @param view
     */
    public void resumeGame(View view) {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Request success callback, here mainly "START_GAME" action
     * @param hmResponse
     */
    @Override
    public void onSuccess(HangmanResponse hmResponse) {
        switch (hmResponse.requst) {
            case START_GAME:
                Log.d("DEBUG", "New Session");
                Hangman.getInstance().setSessionId(hmResponse.sessionId);
                Hangman.getInstance().save(HomeActivity.this);
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                pd.dismiss();
                break;
        }
    }

    /**
     * Request failed
     * @param msg
     */
    @Override
    public void onFailed(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        NetClient.cancel(HangmanApi.ACTION.START_GAME);
        super.onDestroy();
    }
}
