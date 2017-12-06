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
