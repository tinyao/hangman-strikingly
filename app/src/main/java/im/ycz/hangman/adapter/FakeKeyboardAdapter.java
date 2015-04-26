package im.ycz.hangman.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import im.ycz.hangman.R;

/**
 * Created by tinyao on 4/24/15.
 */
public class FakeKeyboardAdapter extends BaseAdapter {

    private final String[][] LETTERS = {
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"Z", "X", "C", "V", "B", "N", "M"}
    };

    private Context mContext;

    private String[] coletters;
    private LayoutInflater mLayoutInflater;

    public FakeKeyboardAdapter(Context context, int rowId) {
        this.mContext = context;
        mLayoutInflater = ((Activity) context).getLayoutInflater();
        coletters = LETTERS[rowId];
     }

    @Override
    public int getCount() {
        return coletters.length;
    }

    @Override
    public Object getItem(int position) {
        return coletters[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = (TextView) mLayoutInflater.inflate(R.layout.key_item_view, null);
            textView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    mContext.getResources().getDimensionPixelSize(R.dimen.height_keyboard_item)));
        } else {
            textView = (TextView) convertView;
        }
        textView.setVisibility(View.VISIBLE);
        textView.setText(coletters[position]);
        return textView;
    }



}
