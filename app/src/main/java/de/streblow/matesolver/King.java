package de.streblow.matesolver;

/**
 * The king class provides functionality for king pieces. 
 * It extends from the Piece class, but also contains 
 * additional field BufferedImage img, which stores the image
 * of a bishop, which is instantiated in the King constructor.  
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class King extends Piece {
	private Bitmap bm; //stores image object

    public King(Context context, int x, int y, int size, boolean color){
        super(context, x, y, size, color);
        bm = null;
        if(super.getColor() == false){
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.whiteking);
        }else{
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackking);
        }
        setBitmapsize(bm.getWidth());
    }

    public void draw(Canvas canvas, Paint paint){
        Rect src = new Rect(0, 0, getBitmapsize() - 1, getBitmapsize() - 1);
        Rect dst = new Rect(getX(), getY(), getX() + getSize(), getY() + getSize());
        canvas.drawBitmap(bm, src, dst, paint);
    }

    /**
     * method checks what legal moves are possible from a given point in the chess board
     */
    public boolean checkLegalMove(Point p, Board b){
        int oldX = (int)super.getSquareOn().x;
        int oldY = (int)super.getSquareOn().y;
        int newX = (int)p.x/ getSize();
        int newY = (int)p.y/ getSize();
        /*
         * checks if the square being moved to has a piece of the same color
         */
        if (b.hasPiece(newX, newY)) {
            if (b.getSquare(newX, newY).getColor() == super.getColor()) {
                return false;
            }
        }
        /*
         * Checks if the difference between the new x and old x and new y and old y is less then 1 (it only moved one square
         * and that the piece is still on the board
         */
        if (Math.abs(newX - oldX) <= 1 && Math.abs(newY - oldY) <=1 && (newX >= 0 && newX <= 7) && (newY >= 0 && newY <= 7)) {
            ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
            return true;
        }
        /*
         * Allow for castling
         */							
        if (super.getColor() == false) { //case that piece is white
            if (b.hasPiece(7, 7)) { //short castling
                if(b.getSquare(7, 7).getType().equals("Rook")) {
                    if (oldX == 4 && oldY == 7 && newX == 6 && newY == 7) {
                        if (!b.hasPiece(5, 7) && !b.hasPiece(6, 7)) {
                            ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
                            return true;
                        }
                    }
                }
            }
            if (b.hasPiece(0, 7)) { //long castling
                if(b.getSquare(0, 7).getType().equals("Rook")) {
                    if (oldX == 4 && oldY == 7 && newX == 2 && newY == 7) {
                        if (!b.hasPiece(1, 7) && !b.hasPiece(2, 7) && !b.hasPiece(3, 7)) {
                            ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
                            return true;
                        }
                    }
                }
            }
        } else { //case that piece is black
            if (b.hasPiece(7, 0)) { //short castling
                if (b.getSquare(7, 0).getType().equals("Rook")) {
                    if (oldX == 4 && oldY == 0 && newX == 6 && newY == 0) {
                        if (!b.hasPiece(5, 0) && !b.hasPiece(6, 0)) {
                            ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
                            return true;
                        }
                    }
                }
            }
            if (b.hasPiece(0, 0)) { //long castling
                if (b.getSquare(0, 0).getType().equals("Rook")) {
                    if (oldX == 4 && oldY == 0 && newX == 2 && newY == 0) {
                        if (!b.hasPiece(1, 0) && !b.hasPiece(2, 0) && !b.hasPiece(3, 0)) {
                            ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
	
    /**
     * The getType() method returns the type of the piece, in this case "King"
     */
    public String getType(){
        return "King";
    }

    public String getFENType(){
        if (color == true)
            return "k";
        else
            return "K";
    }

    @Override
    public String getUTFFigurine() {
        if (color == true)
            return "\u265A";
        else
            return "\u2654";
    }

}
