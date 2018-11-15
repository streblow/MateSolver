package de.streblow.kasparov;

/**
 * For searching for the position.
 *
 * @author Eric Liu
 */
public class Search {

    public static final int INF = 30000;
    public static final int MATE = 29000;

    /**
     * Checks if a position has been repeated.
     *
     * @param boardStructure
     * @return true if the position has been repeated, false otherwise
     */
    public static boolean isRepetition(BoardStructure boardStructure) {
        for (int i = boardStructure.getHistoryPly() - boardStructure.getFiftyMove();
             i < boardStructure.getHistoryPly() - 1; i++) {
            if (boardStructure.getPositionKey() == boardStructure.getHistoryEntry(i).getPosKey())
                return true;
        }
        return false;
    }

    // result variables for searchPosition
    public static String searchOutput;
    public static int bestMove;
    public static int bestScore;
    /**
     * Starts search.
     *
     * @param boardStructure
     * @param searchEntry
     */
    public static void searchPosition(BoardStructure boardStructure, SearchEntry searchEntry) {
        searchOutput = "";
        bestMove = BoardUtils.NO_MOVE;
        bestScore = -INF;
        int pvMoves;
        clearForSearch(boardStructure, searchEntry);

        int factor = 1;
        if (boardStructure.getSide() == BoardColor.BLACK.value)
            factor = -1;

        for (int currentDepth = 1; currentDepth <= searchEntry.getDepth(); currentDepth++) {
            bestScore = alphaBeta(boardStructure, searchEntry, -INF, INF, currentDepth, true);

            if (searchEntry.isStopped()) {
                break;
            }

            pvMoves = PVTable.getPVLine(boardStructure, currentDepth);
            bestMove = boardStructure.getPVArrayEntry(0);

            searchOutput += String.format("Info: score %.2f depth %d nodes %d time %d ",
                    0.01f * (float)(factor * bestScore), currentDepth, searchEntry.getNodes(),
                    Time.getTimeInMilliseconds() - searchEntry.getStartTime());

            pvMoves = PVTable.getPVLine(boardStructure, currentDepth);
            searchOutput += String.format("pv ");
            for (int pvNum = 0; pvNum < pvMoves; pvNum++) {
                searchOutput += String.format(boardStructure.getMoveAsString(boardStructure.getPVArrayEntry(pvNum)) +
                        " ");
            }
            searchOutput += String.format("\n");
        }
    }

    /**
     * Clears search entry.
     *
     * @param boardStructure
     * @param searchEntry
     */
    public static void clearForSearch(BoardStructure boardStructure, SearchEntry searchEntry) {
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < BoardConstants.BOARD_SQR_NUM; j++)
                boardStructure.setSearchHistoryEntry(0, i, j);
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < BoardConstants.MAX_DEPTH; j++)
                boardStructure.setSearchKillerEntry(0, i, j);
        }

        boardStructure.getPVTable().clearPVTable();
        boardStructure.setPly(0);

        searchEntry.setStopped(false);
        searchEntry.setNodes(0);
        searchEntry.setFailHigh(0);
        searchEntry.setFailHighFirst(0);
    }

    /**
     * Checks time limit not reached, if so, stops search.
     * @param searchEntry
     */
    public static void checkUp(SearchEntry searchEntry) {
        if (searchEntry.isTimeSet() && Time.getTimeInMilliseconds() > searchEntry.getStopTime()) {
            searchEntry.setStopped(true);
        }
    }

    /**
     * Runs quiescence search.
     *
     * @param boardStructure
     * @param searchEntry
     * @param alpha
     * @param beta
     * @return alpha
     */
    public static int quiescenceSearch(BoardStructure boardStructure, SearchEntry searchEntry,
                                int alpha, int beta) {
        if ((searchEntry.getNodes() & 2047) == 0) {
            checkUp(searchEntry);
        }

        searchEntry.setNodes(searchEntry.getNodes()+1);

        if (isRepetition(boardStructure) || boardStructure.getFiftyMove() >= 100)
            return 0;

        if (boardStructure.getPly() > BoardConstants.MAX_DEPTH - 1)
            return PositionEvaluator.evaluatePosition(boardStructure);

        int score = PositionEvaluator.evaluatePosition(boardStructure);

        if (score >= beta) {
            return beta;
        }

        if (score >= alpha) {
            alpha = score;
        }

        MoveList moveList = new MoveList();
        MoveGenerator.generateAllCaptureMoves(boardStructure, moveList);

        int legal = 0;
        int oldAlpha = alpha;
        int bestMove = BoardUtils.NO_MOVE;
        score = -INF;
        int pvMove = PVTable.probePVTable(boardStructure);

        for (int moveNum = 0; moveNum < moveList.getCount(); moveNum++) {
            pickNextMove(moveList, moveNum);

            if (!MakeMove.makeMove(boardStructure, moveList.getMove(moveNum).getMove()))
                continue;

            legal++;
            score = -quiescenceSearch(boardStructure, searchEntry, -beta, -alpha);
            MakeMove.takeMove(boardStructure);

            if (searchEntry.isStopped()) {
                return 0;
            }

            if (score > alpha) {
                if (score >= beta) {
                    if (legal == 1) {
                        searchEntry.setFailHighFirst(searchEntry.getFailHighFirst()+1);
                    }
                    searchEntry.setFailHigh(searchEntry.getFailHigh()+1);

                    return beta;
                }
                alpha = score;
                bestMove = moveList.getMove(moveNum).getMove();
            }
        }

        if (alpha != oldAlpha) {
            PVTable.storePVMove(boardStructure, bestMove);
        }

        return alpha;
    }

    /**
     * Runs alpha-beta search.
     *
     * @param boardStructure
     * @param searchEntry
     * @param alpha
     * @param beta
     * @param depth
     * @param isNull
     * @return alpha
     */
    public static int alphaBeta(BoardStructure boardStructure, SearchEntry searchEntry,
                         int alpha, int beta, int depth, boolean isNull) {
        if (depth == 0) {
            return quiescenceSearch(boardStructure, searchEntry, alpha, beta);
        }

        if ((searchEntry.getNodes() & 2047) == 0) {
            checkUp(searchEntry);
        }

        searchEntry.setNodes(searchEntry.getNodes()+1);

        if (isRepetition(boardStructure) || boardStructure.getFiftyMove() >= 100)
            return 0;

        if (boardStructure.getPly() > BoardConstants.MAX_DEPTH - 1)
            return PositionEvaluator.evaluatePosition(boardStructure);

        MoveList moveList = new MoveList();
        MoveGenerator.generateAllMoves(boardStructure, moveList);
        int legal = 0;
        int oldAlpha = alpha;
        int bestMove = BoardUtils.NO_MOVE;
        int score = -INF;
        int pvMove = PVTable.probePVTable(boardStructure);

        if (pvMove != BoardUtils.NO_MOVE) {
            for (int moveNum = 0; moveNum < moveList.getCount(); moveNum++) {
                if (moveList.getMove(moveNum).getMove() == pvMove) {
                    moveList.getMove(moveNum).setScore(2000000);
                    break;
                }
            }
        }

        for (int moveNum = 0; moveNum < moveList.getCount(); moveNum++) {

            pickNextMove(moveList, moveNum);

            if (!MakeMove.makeMove(boardStructure, moveList.getMove(moveNum).getMove()))
                continue;

            legal++;
            score = -alphaBeta(boardStructure, searchEntry, -beta, -alpha, depth-1, true);
            MakeMove.takeMove(boardStructure);

            if (searchEntry.isStopped()) {
                return 0;
            }

            if (score > alpha) {
                if (score >= beta) {
                    if (legal == 1) {
                        searchEntry.setFailHighFirst(searchEntry.getFailHighFirst()+1);
                    }
                    searchEntry.setFailHigh(searchEntry.getFailHigh()+1);


                    if ((moveList.getMove(moveNum).getMove() & MoveUtils.MOVE_FLAG_CAPTURE) == 0) {
                        boardStructure.setSearchKillerEntry(boardStructure.getSearchKillersEntry(0,
                                boardStructure.getPly()), 1, boardStructure.getPly());
                        boardStructure.setSearchKillerEntry(moveList.getMove(moveNum).getMove(), 0,
                                boardStructure.getPly());
                    }

                    return beta;
                }
                alpha = score;
                bestMove = moveList.getMove(moveNum).getMove();

                if ((moveList.getMove(moveNum).getMove() & MoveUtils.MOVE_FLAG_CAPTURE) == 0) {
                    boardStructure.setSearchHistoryEntry(boardStructure
                            .getSearchHistoryEntry(boardStructure
                                    .getPiece(MoveUtils.from(bestMove)),
                                    MoveUtils.to(bestMove)) + depth,
                            boardStructure.getPiece(MoveUtils.from(bestMove)),
                            MoveUtils.to(bestMove));
                }
            }
        }

        if (legal == 0) {
            if (SquareAttacked.isSquareAttacked(boardStructure,
                    boardStructure.getKing(boardStructure.getSide()),
                    boardStructure.getSide() ^ 1)) {
                return -MATE + boardStructure.getPly();
            } else
                return 0;
        }

        if (alpha != oldAlpha)
            PVTable.storePVMove(boardStructure, bestMove);

        return alpha;
    }

    /**
     * Picks the next move.
     *
     * @param moveList
     * @param moveNum
     */
    public static void pickNextMove(MoveList moveList, int moveNum) {
        Move temp;
        int bestScore = 0;
        int bestNum = moveNum;

        for (int i = moveNum; i < moveList.getCount(); i++) {
            if (moveList.getMove(i).getScore() > bestScore) {
                bestScore = moveList.getMove(i).getScore();
                bestNum = i;
            }
        }

        temp = moveList.getMove(moveNum);

        moveList.setMove(moveList.getMove(bestNum), moveNum);
        moveList.setMove(temp, bestNum);
    }

}
