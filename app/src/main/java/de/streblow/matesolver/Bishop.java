package de.streblow.matesolver;

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

public class Bishop extends Piece {
	private Bitmap bm;
	
	public Bishop(Context context, int x, int y, int size, boolean color){
		super(context, x, y, size, color);
		bm = null;
		if(super.getColor() == false){
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.whitebishop);
		}else{
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackbishop);
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
		 *checks if piece moved in a rise/run = 1 pattern by making sure that change in y = change in x
		 *and also checks that the piece is still on the board. Also runs through board and checks that there
		 *are no pieces in the way of the piece getting through
		 *
		 *Iterates through all possible directions the Bishop can move to ensure there are no other pieces in 
		 *its path
		 */
		
		
		if(Math.abs(newX - oldX) == Math.abs(newY - oldY) && (newX >= 0 && newX <= 7) && (newY >= 0 && newY <= 7)){ //case that it moved in the correct pattern
			if(newX - oldX > 0 && newY - oldY > 0){ //checks if moved to the right and down, iterates through board,
				for(int i= 1; i< newX-oldX; i++){   // finds if there are any pieces in the way
					if(b.hasPiece(oldX + i,oldY + i)){
						return false;
					}
				}
				///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
			if(newX - oldX < 0 && newY - oldY < 0){ //checks if it moved left and up
				for(int i = 1; i< oldX - newX; i++){
					if(b.hasPiece(newX + i, newY + i)){
						return false;
					}
				}
				///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
			if(((newX - oldX) > 0) && ((newY - oldY) < 0)){ //checked if it moved right and up
				for(int i = 1; i< newX - oldX; i++){
					if(b.hasPiece(oldX + i, oldY - i)){
						return false;
					}
				}
				///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
			if(newX - oldX < 0 && newY - oldY > 0){ //checks if it moved left and down
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
		return false; //return false if it went in wrong pattern
	}
	
	/**
	 * The getType() method returns the type of the piece, in this case "Bishop"
	 */
	public String getType(){
		return "Bishop";
	}
        
	public String getFENType(){
		if (color == true)
			return "b";
		else
			return "B";
	}

	@Override
	public String getUTFFigurine() {
		if (color == true)
			return "\u265D";
		else
			return "\u2657";
	}

}
