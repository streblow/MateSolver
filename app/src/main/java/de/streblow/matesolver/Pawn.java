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

public class Pawn extends Piece {
	private Bitmap bm;

	public Pawn(Context context, int x, int y, int size, boolean color){
		super(context, x, y, size, color);
		bm = null;
		if(super.getColor() == false){
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.whitepawn);
		}else{
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackpawn);
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
		
		if(super.getColor() == false){ //case that pawn is white
			if(oldY == 6){  //case that pawn is in original position
				//allows pawn to move two spaces ahead as long as there is no space in between
				if(Math.abs(newX - oldX) == 1 && newY == oldY -1 && b.hasPiece(newX, newY)){
                                        ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
					return true;
				}else if(newX == oldX && newY == oldY -1 && !b.hasPiece(newX, newY)){
                                        ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
					return true;
				}else if(newX == oldX && newY == oldY - 2 && !b.hasPiece(newX, newY)){
                                        ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
					return true;
				}
				return false;
			}else{//case pawn is nt in original position
				//check if pawn is capturing another piece
				if(Math.abs(newX - oldX) == 1 && newY == oldY -1 && b.hasPiece(newX, newY)){ 
                                        ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
					return true;
				//check that pawn is moving forward one square
				}else if(newX == oldX && newY == oldY -1 && !b.hasPiece(newX, newY)){
                                        ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
					return true;
				}
				return false;
			}
		}else{//case that pawn is black, repeat same rules as for white
			if(oldY == 1){
				if(Math.abs(newX - oldX) == 1 && newY == oldY +1 && b.hasPiece(newX, newY)){
                                        ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
					return true;
				}else if(newX == oldX && newY == oldY +1 && !b.hasPiece(newX, newY)){
                                        ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
					return true;
				}else if(newX == oldX && newY == oldY + 2 && !b.hasPiece(newX, newY)){
                                        ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
					return true;
				}
				return false;
			}else{//case pawn is not in original position
				if(Math.abs(newX - oldX) == 1 && newY == oldY +1 && b.hasPiece(newX, newY)){
                                        ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
					return true;
				}else if(newX == oldX && newY == oldY +1 && !b.hasPiece(newX, newY)){
                                        ///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
					return true;
				}
				return false;
			}
		}
	}

	/**
	 * the getType() method returns the type of the piece, in this case "Pawn"
	 * 
	 * @return the type of piece
	 */
	@Override
	public String getType() {
		return "Pawn";
	}

	public String getFENType(){
		if (color == true)
			return "p";
		else
			return "P";
	}

	@Override
	public String getUTFFigurine() {
		if (color == true)
			return "\u265F";
		else
			return "\u2659";
	}

}
