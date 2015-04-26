package im.ycz.hangman.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import im.ycz.hangman.R;
import im.ycz.hangman.adapter.FakeKeyboardAdapter;
import im.ycz.hangman.api.*;
import im.ycz.hangman.module.Hangman;
import im.ycz.hangman.ui.dialog.ResultDailog;
import im.ycz.hangman.ui.view.LetterSpacingTextView;

public class MainActivity extends BaseGameActivity implements ResultDailog.OnActionClickListener {

    /**
     * Word TextView for Guessing
     */
    private LetterSpacingTextView wordTv;

    /**
     * Remaining count of wrong guess, for current word
     */
    private TextView remainingTryTv;

    /**
     * Score
     */
    private TextView mScoreTv;

    private static Hangman hangman;

    /**
     * Use for keyboard, each grid for a column
     */
    private GridView keyboradOne, keyboradTwo, keyboradThree;

    private ProgressBar waitingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        findViews();

        // Init adapter to show keyboard
        keyboradOne.setAdapter(new FakeKeyboardAdapter(this, 0));
        keyboradTwo.setAdapter(new FakeKeyboardAdapter(this, 1));
        keyboradThree.setAdapter(new FakeKeyboardAdapter(this, 2));

        hangman = Hangman.getInstance();
        hangman.setCallback(this);
        mScoreTv.setText("" + hangman.mScore.score);

        nextWord();
    }

    private void findViews() {
        wordTv = (LetterSpacingTextView) findViewById(R.id.tv_going_word);
        wordTv.setSpacing(30);
        remainingTryTv = (TextView) findViewById(R.id.tv_remaining_try);
        waitingBar = (ProgressBar) findViewById(R.id.pb_request_waiting);
        mScoreTv = (TextView) findViewById(R.id.tv_total_score);
        keyboradOne = (GridView) findViewById(R.id.grid_keyboard_1);
        keyboradTwo = (GridView) findViewById(R.id.grid_keyboard_2);
        keyboradThree = (GridView) findViewById(R.id.grid_keyboard_3);

        keyboradOne.setOnItemClickListener(itemListener);
        keyboradTwo.setOnItemClickListener(itemListener);
        keyboradThree.setOnItemClickListener(itemListener);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * Listener to handle keyboard item click in GridView
     */
    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

            // Prevent new guess, when previous guess is requesting
            if (waitingBar.getVisibility() == View.VISIBLE) {
                if (wordTv.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            R.string.toast_wait_word, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            R.string.toast_wait_letter, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            String letter = (String) parent.getAdapter().getItem(position);
            guessLetter(letter);

            // When Click the Key Item, fade & scale out it
            Animation fadeAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.key_fade_out);
            fadeAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(fadeAnim);
        }
    };

    /**
     * Next word for guess.
     * Invalidate Views for the keyboard, for some items disappered.
     */
    public void nextWord() {
        keyboradOne.invalidateViews();
        keyboradTwo.invalidateViews();
        keyboradThree.invalidateViews();
        waitingBar.setVisibility(View.VISIBLE);
        wordTv.setText("");
        hangman.nextWord();
    }

    /**
     * Guess letter.
     * @param letter letter to guess
     */
    public void guessLetter(String letter) {
        waitingBar.setVisibility(View.VISIBLE);
        hangman.guessWord(letter);
        if (hangman.getRemainingWrongGuess() >= 0) {
            remainingTryTv.setText("" + hangman.getRemainingWrongGuess());
        }
    }

    private void getResult() {
        loading(R.string.loading_get_result);
        hangman.getResult();
    }

    /**
     * Called when got the data respond.
     * @param hmResponse Parse from Json reponse
     */
    @Override
    public void onSuccess(HangmanResponse hmResponse) {

        // Judge which request action that the response serves
        switch (hmResponse.requst) {
            case NEXT_WORD:
                String word = (String) hmResponse.data.get("word");
                hangman.mScore.totalWordCount = ((Double) hmResponse.data.get("totalWordCount")).intValue();
                hangman.mScore.totalWrongGuessCount = ((Double) hmResponse.data.get("wrongGuessCountOfCurrentWord")).intValue();
                wordTv.setText(word);
                break;

            case GUESS_WORD:
                String word2 = (String) hmResponse.data.get("word");
                hangman.mScore.totalWordCount = ((Double) hmResponse.data.get("totalWordCount")).intValue();
                hangman.mScore.totalWrongGuessCount = ((Double) hmResponse.data.get("wrongGuessCountOfCurrentWord")).intValue();

                // Cache hangman
                hangman.save(MainActivity.this);

                // Check if the guess is correct
                if (word2.equals(wordTv.getText().toString())) {
                    hangman.wrongGuess();
                    remainingTryTv.setText("" + hangman.getRemainingWrongGuess());
                }
                // Check if word completed
                if (!word2.contains("*")) {
                    getResult();
                } else {
                    if (hangman.isDead()) {
                        getResult();
                    }
                }
                wordTv.setText(word2);
                break;
            case GET_RESULT:
                hangman.mScore.score = ((Double) hmResponse.data.get("score")).intValue();
                hangman.mScore.totalWordCount = ((Double) hmResponse.data.get("totalWordCount")).intValue();
                hangman.mScore.correctWordCount = ((Double) hmResponse.data.get("correctWordCount")).intValue();
                hangman.mScore.totalWrongGuessCount = ((Double) hmResponse.data.get("totalWrongGuessCount")).intValue();

                // Cache hangman
                hangman.save(MainActivity.this);

                mScoreTv.setText("" + hangman.mScore.score);
                loadingDialog.dismiss();
                showResult(hangman);

                break;
            case SUBMIT_RESULT:
                Toast.makeText(MainActivity.this,
                        R.string.toast_submit_done, Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                break;
            default:
                break;
        }
        waitingBar.setVisibility(View.GONE);
    }

    /**
     * Called when request failed
     * @param msg
     */
    @Override
    public void onFailed(String msg) {
        waitingBar.setVisibility(View.GONE);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private ProgressDialog loadingDialog;
    private ResultDailog dialog;

    /**
     * Show loading dialog, prevent exiting immerse mode
     * @param resid
     */
    private void loading(int resid) {
        loadingDialog.setMessage(getResources().getString(resid));
        loadingDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        loadingDialog.show();
        loadingDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
        loadingDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    /**
     * Show the result score dialog
     * @param hangman
     */
    private void showResult(Hangman hangman) {
        if (dialog == null) {
            dialog = new ResultDailog(this);
            dialog.setActionListener(this);
        }

        if (isCheckScore) {
            dialog.setCanceledOnTouchOutside(true);
            dialog.setTitle("");
            isCheckScore = !isCheckScore;
        } else {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setTitle(hangman.isDead() ?
                    getResources().getString(R.string.game_word_dead) :
                    getResources().getString(R.string.game_word_correct) + wordTv.getText());
        }
        dialog.setResultScore(hangman.mScore);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(!wordTv.getText().toString().contains("*")) nextWord();
            }
        });
        dialog.show();
    }


    public void exitGame(View view) {
        finish();
    }

    /**
     * callback from ResultDailog, action to submit.
     */
    @Override
    public void onSubmitClick() {
        loadingDialog.setMessage(getResources().getString(R.string.loading_submit));
        loading(R.string.loading_submit);
        hangman.submitResult();
    }

    /**
     * callback from ResultDailog, action to exit.
     */
    @Override
    public void onExitClick() {
        finish();
    }

    /**
     * callback from ResultDailog, action to get next word
     */
    @Override
    public void onNextClick() {
        nextWord();
    }

    /**
     * Check result when click the scoreView
     */
    private boolean isCheckScore = false;
    public void checkScore(View view) {
        isCheckScore = true;
        getResult();
    }

    /**
     * Cancel network request when distroy
     */
    @Override
    protected void onDestroy() {
        NetClient.cancel(HangmanApi.ACTION.GUESS_WORD);
        NetClient.cancel(HangmanApi.ACTION.NEXT_WORD);
        NetClient.cancel(HangmanApi.ACTION.SUBMIT_RESULT);
        super.onDestroy();
    }


}
