package de.streblow.matesolver;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import chesspresso.Chess;

public class MainActivity extends AppCompatActivity {

    private int mAppMode;
    private int mMateInMoves;
    private boolean mFirstMoveOnly;

    public final String savedStateFilename = "savedState";

    private MateSearchTask mateSearchTask = null;
    private AnalysePositionTask analysePositionTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button clickButton = (Button) findViewById(R.id.btnMode);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoardView boardView = (BoardView) findViewById(R.id.cvBoardView);
                Button btnMode = (Button) findViewById(R.id.btnMode);
                btnMode.setText(getString(R.string.switch_to) + " " + boardView.MODES[boardView.getMode()]);
                boardView.setMode(~boardView.getMode() & 1);
                boardView.setSquareSelected(false);
                boardView.clearText();
                TextView tvLogView = (TextView) findViewById(R.id.tvLogView);
                tvLogView.setText("");
                TextView tvLogLabel = (TextView) findViewById(R.id.tvLogLabel);
                if (boardView.getMode() == 0) {
                    boardView.setMateInAnalyseMode(false);
                    tvLogLabel.setText(getString(R.string.analysis_mode_white));
                    if (boardView.getGame().getTurn())
                        boardView.getGame().changeTurn();
                    if (!boardView.getGame().validateGame(boardView.getBoard())) {
                        boardView.setText(getString(R.string.invalid_position));
                        tvLogView.setText(getString(R.string.invalid_position));
                    }
                    else
                        boardView.getGame().setVictory(false);
                    boardView.setMove(1);
                } else {
                    tvLogLabel.setText("(" + boardView.MODES[boardView.getMode()] + ") - " + getString(R.string.setup_board));
                }
            }
        });
        mMateInMoves = 2;
        mFirstMoveOnly = false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("APPMODE", mAppMode);
        savedInstanceState.putInt("MATEINMOVES", mMateInMoves);
        savedInstanceState.putBoolean("FIRSTMOVEONLY", mFirstMoveOnly);
        BoardView boardView = (BoardView) findViewById(R.id.cvBoardView);
        boardView.saveState(savedInstanceState);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        mAppMode = savedInstanceState.getInt("APPMODE");
        mMateInMoves = savedInstanceState.getInt("MATEINMOVES");
        mFirstMoveOnly = savedInstanceState.getBoolean("FIRSTMOVEONLY");
        BoardView boardView = (BoardView) findViewById(R.id.cvBoardView);
        boardView.restoreState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        final BoardView boardView = (BoardView) findViewById(R.id.cvBoardView);
        final TextView tvLogView = (TextView) findViewById(R.id.tvLogView);
        TextView tvLogLabel = (TextView) findViewById(R.id.tvLogLabel);
        ClipboardManager clipboard;
        ClipData clip;
        switch (item.getItemId())
        {
            case R.id.action_searchmate:
                if (boardView.getMode() == 0)
                    return true;
                boardView.setSquareSelected(false);
                if (!boardView.getGame().validateGame(boardView.getBoard())) {
                    boardView.setText(getString(R.string.invalid_position));
                    tvLogView.setText(getString(R.string.invalid_position));
                } else {
                    MateSearch mateSearch = new MateSearch(boardView.getBoard().getFEN() + " w - - 0 1", mMateInMoves * 2 - 1, Chess.WHITE, mFirstMoveOnly);
                    boardView.setText(getString(R.string.search));
                    tvLogView.setText(getString(R.string.search));
                    mateSearchTask = new MateSearchTask(mateSearch, boardView, tvLogView);
                    mateSearchTask.execute();
                }
                return true;
            case R.id.action_cancel_searchmate:
                if (mateSearchTask == null)
                    return true;
                if (mateSearchTask.getStatus() != AsyncTask.Status.RUNNING)
                    return true;
                mateSearchTask.cancel(true);
                boardView.setText(getString(R.string.search_cancelled));
                tvLogView.setText(getString(R.string.search_cancelled));
                return true;
            case R.id.action_analyseposition:
                if (boardView.getMode() == 1)
                    return true;
                boardView.setSquareSelected(false);
                boardView.setMove(1);
                if (!boardView.getGame().validateGame(boardView.getBoard())) {
                    boardView.setText(getString(R.string.invalid_position));
                    tvLogView.setText(getString(R.string.invalid_position));
                } else {
                    AnalysePosition analysePosition;
                    if (!boardView.getGame().getTurn())
                        analysePosition = new AnalysePosition(boardView.getBoard().getFEN() + " w - - 0 1", mMateInMoves * 2, Chess.WHITE);
                    else
                        analysePosition = new AnalysePosition(boardView.getBoard().getFEN() + " b - - 0 1", mMateInMoves * 2, Chess.BLACK);
                    boardView.setText(getString(R.string.analysis));
                    tvLogView.setText(getString(R.string.analysis));
                    analysePositionTask = new AnalysePositionTask(analysePosition, boardView, tvLogView);
                    analysePositionTask.execute();
                }
                return true;
            case R.id.action_cancel_analyseposition:
                if (analysePositionTask == null)
                    return true;
                if (analysePositionTask.getStatus() != AsyncTask.Status.RUNNING)
                    return true;
                analysePositionTask.cancel(true);
                boardView.setText(getString(R.string.analysis_cancelled));
                tvLogView.setText(getString(R.string.analysis_cancelled));
                return true;
            case R.id.action_switch_side:
                if (boardView.getMode() == 1)
                    return true;
                boardView.setSquareSelected(false);
                boardView.setMove(1);
                String turn = boardView.getGame().changeTurn() + " " + getString(R.string.to_move);
                tvLogLabel.setText("(" + getString(R.string.mode_analysis) + ") - " + turn);
                if (!boardView.getGame().validateGame(boardView.getBoard())) {
                    boardView.setText(getString(R.string.invalid_position));
                    tvLogView.setText(getString(R.string.invalid_position));
                } else {
                    if (boardView.getGame().getTurn()) { // black
                        boardView.setText("\n1. ...");
                        tvLogView.setText("\n1. ...");
                    }
                }
                return true;
            case R.id.action_flipboard:
                boardView.flipBoard();
                boardView.invalidate();
                return true;
            case R.id.action_clearboard:
                if (boardView.getMode() == 0)
                    return true;
                boardView.setSquareSelected(false);
                boardView.getBoard().clearBoard();
                boardView.setText("");
                tvLogView.setText("");
                boardView.invalidate();
                return true;
            case R.id.action_resetboard:
                boardView.setSquareSelected(false);
                boardView.getBoard().resetBoard();
                if (boardView.mBoardFlipped) {
                    boardView.flipBoard();
                    boardView.mBoardFlipped = !boardView.mBoardFlipped;
                }
                boardView.setText("");
                tvLogView.setText("");
                if (boardView.getMode() == 0) {
                    boardView.setGame(new Game(getString(R.string.white), getString(R.string.black)));
                    tvLogLabel.setText(getString(R.string.analysis_mode_white));
                    boardView.setMove(1);
                }
                boardView.invalidate();
            case R.id.action_boardtofile:
                Bitmap board = boardView.drawBoard();
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                        + "/Android/data/"
                        + getApplicationContext().getPackageName()
                        + "/Files");
                if (!mediaStorageDir.exists()){
                    if (!mediaStorageDir.mkdirs()){
                        return true;
                    }
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
                File mediaFile;
                String mImageName="MateSolver"+ timeStamp +".png";
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
                if (mediaFile != null) {
                    try {
                        FileOutputStream fos = new FileOutputStream(mediaFile);
                        board.compress(Bitmap.CompressFormat.PNG, 0, fos);
                        fos.close();
                    } catch (Exception e) {
                        return true;
                    }
                }
                return true;
            case R.id.action_boardtoclipboard:
                clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clip = ClipData.newPlainText("MateSolver", boardView.getBoard().getUnicodeString());
                clipboard.setPrimaryClip(clip);
                return true;
            case R.id.action_texttoclipboard:
                clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clip = ClipData.newPlainText("MateSolver", boardView.getText());
                clipboard.setPrimaryClip(clip);
                return true;
            case R.id.action_settings:
                final OptionsDialog options = new OptionsDialog(this, mMateInMoves, mFirstMoveOnly);
                options.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mMateInMoves = options.getMateInMoves();
                        mFirstMoveOnly = options.getFirstMoveOnly();
                    }
                });
                options.show();
                return true;
            case R.id.action_help:
                HelpDialog help = new HelpDialog(this);
                help.setTitle(R.string.help_title);
                help.show();
                return true;
            case R.id.action_about:
                AboutDialog about = new AboutDialog(this);
                about.setTitle(R.string.about_title);
                about.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveState() {
        BoardView boardView = (BoardView) findViewById(R.id.cvBoardView);
        DataOutputStream dos = null;
        try {
            File file = new File(getFilesDir(), savedStateFilename);
            dos = new DataOutputStream(new FileOutputStream(file));
            dos.writeInt(mAppMode);
            dos.writeInt(mMateInMoves);
            dos.writeBoolean(mFirstMoveOnly);
            boardView.saveState(dos);
            dos.close();
            dos = null;
        } catch (Exception e) {
            e.printStackTrace();
            if (dos != null)
                try {
                    dos.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
        }
    }

    public void restoreState() {
        BoardView boardView = (BoardView) findViewById(R.id.cvBoardView);
        Button btnMode = (Button) findViewById(R.id.btnMode);
        TextView tvLogLabel = (TextView) findViewById(R.id.tvLogLabel);
        TextView tvLogView = (TextView) findViewById(R.id.tvLogView);
        DataInputStream dis = null;
        try {
            File file = new File(getFilesDir(), savedStateFilename);
            dis = new DataInputStream(new FileInputStream(file));
            mAppMode = dis.readInt();
            mMateInMoves = dis.readInt();
            mFirstMoveOnly = dis.readBoolean();
            boardView.restoreState(dis);
            btnMode.setText(getString(R.string.switch_to) + " " + boardView.MODES[(~boardView.getMode() & 1)]);
            tvLogView.setText(boardView.getText());
            if (boardView.getMode() == 0) {
                tvLogLabel.setText(getString(R.string.analysis_mode_white));
                if (boardView.getGame().getTurn())
                    tvLogLabel.setText("(" + boardView.MODES[boardView.getMode()] + ") - " + getString(R.string.black) + " " + getString(R.string.to_move));
                if (!boardView.getGame().validateGame(boardView.getBoard())) {
                    if (!boardView.isMateInAnalyseMode())
                        tvLogView.setText(getString(R.string.invalid_position));
                }
                else
                    boardView.getGame().setVictory(false);
            } else {
                tvLogLabel.setText("(" + boardView.MODES[boardView.getMode()] + ") - " + getString(R.string.setup_board));
            }
            dis.close();
            dis = null;
        } catch (Exception e) {
            e.printStackTrace();
            if (dis != null)
                try {
                    dis.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
        }
    }
}
