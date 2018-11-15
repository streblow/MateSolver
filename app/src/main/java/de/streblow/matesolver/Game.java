package de.streblow.matesolver;

import android.os.Bundle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;

public class Game {

    private boolean turn;
    public boolean victory;
    private String playerOneName;
    private String playerTwoName;

    public Game() {
        turn = false;
        victory = false;
    }

    public Game(String playerOneName, String playerTwoName) {
        turn = false;
        victory = false;
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
    }

    public boolean getVictory() {
        return victory;
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }

    public boolean getTurn() {
        return turn;
    }

    public String changeTurn() {
        turn = !turn;
        if(!turn){
            return playerOneName;
        }else{
            return playerTwoName;
        }
    }

    public void checkVictory(Board oldBoard, int oldX, int oldY, int newX, int newY, boolean capturing) {
        String strFEN = oldBoard.getFEN();
        if (turn == true)
            strFEN += " b - - 0 1";
        else
            strFEN += " w - - 0 1";
        Position position = new Position(strFEN);
        short move = Move.getRegularMove((7 - oldY) * 8 + oldX, (7 - newY) * 8 + newX, capturing); //A1:0 H8:63
        try {
            position.doMove(move);
        } catch (IllegalMoveException ex) {
            victory = false;
            return;
        }
        victory = position.isMate();
    }

    public boolean isCheck(Board oldBoard, int oldX, int oldY, int newX, int newY, boolean capturing) {
        String strFEN = oldBoard.getFEN();
        if (turn == true)
            strFEN += " b - - 0 1";
        else
            strFEN += " w - - 0 1";
        Position position = new Position(strFEN);
        short move = Move.getRegularMove((7 - oldY) * 8 + oldX, (7 - newY) * 8 + newX, capturing); //A1:0 H8:63
        try {
            position.doMove(move);
        } catch (IllegalMoveException ex) {
            return false;
        }
        return position.isCheck();
    }

    public boolean validateGame(Board board) {
        String strFEN = board.getFEN();
        if (turn == true)
            strFEN += " b - - 0 1";
        else
            strFEN += " w - - 0 1";
        try {
            Position position = new Position(strFEN);
            if (position.isMate())
                return false;
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean checkCheck(Board oldBoard, int oldX, int oldY, int newX, int newY, boolean capturing) {
        String strFEN = oldBoard.getFEN();
        if (turn == true)
            strFEN += " b - - 0 1";
        else
            strFEN += " w - - 0 1";
        Position position = new Position(strFEN);
        short move = Move.getRegularMove((7 - oldY) * 8 + oldX, (7 - newY) * 8 + newX, capturing); //A1:0 H8:63
        try {
            position.doMove(move);
        } catch (IllegalMoveException ex) {
            return true;
        }
        position.toggleToPlay();
        return position.isCheck();
    }

    public void saveState(DataOutputStream dos) {
        try {
            dos.writeBoolean(turn);
            dos.writeBoolean(victory);
            dos.writeUTF(playerOneName);
            dos.writeUTF(playerTwoName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("TURN", turn);
        savedInstanceState.putBoolean("VICTORY", victory);
        savedInstanceState.putString("PLAYERONE", playerOneName);
        savedInstanceState.putString("PLAYERTWO", playerTwoName);
    }

    public void restoreState(DataInputStream dis) {
        try {
            turn = dis.readBoolean();
            victory = dis.readBoolean();
            playerOneName = dis.readUTF();
            playerTwoName = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restoreState(Bundle savedInstanceState) {
        turn = savedInstanceState.getBoolean("TURN");
        victory = savedInstanceState.getBoolean("VICTORY");
        playerOneName = savedInstanceState.getString("PLAYERONE");
        playerTwoName = savedInstanceState.getString("PLAYERTWO");
    }

}