package de.streblow.matesolver;

import android.content.Context;
import android.os.Bundle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Board {

    private Piece[][] board;
    private Context context;
    private int size;

    public Board(Context context, int size) {
        this.context = context;
        this.size = size;
        board = new Piece[10][8];
        resetBoard();
    }

    public Board(Board b) {
        this.context = b.getContext();
        this.size = b.getSize();
        board = new Piece[10][8];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 8; j++) {
                if (b.hasPiece(i, j)) {
                    if (b.getSquare(i, j).getColor() == false) { //case that piece is white
                        if (b.getSquare(i, j).getType().equals("King")) {
                            board[i][j] = new King(context, i, j, size, false);
                        } else if (b.getSquare(i, j).getType().equals("Queen")) {
                            board[i][j] = new Queen(context, i, j, size, false);
                        } else if (b.getSquare(i, j).getType().equals("Rook")) {
                            board[i][j] = new Rook(context, i, j, size, false);
                        } else if (b.getSquare(i, j).getType().equals("Bishop")) {
                            board[i][j] = new Bishop(context, i, j, size, false);
                        } else if (b.getSquare(i, j).getType().equals("Knight")) {
                            board[i][j] = new Knight(context, i, j, size, false);
                        } else if (b.getSquare(i, j).getType().equals("Pawn")) {
                            board[i][j] = new Pawn(context, i, j, size, false);
                        }
                    } else { //case that piece is black
                        if (b.getSquare(i, j).getType().equals("King")) {
                            board[i][j] = new King(context, i, j, size, true);
                        } else if (b.getSquare(i, j).getType().equals("Queen")) {
                            board[i][j] = new Queen(context, i, j, size, true);
                        } else if (b.getSquare(i, j).getType().equals("Rook")) {
                            board[i][j] = new Rook(context, i, j, size, true);
                        } else if (b.getSquare(i, j).getType().equals("Bishop")) {
                            board[i][j] = new Bishop(context, i, j, size, true);
                        } else if (b.getSquare(i, j).getType().equals("Knight")) {
                            board[i][j] = new Knight(context, i, j, size, true);
                        } else if (b.getSquare(i, j).getType().equals("Pawn")) {
                            board[i][j] = new Pawn(context, i, j, size, true);
                        }
                    }
                }
            }
        }
    }

    public Board(Context context, int size, Board b) {
        this.context = context;
        this.size = size;
        board = new Piece[10][8];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 8; j++) {
                if (b.hasPiece(i, j)) {
                    if (b.getSquare(i, j).getColor() == false) { //case that piece is white
                        if (b.getSquare(i, j).getType().equals("King")) {
                            board[i][j] = new King(context, i, j, size, false);
                        } else if (b.getSquare(i, j).getType().equals("Queen")) {
                            board[i][j] = new Queen(context, i, j, size, false);
                        } else if (b.getSquare(i, j).getType().equals("Rook")) {
                            board[i][j] = new Rook(context, i, j, size, false);
                        } else if (b.getSquare(i, j).getType().equals("Bishop")) {
                            board[i][j] = new Bishop(context, i, j, size, false);
                        } else if (b.getSquare(i, j).getType().equals("Knight")) {
                            board[i][j] = new Knight(context, i, j, size, false);
                        } else if (b.getSquare(i, j).getType().equals("Pawn")) {
                            board[i][j] = new Pawn(context, i, j, size, false);
                        }
                    } else { //case that piece is black
                        if (b.getSquare(i, j).getType().equals("King")) {
                            board[i][j] = new King(context, i, j, size, true);
                        } else if (b.getSquare(i, j).getType().equals("Queen")) {
                            board[i][j] = new Queen(context, i, j, size, true);
                        } else if (b.getSquare(i, j).getType().equals("Rook")) {
                            board[i][j] = new Rook(context, i, j, size, true);
                        } else if (b.getSquare(i, j).getType().equals("Bishop")) {
                            board[i][j] = new Bishop(context, i, j, size, true);
                        } else if (b.getSquare(i, j).getType().equals("Knight")) {
                            board[i][j] = new Knight(context, i, j, size, true);
                        } else if (b.getSquare(i, j).getType().equals("Pawn")) {
                            board[i][j] = new Pawn(context, i, j, size, true);
                        }
                    }
                }
            }
        }
    }

    public void clearBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
    }

    public void resetBoard() {
        //this for loop interates through board and fills it with null objects
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
        //black pieces
        setSquare(4, 0, new King(context, 4, 0, size, true));
        setSquare(0, 0, new Rook(context, 0, 0, size, true));
        setSquare(1, 0, new Knight(context, 1, 0, size, true));
        setSquare(2, 0, new Bishop(context, 2, 0, size, true));
        setSquare(3, 0, new Queen(context, 3, 0, size, true));
        setSquare(5, 0, new Bishop(context, 5, 0, size, true));
        setSquare(6, 0, new Knight(context, 6, 0, size, true));
        setSquare(7, 0, new Rook(context, 7, 0, size, true));
        for (int i = 0; i < 8; i++)
            setSquare(i, 1, new Pawn(context, i, 1, size, true));
        //white pieces
        setSquare(4, 7, new King(context, 4, 7, size, false));
        setSquare(0, 7, new Rook(context, 0, 7, size, false));
        setSquare(1, 7, new Knight(context, 1, 7, size, false));
        setSquare(2, 7, new Bishop(context, 2, 7, size, false));
        setSquare(3, 7, new Queen(context, 3, 7, size, false));
        setSquare(5, 7, new Bishop(context, 5, 7, size, false));
        setSquare(6, 7, new Knight(context, 6, 7, size, false));
        setSquare(7, 7, new Rook(context, 7, 7, size, false));
        for (int i = 0; i < 8;i++)
            setSquare(i, 6, new Pawn(context, i, 6, size, false));
        //pieces for setting up a board
        setSquare(8, 0, new King(context, 8, 0, size, false));
        setSquare(8, 1, new Queen(context, 8, 1, size, false));
        setSquare(8, 2, new Rook(context, 8, 2, size, false));
        setSquare(8, 3, new Bishop(context, 8, 3, size, false));
        setSquare(8, 4, new Knight(context, 8, 4, size, false));
        setSquare(8, 5, new Pawn(context, 8, 5, size, false));
        setSquare(9, 0, new King(context, 9, 0, size, true));
        setSquare(9, 1, new Queen(context, 9, 1, size, true));
        setSquare(9, 2, new Rook(context, 9, 2, size, true));
        setSquare(9, 3, new Bishop(context, 9, 3, size, true));
        setSquare(9, 4, new Knight(context, 9, 4, size, true));
        setSquare(9, 5, new Pawn(context, 9, 5, size, true));
    }

    public Piece getSquare(int row, int col) {
        return board[row][col];
    }

    public void setSquare(int row, int col, Piece piece) {
        board[row][col] = piece;
    }

    public void clearSquare(int row, int col) {
        board[row][col] = null;
    }

    public boolean hasPiece(int row, int col) {
        if (getSquare(row,col) != null) {
            return true;
        }else{
            return false;
        }
    }

    public String getFEN() {
        String fen = "";
        int empty = 0;
        for (int j = 0; j < 8; j++) { //up to down
            for (int i = 0; i < 8; i++) { //left to right
                if (hasPiece(i, j)) {
                    if (empty > 0) {
                        fen += String.valueOf(empty);
                        empty = 0;
                    }
                    fen += getSquare(i, j).getFENType();
                } else
                    empty++;
            }
            if (empty > 0) {
                fen += String.valueOf(empty);
                empty = 0;
            }
            if (j < 7)
                fen += "/";
        }
        return fen;
    }

    public String getUnicodeString() {
        String utf16 = "";
        String empty = "\u25E6";
        for (int j = 0; j < 8; j++) { //up to down
            for (int i = 0; i < 8; i++) { //left to right
                if (hasPiece(i, j)) {
                    utf16 += getSquare(i, j).getUTFFigurine();
                } else
                    utf16 += empty;
                if (i < 7)
                    utf16 += " ";
            }
            if (j < 7)
                utf16 += "\n";
        }
        return utf16;
    }

    public Context getContext() {
        return context;
    }

    public int getSize() {
        return size;
    }

    public void saveState(DataOutputStream dos) {
        try {
            dos.writeInt(size);
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board[i][j] != null)
                        dos.writeUTF(board[i][j].getFENType());
                    else
                        dos.writeUTF("0");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveState(Bundle savedInstanceState) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null)
                    savedInstanceState.putString(Integer.toString(i) + Integer.toString(j), board[i][j].getFENType());
                else
                    savedInstanceState.putString(Integer.toString(i) + Integer.toString(j), "0");
            }
        }
    }

    public void restoreState(DataInputStream dis) {
        try {
            size = dis.readInt();
            String fen = "";
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 8; j++) {
                    fen = dis.readUTF();
                    switch (fen) {
                        case "p":
                            board[i][j] = new Pawn(context, i, j, size, true); break;
                        case "P":
                            board[i][j] = new Pawn(context, i, j, size, false); break;
                        case "k":
                            board[i][j] = new King(context, i, j, size, true); break;
                        case "K":
                            board[i][j] = new King(context, i, j, size, false); break;
                        case "q":
                            board[i][j] = new Queen(context, i, j, size, true); break;
                        case "Q":
                            board[i][j] = new Queen(context, i, j, size, false); break;
                        case "r":
                            board[i][j] = new Rook(context, i, j, size, true); break;
                        case "R":
                            board[i][j] = new Rook(context, i, j, size, false); break;
                        case "b":
                            board[i][j] = new Bishop(context, i, j, size, true); break;
                        case "B":
                            board[i][j] = new Bishop(context, i, j, size, false); break;
                        case "n":
                            board[i][j] = new Knight(context, i, j, size, true); break;
                        case "N":
                            board[i][j] = new Knight(context, i, j, size, false); break;
                        default:
                            board[i][j] = null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restoreState(Bundle savedInstanceState) {
        String fen = "";
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 8; j++) {
                fen = savedInstanceState.getString(Integer.toString(i) + Integer.toString(j));
                switch (fen) {
                    case "p":
                        board[i][j] = new Pawn(context, i, j, size, true); break;
                    case "P":
                        board[i][j] = new Pawn(context, i, j, size, false); break;
                    case "k":
                        board[i][j] = new King(context, i, j, size, true); break;
                    case "K":
                        board[i][j] = new King(context, i, j, size, false); break;
                    case "q":
                        board[i][j] = new Queen(context, i, j, size, true); break;
                    case "Q":
                        board[i][j] = new Queen(context, i, j, size, false); break;
                    case "r":
                        board[i][j] = new Rook(context, i, j, size, true); break;
                    case "R":
                        board[i][j] = new Rook(context, i, j, size, false); break;
                    case "b":
                        board[i][j] = new Bishop(context, i, j, size, true); break;
                    case "B":
                        board[i][j] = new Bishop(context, i, j, size, false); break;
                    case "n":
                        board[i][j] = new Knight(context, i, j, size, true); break;
                    case "N":
                        board[i][j] = new Knight(context, i, j, size, false); break;
                    default:
                        board[i][j] = null;
                }
            }
        }
    }

}