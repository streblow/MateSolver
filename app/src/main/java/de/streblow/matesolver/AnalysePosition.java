package de.streblow.matesolver;

import chesspresso.Chess;
import kasparov.*;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class AnalysePosition {

    private static String m_Command;
    private static int m_Plys;
    private static int m_Color;

    public AnalysePosition(String fen, int plys, int color) {
        m_Command = "analyse fen " + fen;
        m_Plys = plys;
        m_Color = color;
    }

    public String doAnalysePosition() {
        Engine.analysePosition(m_Command, m_Plys);
        float score = 0.0f;
        if (m_Color == Chess.WHITE)
            score = (float)Engine.bestScore / 100.0f;
        else
            score = -(float)Engine.bestScore / 100.0f;
        return Engine.analysePostionResult + String.format("Score: %.2f\nBest move: %s", score, Engine.bestMove);
    }

}