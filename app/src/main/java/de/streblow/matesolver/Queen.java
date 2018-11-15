package de.streblow.matesolver;

/**
 * The queen class provides functionality for queen pieces. 
 * It extends from the Piece class, but also contains 
 * additional field BufferedImage img, which stores the image
 * of a queen, which is instantiated in the Queen constructor.  
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * The bishop class provides functionality for bishop pieces.
 * It extends from the Piece class, but also contains
 * additional field BufferedImage img, which stores the image
 * of a bishop, which is instantiated in the Bishop constructor.
 */

public class Queen extends Piece {
	private Bitmap bm;

	public Queen(Context context, int x, int y, int size, boolean color){
		super(context, x, y, size, color);
		bm = null;
		if(super.getColor() == false){
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.whitequeen);
		}else{
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackqueen);
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
		if(b.hasPiece(newX, newY)){
			if(b.getSquare(newX, newY).getColor() == super.getColor()){
				return false;
			}
		}
		/*
		 * combines rules for rook and bishop, allows for either scenario to occur (move in change in x/change in y = 1 pattern)
		 * or horizontal/vertical pattern.
		 * 
		 * 
		 * also checks if piece is still on the board, and makes checks to see if there are any other pieces in the way 
		 */
		if((oldX == newX) && (newX >= 0 && newX <= 7) && (newY >= 0 && newY <= 7)){
			/*
			 * For all possible patterns of movement, the Queen iterates through the board to check
			 * if there are any pieces in the way in that direction
			 */
			if(newY > oldY){
				for(int i = 1; i< newY-oldY; i++){ 
					if(b.hasPiece(oldX, oldY + i)){ 
						return false;
					}
				}
                                ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
			if(newY < oldY){
				for(int i= 1; i<oldY - newY; i++){
					if(b.hasPiece(oldX, newY + i)){
						return false;
					}
				}
                                ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
			if(newY == oldY){
                                ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
                        ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
			return true;
		}
		if((oldY == newY) && (newX >= 0 && newX <= 7) && (newY >= 0 && newY <= 7)){
			if(newX > oldX){
				for(int i = 1; i<newX-oldX; i++){
					if(b.hasPiece(oldX + i, oldY)){
						return false;
					}
				}
                                ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
			
			if(newX < oldX){
				for(int i = 1; i<oldX-newX; i++){
					if(b.hasPiece(newX + i, oldY)){
						return false;
					}
				}
                                ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
			if(newX == oldX){
                                ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
		}
		if(Math.abs(newX - oldX) == Math.abs(newY - oldY) && (newX >= 0 && newX <= 7) && (newY >= 0 && newY <= 7)){
			if(newX - oldX > 0 && newY - oldY > 0){
				for(int i= 1; i< newX-oldX; i++){
					if(b.hasPiece(oldX + i,oldY + i)){
						return false;
					}
				}
                                ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
			if(newX - oldX < 0 && newY - oldY < 0){
				for(int i = 1; i< oldX - newX; i++){
					if(b.hasPiece(newX + i, newY + i)){
						return false;
					}
				}
                                ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
			if(((newX - oldX) > 0) && ((newY - oldY) < 0)){
				for(int i = 1; i< newX - oldX; i++){
					if(b.hasPiece(oldX + i, oldY - i)){
						return false;
					}
				}
                                ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
			if(newX - oldX < 0 && newY - oldY > 0){
				for(int i = 1; i< Math.abs(newX - oldX); i++){
					if(b.hasPiece(oldX -i, oldY + i)){
						return false;
					}
				}
                                ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
                        ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
			return true;
		}
		return false;
	}

	/**
	 * The getType() method returns the type of the piece, in this case "Queen"
	 */
	public String getType(){
		return "Queen";
	}

	public String getFENType(){
		if (color == true)
			return "q";
		else
			return "Q";
	}

	@Override
	public String getUTFFigurine() {
		if (color == true)
			return "\u265B";
		else
			return "\u2655";
	}

}
