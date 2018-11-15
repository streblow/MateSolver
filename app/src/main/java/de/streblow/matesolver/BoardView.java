package de.streblow.matesolver;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BoardView extends View {

    public String[] MODES = {"Analysis Mode", "Mate Search"};

    private final String[] columns = {"a", "b", "c", "d", "e", "f", "g", "h"};

    private int mMode;
    private int mSquareSize;
    private Paint mPaint;
    private Rect mRect;
    private Board mBoard;
    private Game mGame;
    private int mMove;
    private boolean mSquareSelected;
    private int mSelectedRow;
    private int mSelectedColumn;
    private String mText;
    private boolean mMateInAnalyseMode;
    public boolean mBoardFlipped;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        MODES[0] = getContext().getString(R.string.mode_analysis);
        MODES[1] = getContext().getString(R.string.mode_mate_search);
        setSaveEnabled(true);
        init();
    }

    private void init() {
        mMode = 0; // analysis mode
        mSquareSize = getWidth() / 10;
        mPaint = new Paint();
        mRect = new Rect();
        mBoard = new Board(getContext(), mSquareSize);
        mGame = new Game(getContext().getString(R.string.white), getContext().getString(R.string.black));
        mMove = 1;
        mSquareSelected = false;
        mSelectedRow = -1;
        mSelectedColumn = -1;
        mText = "";
        mMateInAnalyseMode = false;
        mBoardFlipped = false;
    }

    public void flipBoard() {
        mBoardFlipped = !mBoardFlipped;
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(getBoard().hasPiece(i, j)) {
                    if (mBoardFlipped)
                        mBoard.getSquare(i, j).setLocationXY(7 - i, 7 - j);
                    else
                        mBoard.getSquare(i, j).setLocationXY(i, j);
                }
            }
        }
        if (mSquareSelected) {
            if (mSelectedColumn < 8) {
                mSelectedRow = 7 - mSelectedRow;
                mSelectedColumn = 7 - mSelectedColumn;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point touchPoint = new Point((int)event.getX(), (int)event.getY());
        if (!mSquareSelected) {
            if (mMode == 0) { // Analysis Mode
                boolean valid = mGame.validateGame(mBoard);
                if (!mGame.getVictory() & valid) {
                    for (int i = 0; i < 8 ; i++) {
                        for (int j = 0; j < 8; j++) {
                            if (mBoard.hasPiece(i, j)) {
                                if (mBoard.getSquare(i, j).contains(touchPoint)) {
                                    if (mBoard.getSquare(i, j).getColor() == mGame.getTurn()) {
                                        mBoard.getSquare(i, j).setSelected(true);
                                        mSquareSelected = true;
                                        if (!mBoardFlipped) {
                                            mSelectedRow = j;
                                            mSelectedColumn = i;
                                        } else {
                                            mSelectedRow = 7 - j;
                                            mSelectedColumn = 7 - i;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else { // Search Mate Mode
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (mBoard.hasPiece(i, j)) {
                            if (mBoard.getSquare(i, j).contains(touchPoint)) {
                                mBoard.getSquare(i, j).setSelected(true);
                                mSquareSelected = true;
                                if ( i < 8) {
                                    if (!mBoardFlipped) {
                                        mSelectedRow = j;
                                        mSelectedColumn = i;
                                    } else {
                                        mSelectedRow = 7 - j;
                                        mSelectedColumn = 7 - i;
                                    }
                                } else {
                                    mSelectedRow = j;
                                    mSelectedColumn = i;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            TextView tvLogView = (TextView) ((MainActivity)getContext()).findViewById(R.id.tvLogView);
            TextView tvLogLabel = (TextView) ((MainActivity)getContext()).findViewById(R.id.tvLogLabel);
            mSquareSelected = false;
            mSelectedRow = -1;
            mSelectedColumn = -1;
            Board currentBoard = new Board(mBoard);
            boolean pieceSelected = false;
            Piece selectedPiece = null;
            Piece eatenPiece = null;
            int oldX = 0;
            int oldY = 0;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (mBoard.hasPiece(i, j)){
                        if (mBoard.getSquare(i, j).isSelected()){
                            pieceSelected = true;
                            selectedPiece = mBoard.getSquare(i, j);
                            oldX = i;
                            oldY = j;
                        }
                    }
                }
            }
            if (mMode != 0) { // Search Mate Mode
                for (int i = 8; i < 10; i++) {
                    for (int j = 0; j < 6; j++) {
                        if (mBoard.hasPiece(i, j)) {
                            if (mBoard.getSquare(i, j).isSelected()) {
                                pieceSelected = true;
                                selectedPiece = mBoard.getSquare(i, j);
                                oldX = i;
                                oldY = j;
                            }
                        }
                    }
                }
            }
            if (pieceSelected) {
                if (mMode == 0) {  // Analysis Mode: do legal moves
                    int newX = touchPoint.x / mSquareSize;
                    int newY = touchPoint.y / mSquareSize;
                    Point flippedTouchPoint = touchPoint;
                    if ((newX < 8) && mBoardFlipped) {
                        newX = 7 - newX;
                        newY = 7 - newY;
                        flippedTouchPoint.x = newX * mSquareSize;
                        flippedTouchPoint.y = newY * mSquareSize;
                    }
                    // Check if move would yield to check for current side
                    boolean valid = true;
                    if (newX == oldX & newY == oldY)
                        valid = false;
                    else
                        valid = !mGame.checkCheck(currentBoard, oldX, oldY, newX, newY, mBoard.hasPiece(newX, newY));
                    if (selectedPiece.checkLegalMove(flippedTouchPoint, mBoard) && valid) {
                        // Check for castling
                        if (selectedPiece.getType().equals("King") && (Math.abs(newX - oldX) == 2)) {
                            if (selectedPiece.getColor() == false) { //piece is white
                                mBoard.clearSquare(oldX, oldY);
                                mBoard.setSquare(newX, newY, selectedPiece);
                                selectedPiece.setLocation(newX, newY);
                                if (mBoardFlipped)
                                    selectedPiece.setLocationXY(7 - newX, 7 - newY);
                                if (oldX < newX) { //short castling
                                    Piece rook = mBoard.getSquare(7, 7);
                                    mBoard.setSquare(5, 7, rook);
                                    rook.setLocation(5, 7);
                                    if (mBoardFlipped)
                                        rook.setLocationXY(2, 0);
                                    mBoard.clearSquare(7, 7);
                                    mText = getText() + mMove + ". 0-0";
                                } else { //long castling
                                    Piece rook = mBoard.getSquare(0, 7);
                                    mBoard.setSquare(3, 7, rook);
                                    rook.setLocation(3, 7);
                                    if (mBoardFlipped)
                                        rook.setLocationXY(4, 7);
                                    mBoard.clearSquare(0, 7);
                                    mText = getText() + mMove + ". 0-0-0";
                                }
                            } else { //piece is black
                                mBoard.clearSquare(oldX, oldY);
                                mBoard.setSquare(newX, newY, selectedPiece);
                                selectedPiece.setLocation(newX, newY);
                                if (oldX < newX) { //short castling
                                    Piece rook = mBoard.getSquare(7, 0);
                                    mBoard.setSquare(5, 0, rook);
                                    rook.setLocation(5, 0);
                                    if (mBoardFlipped)
                                        rook.setLocationXY(2, 7);
                                    mBoard.clearSquare(7, 0);
                                    mText = getText() + " 0-0";
                                } else { //long castling
                                    Piece rook = mBoard.getSquare(0, 0);
                                    mBoard.setSquare(3, 0, rook);
                                    rook.setLocation(3, 0);
                                    if (mBoardFlipped)
                                        rook.setLocationXY(4, 7);
                                    mBoard.clearSquare(0, 0);
                                    mText = getText() + " 0-0-0";
                                }
                                mMove += 1;
                            }
                        } else {
                            if (selectedPiece.getColor() == true) { //piece is black
                                mText = getText() + " ";
                            } else { //piece is white
                                mText = getText() + mMove + ". ";
                            }
                            if (mBoard.hasPiece(newX, newY)) {
                                eatenPiece = mBoard.getSquare(newX, newY);
                            }
                            mBoard.clearSquare(oldX, oldY);
                            mBoard.setSquare(newX, newY, selectedPiece);
                            selectedPiece.setLocation(newX, newY);
                            if (mBoardFlipped)
                                selectedPiece.setLocationXY(7 - newX, 7 - newY);
                            String figurine = "";
                            if (!selectedPiece.getFENType().toUpperCase().equals("P"))
                                figurine = selectedPiece.getFENType().toUpperCase();
                            mText = getText() + figurine + this.columns[oldX] +  (7 - oldY+1);
                            if ((eatenPiece != null))
                                mText = getText() + "x" + this.columns[newX] +  (7 - newY+1);
                            else
                                mText = getText() + "-" + this.columns[newX] +  (7 - newY+1);
                        }
                        tvLogView.setText(getText());
                        mGame.checkVictory(currentBoard, oldX, oldY, newX, newY, eatenPiece != null);
                        if (mGame.getVictory()) {
                            if (mGame.getTurn() == false) {
                                mText = getText() + "#\n\n" + getContext().getString(R.string.white_mates);
                            } else {
                                mText = getText() + "#\n\n" + getContext().getString(R.string.black_mates);
                            }
                            tvLogView.setText(getText());
                            invalidate();
                            showMessage(getContext().getString(R.string.mate) + "!", "MateSolver");
                            mMateInAnalyseMode = true;
                        } else {
                            ///TODO: Improve check tracing
                            if (mGame.isCheck(currentBoard, oldX, oldY, newX, newY, eatenPiece != null)) {
                                mText = getText() + "+";
                                tvLogView.setText(getText());
                                invalidate();
                                showMessage(getContext().getString(R.string.check) + "!", "MateSolver");
                            }
                            String turn = mGame.changeTurn() + " " + getContext().getString(R.string.to_move);
                            tvLogLabel.setText("(" + getContext().getString(R.string.mode_analysis) + ") - " + turn);

                        }
                        if (selectedPiece.getColor() == true) { //piece is black
                            mText = getText() + "\n";
                            mMove += 1;
                        }
                    } else {
                        //snap back to original square
                        selectedPiece.setLocation(oldX, oldY);
                        if (mBoardFlipped)
                            selectedPiece.setLocationXY(7 - oldX, 7 - oldY);
                        if (oldX != newX | oldY != newY) {
                            invalidate();
                        }
                    }
                } else { // Search Mate: setup board
                    int newX = touchPoint.x / mSquareSize;
                    int newY = touchPoint.y / mSquareSize;
                    if ((newX < 8) && mBoardFlipped) {
                        newX = 7 - newX;
                        newY = 7 - newY;
                    }
                    mBoard.clearSquare(oldX, oldY);
                    if (oldX > 7) //new piece taken from new piece area, replace it
                        switch (oldY) {
                            case 0:
                                mBoard.setSquare(oldX, oldY, new King(this.getContext(), oldX, oldY, mSquareSize, oldX == 9)); break;
                            case 1:
                                mBoard.setSquare(oldX, oldY, new Queen(this.getContext(), oldX, oldY, mSquareSize, oldX == 9)); break;
                            case 2:
                                mBoard.setSquare(oldX, oldY, new Rook(this.getContext(), oldX, oldY, mSquareSize, oldX == 9)); break;
                            case 3:
                                mBoard.setSquare(oldX, oldY, new Bishop(this.getContext(), oldX, oldY, mSquareSize, oldX == 9)); break;
                            case 4:
                                mBoard.setSquare(oldX, oldY, new Knight(this.getContext(), oldX, oldY, mSquareSize, oldX == 9)); break;
                            case 5:
                                mBoard.setSquare(oldX, oldY, new Pawn(this.getContext(), oldX, oldY, mSquareSize, oldX == 9)); break;
                            default:
                                break;
                        }
                    if (newX < 8 & newY < 8) { //destination on the board, move selected piece
                        mBoard.setSquare(newX, newY, selectedPiece);
                        selectedPiece.setLocation(newX, newY);
                        if (mBoardFlipped)
                            selectedPiece.setLocationXY(7 - newX, 7 - newY);
                    }
                }
                selectedPiece.setSelected(false);
            }
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w < h)
            mSquareSize = w / 10;
        else
            mSquareSize = h / 8;
        Board board = new Board(mBoard);
        mBoard = new Board(this.getContext(), mSquareSize, board);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        int desiredWidth;
        int desiredHeight;

        //Measure Width
        if (widthSize < heightSize)
            desiredWidth = widthSize;
        else
            desiredWidth = heightSize / 8 * 10;
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        desiredHeight = width / 10 * 8;
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean isBlack = false;
        // Draw board and new piece area
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(false);
        for (int i = 0; i < 10; i++) {
            isBlack = !isBlack;
            for (int j = 0; j < 8; j++) {
                isBlack = !isBlack;
                mRect.set(i*mSquareSize, j*mSquareSize, i*mSquareSize + mSquareSize - 1, j*mSquareSize + mSquareSize - 1);
                if (i < 8 && j < 8) {
                    if (isBlack) {
                        mPaint.setColor(Color.rgb(80, 96, 127));
                    } else {
                        mPaint.setColor(Color.rgb(239, 239, 255));
                    }
                    canvas.drawRect(mRect, mPaint);
                }
                if (i < 8 || j < 6) {
                    mPaint.setColor(Color.BLACK);
                    canvas.drawLine(i*mSquareSize + mSquareSize - 1, j*mSquareSize, i*mSquareSize + mSquareSize - 1, j*mSquareSize + mSquareSize, mPaint);
                    canvas.drawLine(i*mSquareSize, j*mSquareSize + mSquareSize - 1, i*mSquareSize + mSquareSize - 1, j*mSquareSize + mSquareSize - 1, mPaint);
                }
            }
        }
        canvas.drawLine(0, 0, 10 * mSquareSize - 1, 0, mPaint);
        canvas.drawLine(0, 0, 0, 8 * mSquareSize - 1, mPaint);
        // Draw column row labels
        String columns = "ABCDEFGH";
        String rows = "12345678";
        if (mBoardFlipped) {
            columns = "HGFEDCBA";
            rows = "87654321";
        }
        Rect rect = new Rect();
        mPaint.setColor(Color.rgb(40, 40, 63));
        mPaint.setTextSize(0.25f * (float)mSquareSize);
        mPaint.getTextBounds("W", 0, 1, rect);
        for(int i = 0; i < 8; i++) {
            canvas.drawText(columns.substring(i, i + 1), (float)(i + 1) * (float)mSquareSize - 0.05f * (float)mSquareSize - (float)rect.width(), 8.0f * (float)mSquareSize - 0.05f * (float)mSquareSize, mPaint);
            canvas.drawText(rows.substring(i, i + 1), 0.05f * (float)mSquareSize, (float)(7 - i) * (float)mSquareSize + 0.05f * (float)mSquareSize + (float)rect.height(), mPaint);
        }
        // Draw pieces
        mPaint.setAntiAlias(true);
        for(int i = 0; i < 10; i++) {
            for (int j = 0; j < 8; j++) {
                if(mBoard.hasPiece(i, j)) {
                    mBoard.getSquare(i, j).draw(canvas, mPaint);
                }
            }
        }
        // Draw selected square
        if (mSquareSelected) {
            Bitmap bm;
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.selected);
            Rect src = new Rect(0, 0, bm.getWidth() - 1, bm.getHeight() - 1);
            Rect dst = new Rect(mSelectedColumn * mSquareSize, mSelectedRow * mSquareSize, mSelectedColumn * mSquareSize + mSquareSize - 1, mSelectedRow * mSquareSize + mSquareSize - 1);
            canvas.drawBitmap(bm, src, dst, mPaint);
        }
    }

    public Bitmap drawBoard() {
        int squaresize = mSquareSize;
        Bitmap bitmap = Bitmap.createBitmap(8 * squaresize, 8 * squaresize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        Rect rect = new Rect();
        boolean isBlack = false;
        // Draw board and new piece area
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(false);
        for (int i = 0; i < 8; i++) {
            isBlack = !isBlack;
            for (int j = 0; j < 8; j++) {
                isBlack = !isBlack;
                rect.set(i*squaresize, j*squaresize, i*squaresize + squaresize - 1, j*squaresize + squaresize - 1);
                if (isBlack) {
                    paint.setColor(Color.rgb(80, 96, 127));
                } else {
                    paint.setColor(Color.rgb(239, 239, 255));
                }
                canvas.drawRect(rect, paint);
                paint.setColor(Color.BLACK);
                canvas.drawLine(i*squaresize + squaresize - 1, j*squaresize, i*squaresize + squaresize - 1, j*squaresize + squaresize, paint);
                canvas.drawLine(i*squaresize, j*squaresize + squaresize - 1, i*squaresize + squaresize - 1, j*squaresize + squaresize - 1, paint);
            }
        }
        canvas.drawLine(0, 0, 8 * squaresize - 1, 0, paint);
        canvas.drawLine(0, 0, 0, 8 * squaresize - 1, paint);
        // Draw pieces
        paint.setAntiAlias(true);
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(mBoard.hasPiece(i, j)) {
                    mBoard.getSquare(i, j).draw(canvas, paint);
                }
            }
        }
        return bitmap;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        this.mMode = mode;
    }

    public Game getGame() {
        return mGame;
    }

    public void setGame(Game game) {
        this.mGame = game;
    }

    public Board getBoard() {
        return mBoard;
    }

    public void setBoard(Board board) {
        this.mBoard = board;
    }

    public void setMove(int move) {
        this.mMove = move;
    }

    public int getMove() {
        return this.mMove;
    }

    public void setSquareSelected(boolean squareSelected) {
        if (mSquareSelected != squareSelected) {
            mSquareSelected = squareSelected;
            invalidate();
        }
    }

    public void setText(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public void showMessage(String message, String title) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
        dlgAlert.setCancelable(true);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        AlertDialog dlg = dlgAlert.create();
        dlg.show();
    }

    public void clearText() {
        mText = "";
    }

    public void saveState(DataOutputStream dos) {
        try {
            dos.writeInt(mMode);
            dos.writeInt(mSquareSize);
            mBoard.saveState(dos);
            mGame.saveState(dos);
            dos.writeInt(mMove);
            dos.writeBoolean(mSquareSelected);
            dos.writeInt(mSelectedRow);
            dos.writeInt(mSelectedColumn);
            dos.writeUTF(mText);
            dos.writeBoolean(isMateInAnalyseMode());
            dos.writeBoolean(mBoardFlipped);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restoreState(DataInputStream dis) {
        try {
            mMode = dis.readInt();
            mSquareSize = dis.readInt();
            mBoard.restoreState(dis);
            mGame.restoreState(dis);
            mMove = dis.readInt();
            mSquareSelected = dis.readBoolean();
            mSelectedRow = dis.readInt();
            mSelectedColumn = dis.readInt();
            mText = dis.readUTF();
            mMateInAnalyseMode = dis.readBoolean();
            mBoardFlipped = dis.readBoolean();
            if (mBoardFlipped) {
                for(int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if(mBoard.hasPiece(i, j))
                            mBoard.getSquare(i, j).setLocationXY(7 - i, 7 - j);
                    }
                }
                if (mSquareSelected) {
                    if (mSelectedColumn < 8)
                        mBoard.getSquare(7 - mSelectedColumn, 7 - mSelectedRow).setSelected(true);
                    else
                        mBoard.getSquare(mSelectedColumn, mSelectedRow).setSelected(true);
                }
            } else
            if (mSquareSelected) {
                mBoard.getSquare(mSelectedColumn, mSelectedRow).setSelected(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveState(Bundle savedInstanceState) {
        savedInstanceState.putInt("MODE", mMode);
        savedInstanceState.putInt("SQUARESIZE", mSquareSize);
        mBoard.saveState(savedInstanceState);
        mGame.saveState(savedInstanceState);
        savedInstanceState.putInt("MOVE", mMove);
        savedInstanceState.putBoolean("SQUARESELECTED", mSquareSelected);
        savedInstanceState.putInt("SELECTEDROW", mSelectedRow);
        savedInstanceState.putInt("SELECTEDCOLUMN", mSelectedColumn);
        savedInstanceState.putString("TEXT", mText);
        savedInstanceState.putBoolean("MATEINANALYSEMODE", isMateInAnalyseMode());
        savedInstanceState.putBoolean("BOARDFLIPPED", mBoardFlipped);
    }

    public void restoreState(Bundle savedInstanceState) {
        mMode = savedInstanceState.getInt("MODE");
        mSquareSize = savedInstanceState.getInt("SQUARESIZE");
        mBoard.restoreState(savedInstanceState);
        mGame.restoreState(savedInstanceState);
        mMove = savedInstanceState.getInt("MOVE");
        mSquareSelected = savedInstanceState.getBoolean("SQUARESELECTED");
        mSelectedRow = savedInstanceState.getInt("SELECTEDROW");
        mSelectedColumn = savedInstanceState.getInt("SELECTEDCOLUMN");
        mText = savedInstanceState.getString("TEXT");
        mMateInAnalyseMode = savedInstanceState.getBoolean("MATEINANALYSEMODE");
        mBoardFlipped = savedInstanceState.getBoolean("BOARDFLIPPED");
        if (mBoardFlipped) {
            for(int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if(mBoard.hasPiece(i, j))
                        mBoard.getSquare(i, j).setLocationXY(7 - i, 7 - j);
                }
            }
            if (mSquareSelected) {
                if (mSelectedColumn < 8)
                    mBoard.getSquare(7 - mSelectedColumn, 7 - mSelectedRow).setSelected(true);
                else
                    mBoard.getSquare(mSelectedColumn, mSelectedRow).setSelected(true);
            }
        } else
        if (mSquareSelected) {
            mBoard.getSquare(mSelectedColumn, mSelectedRow).setSelected(true);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        BoardViewSavedState ss = new BoardViewSavedState(superState);
        ss.mMode = mMode;
        ss.mSquareSize = mSquareSize;
        ss.mBoard = mBoard;
        ss.mGame = mGame;
        ss.mMove = mMove;
        ss.mSquareSelected = mSquareSelected;
        ss.mSelectedRow = mSelectedRow;
        ss.mSelectedColumn = mSelectedColumn;
        ss.mText = mText;
        ss.mMateInAnalyseMode = isMateInAnalyseMode();
        ss.mBoardFlipped = mBoardFlipped;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        BoardViewSavedState ss = (BoardViewSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mMode = ss.mMode;
        mSquareSize = ss.mSquareSize;
        mBoard = ss.mBoard;
        mGame = ss.mGame;
        mMove = ss.mMove;
        mSquareSelected = ss.mSquareSelected;
        mSelectedRow = ss.mSelectedRow;
        mSelectedColumn = ss.mSelectedColumn;
        mText = ss.mText;
        mMateInAnalyseMode = ss.mMateInAnalyseMode;
        mBoardFlipped = ss.mBoardFlipped;
    }

    public boolean isMateInAnalyseMode() {
        return mMateInAnalyseMode;
    }

    public void setMateInAnalyseMode(boolean mode) {
        mMateInAnalyseMode = mode;
    }

}
