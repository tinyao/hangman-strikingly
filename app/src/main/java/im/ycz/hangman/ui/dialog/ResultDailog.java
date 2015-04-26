package im.ycz.hangman.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import im.ycz.hangman.R;
import im.ycz.hangman.module.Score;

/**
 * Created by tinyao on 4/25/15.
 */
public class ResultDailog extends Dialog {

    private Score mScore;
    private String title;
    private TextView titleView, mScoreTv, totalWordTv, correctWordTv, wrongGuessTv;
    private Button submitBtn, exitBtn, nextBtn;

    /**
     * Listener for button click event in Result Dailog.
     */
    private OnActionClickListener actionListener;



    public ResultDailog(Context context) {
        super(context, R.style.ResultDailog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_dialog);

        // Set dialog width, wider than default
        getWindow().setLayout(getContext().getResources().getDimensionPixelSize(R.dimen.width_dialog_result),
                WindowManager.LayoutParams.WRAP_CONTENT);

        titleView = (TextView) findViewById(R.id.tv_dialog_title);
        mScoreTv = (TextView) findViewById(R.id.tv_dialog_score);
        totalWordTv = (TextView) findViewById(R.id.tv_dialog_total_words);
        correctWordTv = (TextView) findViewById(R.id.tv_dialog_correct_words);
        wrongGuessTv = (TextView) findViewById(R.id.tv_dialog_wrong_guess);

        submitBtn = (Button) findViewById(R.id.btn_dialog_submit);
        exitBtn = (Button) findViewById(R.id.btn_dialog_exit_game);
        nextBtn = (Button) findViewById(R.id.btn_dialog_next_word);

        nextBtn.setOnClickListener(listener);
        exitBtn.setOnClickListener(listener);
        submitBtn.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_dialog_exit_game:
                    actionListener.onExitClick();
                    break;
                case R.id.btn_dialog_next_word:
                    actionListener.onNextClick();
                    break;
                case R.id.btn_dialog_submit:
                    AlertDialog alert = new AlertDialog.Builder(ResultDailog.this.getContext())
                            .setTitle("Sure to submit ?")
                            .setPositiveButton(R.string.hint_yes, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    actionListener.onSubmitClick();
                                }
                            })
                            .setNegativeButton(R.string.hint_cancel, null)
                            .create();
                    alert.show();
                    break;
            }
            dismiss();
        }
    };

    @Override
    public void show() {
        super.show();
        hideSystemUI();

        if( title == null || title.isEmpty()) {
            findViewById(R.id.lay_dialog_title).setVisibility(View.GONE);
        } else {
            findViewById(R.id.lay_dialog_title).setVisibility(View.VISIBLE);
            titleView.setText(title);
        }

        if (mScore == null) return;
        mScoreTv.setText("" + mScore.score);
        totalWordTv.setText("" + mScore.totalWordCount);
        correctWordTv.setText("" + mScore.correctWordCount);
        wrongGuessTv.setText("" + mScore.totalWrongGuessCount);
    }

    /**
     * Set button click listener
     * @param actListener
     */
    public void setActionListener(OnActionClickListener actListener) {
        this.actionListener = actListener;
    }

    /**
     * Set score hold by dialog
     * @param score
     */
    public void setResultScore(Score score) {
        this.mScore = score;
    }

    public void setTitle(String title) {
        this.title = title;
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

    /**
     * Interface for button click event in Result Dailog
     */
    public static interface OnActionClickListener {
        public void onSubmitClick();
        public void onExitClick();
        public void onNextClick();
    }

}
