package de.streblow.matesolver;

import android.os.AsyncTask;
import android.widget.TextView;

/**
 * Created by streblow on 04.12.2017.
 */

public class AnalysePositionTask extends AsyncTask<Object, Void, String> {
    private AnalysePosition analysePosition;
    public BoardView boardView;
    public TextView textView;

    public AnalysePositionTask(AnalysePosition analysePosition, BoardView boardView, TextView textView) {
        super();
        this.analysePosition = analysePosition;
        this.boardView = boardView;
        this.textView = textView;
    }

    @Override
    protected String doInBackground(Object[] objects) {
        return analysePosition.doAnalysePosition();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        boardView.setText(result);
        textView.setText(result);
    }

}
