package de.streblow.matesolver;

import android.os.AsyncTask;
import android.widget.TextView;

import chesspresso.Chess;
import de.streblow.kasparov.*;

/**
 * Created by streblow on 04.12.2017.
 */

public class AnalysePositionTask extends AsyncTask<Object, String, String> {
    public BoardView boardView;
    public TextView textView;

    private static String m_Command;
    private static int m_Plys;
    private static int m_Color;

    private static String analysePositionResult;
    private static String bestMoveStr;
    private static int bestMove;
    private static int bestScore;
    private static String m_Status_Text;

    public AnalysePositionTask(BoardView boardView, TextView textView,
                               String fen, int plys, int color) {
        super();
        this.boardView = boardView;
        this.textView = textView;

        m_Command = "analyse fen " + fen;
        m_Plys = plys;
        m_Color = color;

        analysePositionResult = "";
        bestMoveStr = "";
        bestMove = BoardUtils.NO_MOVE;
        bestScore = 0;
        m_Status_Text = textView.getText().toString();
    }

    @Override
    protected String doInBackground(Object... objects) {
        analysePosition(m_Command, m_Plys);
        float score = 0.0f;
        if (m_Color == Chess.WHITE)
            score = (float)bestScore / 100.0f;
        else
            score = -(float)bestScore / 100.0f;
        return String.format("Score: %.2f\nDepth: %d\nBest move: %s\nBest line: %s", score, m_Plys, bestMoveStr, analysePositionResult);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (boardView.getGame().getTurn()) { // black
            boardView.setText(result + "\n1. ... ");
            textView.setText(result + "\n1. ... ");
        } else {
            boardView.setText(result + "\n");
            textView.setText(result + "\n");
        }
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        String result = progress[0];
        boardView.setText(m_Status_Text + "\n" + result);
        textView.setText(m_Status_Text + "\n" + result);
    }

    public void analysePosition(String line, int d) {

        BoardStructure boardStructure = new BoardStructure();
        SearchEntry searchEntry = new SearchEntry();

        int ptr = 8;
        if (ptr >= line.length())
            return;
        String s = line.substring(ptr, line.length()).trim();

        // analyse fen <fenStr> [moves <m1 m2 ... mn>]
        if (s.startsWith("fen")) {
            ptr += 4;
            if (ptr >= line.length())
                return;
            s = line.substring(ptr, line.length());

            int movesIndex = line.indexOf("moves");

            // analyse fen <fenStr>
            if (movesIndex == -1) {
                boardStructure.parseFEN(s);
                boardStructure.updateListMaterials();
            } else {

                // analyse fen <fenStr> moves <m1 m2 ... mn>
                String fen = line.substring(ptr, movesIndex-1);
                ptr = movesIndex + 6;
                if (ptr >= line.length())
                    return;

                s = line.substring(ptr, line.length());

                boardStructure.parseFEN(fen);
                boardStructure.updateListMaterials();

                String[] moves = s.split(" ");
                for (int i = 0; i < moves.length; i++) {
                    int move = MoveUtils.parseMove(boardStructure, moves[i]);
                    if (move == BoardUtils.NO_MOVE)
                        break;
                    MakeMove.makeMove(boardStructure, move);
                    boardStructure.setPly(0);
                }
            }

            int depth = d;
            int movesToGo = 30;
            int moveTime = -1;
            int time = -1;
            int inc = 0;
            searchEntry.setTimeSet(false);

            if (moveTime != -1) {
                time = moveTime;
                movesToGo = 1;
            }

            searchEntry.setStartTime(Time.getTimeInMilliseconds());
            searchEntry.setDepth(depth);

            if (time != -1) {
                searchEntry.setTimeSet(true);
                time /= movesToGo;
                time -= 50;
                searchEntry.setStopTime(searchEntry.getStartTime() + time + inc);
            }

            if (depth == -1) {
                searchEntry.setDepth(BoardConstants.MAX_DEPTH);
            }

            searchPosition(boardStructure, searchEntry);
        }
    }

    public void searchPosition(BoardStructure boardStructure, SearchEntry searchEntry) {
        analysePositionResult = "";
        bestScore = -Search.INF;
        int pvMoves = 0;
        int startPly = 1;
        String startNo = "1. ";
        if (boardStructure.getSide() == BoardColor.BLACK.value) {
            startPly = 2;
            startNo = "1. ... ";
        }
        Search.clearForSearch(boardStructure, searchEntry);

        int factor = 1;
        if (boardStructure.getSide() == BoardColor.BLACK.value)
            factor = -1;

        for (int currentDepth = 1; currentDepth <= searchEntry.getDepth(); currentDepth++) {
            bestScore = Search.alphaBeta(boardStructure, searchEntry, -Search.INF, Search.INF, currentDepth, true);

            if (searchEntry.isStopped()) {
                break;
            }

            pvMoves = PVTable.getPVLine(boardStructure, currentDepth);

            bestMove = boardStructure.getPVArrayEntry(0);
            bestMoveStr = boardStructure.getMoveAsLongString(bestMove);
            analysePositionResult = String.format("score %.2f depth %d nodes %d time %d ",
                    0.01f * (float)(factor * bestScore), currentDepth, searchEntry.getNodes(),
                    Time.getTimeInMilliseconds() - searchEntry.getStartTime());
            analysePositionResult += String.format("pv ");
            int plyCounter = startPly;
            analysePositionResult += startNo;
            for (int pvNum = 0; pvNum < pvMoves; pvNum++) {
                if ((plyCounter > 2) && ((plyCounter % 2) == 1))
                    analysePositionResult += Integer.toString((plyCounter + 1) / 2) + ". ";
                plyCounter += 1;
                analysePositionResult += String.format(boardStructure.getMoveAsLongString(boardStructure.getPVArrayEntry(pvNum)) +
                    " ");
            }
            analysePositionResult += String.format("\n");
            publishProgress(analysePositionResult);
        }
        analysePositionResult = "";
        int plyCounter = startPly;
        analysePositionResult += startNo;
        for (int pvNum = 0; pvNum < pvMoves; pvNum++) {
            if ((plyCounter > 2) && ((plyCounter % 2) == 1))
                analysePositionResult += Integer.toString((plyCounter + 1) / 2) + ". ";
            plyCounter += 1;
            analysePositionResult += String.format(boardStructure.getMoveAsLongString(boardStructure.getPVArrayEntry(pvNum)) +
                    " ");
            MakeMove.makeMove(boardStructure, boardStructure.getPVArrayEntry(pvNum));
        }
    }
}
