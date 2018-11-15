package de.streblow.matesolver;

/**
 * The rook class provides functionality for rook pieces. 
 * It extends from the Piece class, but also contains 
 * additional field BufferedImage img, which stores the image
 * of a rook, which is instantiated in the Rook constructor.  
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

public class Rook extends Piece {
	private Bitmap bm;

	public Rook(Context context, int x, int y, int size, boolean color){
		super(context, x, y, size, color);
		bm = null;
		if(super.getColor() == false){
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.whiterook);
		}else{
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackrook);
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
		 * Checks if piece is still in line with either the x or the y and makes sure that
		 * the piece is still on the board.  
		 */
		if((oldX == newX) && (newX >= 0 && newX <= 7) && (newY >= 0 && newY <= 7)){
			if(newY > oldY){ //checks if there are any pieces in the way if the rook is moving down
				for(int i = 1; i< newY-oldY; i++){ //iterates through board to check for pieces
					if(b.hasPiece(oldX, oldY + i)){
						return false;
					}
				}
                                ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
			if(newY < oldY){ //checks if there are pieces in the way if the rook moves up
				for(int i= 1; i<oldY - newY; i++){ //iterates through board, checks for pieces
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
			if(newX > oldX){ //checks if there are pieces in the way if rook moves right
				for(int i = 1; i<newX-oldX; i++){ //iterates through board checking for pieces
					if(b.hasPiece(oldX + i, oldY)){
						return false;
					}
				}
                                ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
				return true;
			}
			if(newX < oldX){ //checks if there are pieces in teh way if the rook moves left
				for(int i = 1; i<oldX-newX; i++){ //iterates through board
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
		return false;
	}
	
	/**
	 * the getType() method returns the type of the piece, in this case "Rook"
	 * 
	 * @return the type of piece
	 */
	public String getType(){
		return "Rook";
	}

	public String getFENType(){
		if (color == true)
			return "r";
		else
			return "R";
	}

	@Override
	public String getUTFFigurine() {
		if (color == true)
			return "\u265C";
		else
			return "\u2656";
	}

}
