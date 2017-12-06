package de.streblow.matesolver;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.NumberPicker;

import java.text.DecimalFormat;

public class OptionsDialog extends Dialog implements DialogInterface {

    private static Context mContext = null;
    private int mMateInMoves;
    private boolean mFirstMoveOnly;

    public OptionsDialog(Context context, int mateinmoves, boolean firstmoveonly) {
        super(context);
        mContext = context;
        mMateInMoves = mateinmoves;
        mFirstMoveOnly = firstmoveonly;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_options);
        setTitle(mContext.getResources().getString(R.string.options_title));
        NumberPicker np = (NumberPicker) findViewById(R.id.npMateInMoves);
        String[] nums = new String[6];
        for(int i=0; i<nums.length; i++)
            nums[i] = new DecimalFormat("0").format(i + 1);
        np.setMinValue(1);
        np.setMaxValue(6);
        np.setDisplayedValues(nums);
        np.setValue(mMateInMoves);
        np.setWrapSelectorWheel(true);
        CheckBox cb = (CheckBox) findViewById(R.id.cbFirstMoveOnly);
        cb.setChecked(mFirstMoveOnly);
    }

    public int getMateInMoves() {
        NumberPicker np = (NumberPicker) findViewById(R.id.npMateInMoves);
        return np.getValue();
    }

    public boolean getFirstMoveOnly() {
        CheckBox cb = (CheckBox) findViewById(R.id.cbFirstMoveOnly);
        return cb.isChecked();
    }

}
