package de.streblow.kasparov;

/**
 * Engine
 *
 * @author Eric Liu
 */
public class Engine {

    public static String analysePositionResult;
    public static String bestMove;
    public static int bestScore;

    /**
     * Analyse a position command.
     *
     * @param line: analyse fen <fenStr> [moves <m1 m2 ... mn>]
     * @param d: depth in ply
     */
    public static void analysePosition(String line, int d) {

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

            Search.searchPosition(boardStructure, searchEntry);
            analysePositionResult = String.format("%s", Search.searchOutput);
            bestMove = String.format("%s", boardStructure.getMoveAsString(Search.bestMove));
            bestScore = Search.bestScore;
        }
    }
}
