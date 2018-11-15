package de.streblow.matesolver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public abstract class Piece {

    protected Context context;
    private int x;
    private int y;
    protected int size;
    protected int bitmapsize;
    private Point squareOn;
    protected boolean color;
    private boolean selected;

    /**
     * The constructor Piece(Context context, int x, int y, int size, boolean color) takes a square on the board as parameter, sets the squareOn field
     * to that value, and the x and y location to that value multiplied by <size>, as each chess square is <size> pixels
     * wide and tall, color is true for black and false for white
     *
     * @param context
     * @param x
     * @param y
     * @param size
     * @param color
     */
    public Piece(Context context, int x, int y, int size, boolean color) {
        this.context = context;
        squareOn = new Point(x, y);
        this.size = size;
        this.x = x * size;
        this.y = y * size;
        this.selected = false;
        this.color = color;
    }

    /**
     * The getSquareOn method returns the square that the piece is on as a point object
     * @return Point, the square that the piece is on
     */
    public Point getSquareOn() {
        return squareOn;
    }

    /**
     * the setSelected(boolean selected) method sets the selected field to the parameter selected.
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * The method isSelected returns whether or not the piece is selected.
     * @return boolean, true if piece is selected, false if piece is not selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * the getLocation() method returns the location of the piece as a Point, with the pixel location x,y
     * @return Point, the location of the piece
     */
    public Point getLocation() {
        return new Point(x, y);
    }

    /**
     * the getX() method returns the x value of the location of the piece
     * @return int, the x value of the location of hte piece
     */
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    /**
     * the getY() method returns the y-value of the location of the piece.
     * @return int, the y value of the location of the piece
     */
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * The contains(Point p) method takes a Point as a parameter and checks if the point lies on the piece.
     *
     * The method checks this by checking if if the point that the user clicked was between x and x+<size> and between y and
     * y+<size>, as each square has width and height of 62 pixels
     * @param p
     * @return boolean, true if point is contained, false if it is not
     */
    public boolean contains(Point p) {
        return x <= p.x && (x + getSize()) >= p.x && y <= p.y && (y + getSize()) >= p.y;
    }

    /**
     * The translate method is used when a piece is being dragged and it moves the piece
     * by parameters dx and dy.
     *
     * @param dx
     * @param dy
     */
    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /**
     * The setLocation(int row, int col) takes a row and column from the board as parameters,
     * sets x and y to those multiplied by size, and sets the squareOn point to that point.
     *
     * Used to set a piece on a given square
     * @param row
     * @param col
     */
    public boolean setLocation(int row, int col) {
        x = row * getSize();
        y  = col * getSize();
        squareOn.set(row, col);
        return true;
    }

    public void setLocationXY(int row, int col) {
        x = row * getSize();
        y  = col * getSize();
    }

    public boolean getColor() {
        return color;
    }

    /**
     * The getType() method returns the type of piece (rook, king, bishop)
     * @return String, the type of piece
     */
    public abstract String getType();

    /**
     * The getFENType() method returns the FEN type of piece (r, R, k, K, b, B)
     * @return String, the FEN type of piece
     */
    public abstract String getFENType();

    /**
     * The getUTFFigurine() method returns the UTF16 figurine code of piece
     * @return String, the FEN type of piece
     */
    public abstract String getUTFFigurine();

    /**
     * The draw method governs how to draw each piece
     * @param canvas
     */
    public abstract void draw(Canvas canvas, Paint paint);

    /**
     * The checkLegalMove(Point p) method takes a point in pixels and
     * determines whether the move is legal or not for the piece
     *
     * @return boolean, true if move is legal, false if it is not
     */
    public abstract boolean checkLegalMove(Point p, Board b);

    public Context getContext() {
        return context;
    }

    public int getSize() {
        return size;
    }

    public int getBitmapsize() {
        return bitmapsize;
    }

    public void setBitmapsize(int bitmapsize) {
        this.bitmapsize = bitmapsize;
    }
}