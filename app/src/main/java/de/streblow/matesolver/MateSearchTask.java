package de.streblow.matesolver;

import android.os.AsyncTask;
import android.widget.TextView;

/**
 * Created by streblow on 04.12.2017.
 */

public class MateSearchTask extends AsyncTask<Object, Void, String> {
    private MateSearch mateSearch;
    public BoardView boardView;
    public TextView textView;

    public MateSearchTask(MateSearch mateSearch, BoardView boardView, TextView textView) {
        super();
        this.mateSearch = mateSearch;
        this.boardView = boardView;
        this.textView = textView;
    }

    @Override
    protected String doInBackground(Object[] objects) {
        return mateSearch.doMateSearch();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        boardView.setText(result);
        textView.setText(result);
    }

}
