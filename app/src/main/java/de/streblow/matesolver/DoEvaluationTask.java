package de.streblow.matesolver;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

/**
 * Created by streblow on 04.12.2017.
 */

public class DoEvaluationTask extends AsyncTask<Object, Void, String> {
    private Analyse analyse;
    public BoardView boardView;
    public TextView textView;

    public DoEvaluationTask(Analyse analyse, BoardView boardView, TextView textView) {
        super();
        this.analyse = analyse;
        this.boardView = boardView;
        this.textView = textView;
    }

    @Override
    protected String doInBackground(Object[] objects) {
        return analyse.evaluatePosition();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        boardView.setText(result);
        textView.setText(result);
    }

}
