package de.streblow.kasparov;

/**
 * List of moves.
 */
public class MoveList {

    private Move[] moves;
    private int count;

    /**
     * Initializes a MoveList.
     */
    protected MoveList() {
        moves = new Move[BoardConstants.MAX_POSITION_MOVES];
        count = 0;
    }

    /**
     * Getter for moves.
     *
     * @return moves
     */
    protected Move[] getMoves() {
        return moves;
    }

    /**
     * Getter for count.
     *
     * @return
     */
    protected int getCount() {
        return count;
    }

    /**
     * Setter for moves.
     *
     * @param moves
     */
    protected void setMoves(Move[] moves) {
        this.moves = moves;
    }

    /**
     * Setter for count.
     *
     * @param count
     */
    protected void setCount(int count) {
        this.count = count;
    }

    /**
     * Add a move to the MoveList.
     *
     * @param move
     */
    protected void addMove(int move) {
        moves[count] = new Move(move, 0);
        count++;
    }

    /**
     * Return the Move at the specified index.
     *
     * @param i
     * @return Move at index i
     */
    protected Move getMove(int i) {
        return moves[i];
    }

    /**
     * Sets the Move at the specified index.
     *
     * @param move
     * @param i
     */
    protected void setMove(Move move, int i) {
        moves[i] = move;
    }
}
