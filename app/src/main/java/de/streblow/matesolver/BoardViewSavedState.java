package de.streblow.matesolver;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class BoardViewSavedState extends View.BaseSavedState {
    public int mMode;
    public int mSquareSize;
    public Board mBoard;
    public Game mGame;
    public int mMove;
    public boolean mSquareSelected;
    public int mSelectedRow;
    public int mSelectedColumn;
    public String mText;
    public boolean mMateInAnalyseMode;
    public boolean mBoardFlipped;

    BoardViewSavedState(Parcelable superState) {
        super(superState);
    }

    private BoardViewSavedState(Parcel in) {
        super(in);
        mMode = in.readInt();
        mSquareSize = in.readInt();
        Bundle bundle = null;
        bundle = in.readBundle();
        mBoard.restoreState(bundle);
        bundle = in.readBundle();
        mGame.restoreState(bundle);
        mMove = in.readInt();
        mSquareSelected = (in.readInt() == 0) ? false : true;
        mSelectedRow = in.readInt();
        mSelectedColumn = in.readInt();
        mText = in.readString();
        mMateInAnalyseMode = (in.readInt() == 0) ? false : true;
        mBoardFlipped = (in.readInt() == 0) ? false : true;
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
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(mMode);
        out.writeInt(mSquareSize);
        Bundle bundle = new Bundle();
        mBoard.saveState(bundle);
        out.writeBundle(bundle);
        bundle = new Bundle();
        mGame.saveState(bundle);
        out.writeBundle(bundle);
        out.writeInt(mMove);
        out.writeInt((mSquareSelected) ? -1 : 0);
        out.writeInt(mSelectedRow);
        out.writeInt(mSelectedColumn);
        out.writeString(mText);
        out.writeInt((mMateInAnalyseMode) ? -1 : 0);
        out.writeInt((mBoardFlipped) ? -1 : 0);
    }

    public static final Parcelable.Creator<BoardViewSavedState> CREATOR
            = new Parcelable.Creator<BoardViewSavedState>() {
        public BoardViewSavedState createFromParcel(Parcel in) {
            return new BoardViewSavedState(in);
        }

        public BoardViewSavedState[] newArray(int size) {
            return new BoardViewSavedState[size];
        }
    };
}
