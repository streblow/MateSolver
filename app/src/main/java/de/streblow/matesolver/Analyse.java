package de.streblow.matesolver;

import chesspresso.position.Position;
import chesspresso.move.IllegalMoveException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Analyse {

    private static final int MAX_PLY = 15; // Limit to maximal mate in 8

    private static Position m_Position;
    private static int m_Plys;
    private static int m_Color;
    private static String[] m_Moves;
    private static String[] m_LastMoves;
    private static String m_Solution;
    private static int m_CalculatedPositions;
    private static boolean m_FoundSolution;
    private static boolean m_showFirstOnly;

    public Analyse(String fen, int plys, int color, boolean firstmoveonly) {
        m_Position = new Position(fen);
        m_Plys = plys;
        m_Color = color;
        m_Moves = null;
        m_LastMoves = null;
        m_Solution = "";
        m_CalculatedPositions = 0;
        m_FoundSolution = false;
        m_showFirstOnly = firstmoveonly;
    }

    public String evaluatePosition() {
        m_Moves = new String[MAX_PLY];
        m_LastMoves = new String[MAX_PLY];
        for (int i = 0; i < m_Plys; i++)
            m_LastMoves[i] = "XX-XX";
        m_Solution = "";
        m_CalculatedPositions = 0;
        try {
            m_FoundSolution = searchMate();
        } catch (IllegalMoveException ex) {
            Logger.getLogger(Analyse.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (m_FoundSolution) {
            m_Solution = formatSolution(m_Solution);
            if (m_showFirstOnly) {
                String solution = "";
                String lines[] = m_Solution.split("\\r?\\n");
                for (int i = 0; i < lines.length; i++) {
                    if (lines[i].substring(0, 1).equals("1")) {
                        if (lines[i].indexOf(" ", 3) == -1)
                            solution += lines[i] + "\n";
                        else if (!lines[i].substring(3, 4).equals(" ") &
                                !lines[i].substring(3, 4).equals(".")) {
                            solution += lines[i].substring(0, lines[i].indexOf(" ", 3)) + "\n";
                        }
                    }
                }
                m_Solution = solution;
            }
            return "Solution found\nCalculated positions: " + m_CalculatedPositions + "\n" + m_Solution;
        }
        else
            return "No solution found\nCalculated positions: " + m_CalculatedPositions + "\n";
    }

    private static boolean searchMate() throws IllegalMoveException {
        int currentsolutionlength = m_Solution.length();
        int ply = m_Position.getPlyNumber();

        // Record move.
        if (m_Position.getLastMove() != null)
            m_Moves[ply - 1] = m_Position.getLastMove().getLAN();

        // Check for mate if we are at last ply or handle earlier mate
        if (ply >= m_Plys | m_Position.isMate()) {
            m_CalculatedPositions += 1;
            // Check if we've found a position where current position is mate
            if (m_Position.isMate()) {
                // We found a mate, now print the sequence of moves
                for (int i = 0; i < ply; i++) {
                    if (i % 2  == 0)
                        m_Solution = m_Solution + (i / 2 + 1) + ". " + m_Moves[i] + " ";
                    else
                        m_Solution = m_Solution + m_Moves[i] + " ";
                }
                m_Solution = m_Solution.substring(0, m_Solution.length() - 1);
                for (int i = 0; i < m_Plys; i++)
                    m_LastMoves[i] = m_Moves[i];
                m_Solution = m_Solution + "\n";
                return true;
            } else
                return false;
        }
        // Recurse through all possible next positions
        boolean matefound = false;
        int colortoplay = m_Position.getToPlay();
        short[] nextMoves = m_Position.getAllMoves();
        for(short thisMove : nextMoves){
            // Make the move, recurse searchMate, and undo the move
            m_Position.doMove(thisMove);
            boolean mate = searchMate();
            m_Position.undoMove();
            if (colortoplay == m_Color) { // At least one move must result in mate
                if (mate)
                    matefound = true;
            } else { // All moves must result in mate
                if (mate)
                    matefound = true;
                else {
                    matefound = false;
                    break;
                }
            }
        }
        if (!matefound)
            if (currentsolutionlength > 0)
                m_Solution = m_Solution.substring(0, currentsolutionlength);
            else
                m_Solution = "";
        return matefound;
    }

    private static String formatSolution(String solution) {
        String result = "";
        String lines[] = solution.split("\\r?\\n");
        String lastline = "1. XX-XX";
        for (int i = 0; i < lines.length; i++) {
            String lastmoves[] = lastline.split(" ");
            String moves[] = lines[i].split(" ");
            if (lastmoves.length <= moves.length) {
                for (int j = 0; j < lastmoves.length; j++) {
                    if (j % 3 == 1) { // White move (incl. move number)
                        if (lastmoves[j].equals(moves[j])) {
                            result += fillSpaces(moves[j - 1] + " " + moves[j]) + " ";
                        } else { // Move list differs from last move list
                            result += moves[j - 1] + " " + moves[j] + " ";
                            for (int k = j + 1; k < moves.length; k++)
                                result += moves[k] + " ";
                            break;
                        }
                    } else if (j % 3 == 2) { // Black move (w/o move number)
                        if (lastmoves[j].equals(moves[j])) {
                            result += fillSpaces(moves[j]) + " ";
                        } else { // Move list differs from last move list
                            result = result.substring(0,
                                    result.length() - moves[j - 2].length() - moves[j - 1].length() - 2);
                            result += moves[j - 2] + " " + fillSpaces(moves[j - 1]) + " ";
                            result = result.substring(0, result.length() - 5);
                            result += " ... ";
                            result += moves[j] + " ";
                            for (int k = j + 1; k < moves.length; k++)
                                result += moves[k] + " ";
                            break;
                        }
                    }
                }
            } else {
                for (int j = 0; j < moves.length; j++) {
                    if (j % 3 == 1) { // White move (incl. move number)
                        if (lastmoves[j].equals(moves[j])) {
                            result += fillSpaces(moves[j - 1] + " " + moves[j]) + " ";
                        } else { // Move list differs from last move list
                            result += moves[j - 1] + " " + moves[j] + " ";
                            for (int k = j + 1; k < moves.length; k++)
                                result += moves[k] + " ";
                            break;
                        }
                    } else if (j % 3 == 2) { // Black move (w/o move number)
                        if (lastmoves[j].equals(moves[j])) {
                            result += fillSpaces(moves[j]) + " ";
                        } else { // Move list differs from last move list
                            result = result.substring(0,
                                    result.length() - moves[j - 2].length() - moves[j - 1].length() - 2);
                            result += moves[j - 2] + " " + fillSpaces(moves[j - 1]) + " ";
                            result = result.substring(0, result.length() - 5);
                            result += " ... ";
                            result += moves[j] + " ";
                            for (int k = j + 1; k < moves.length; k++)
                                result += moves[k] + " ";
                            break;
                        }
                    }
                }
            }
            result = result.substring(0, result.length() - 1);
            result += "\n";
            lastline = lines[i];
        }
        return result;
    }

    private static String fillSpaces(String string) {
        char[] chars = new char[string.length()];
        Arrays.fill(chars, ' ');
        return new String(chars);
    }

}