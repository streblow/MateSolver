package de.streblow.matesolver;

/**
 * The knight class provides functionality for knight pieces. 
 * It extends from the Piece class, but also contains 
 * additional field BufferedImage img, which stores the image
 * of a knight, which is instantiated in the Knight constructor.  
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

public class Knight extends Piece {
	private Bitmap bm;

	public Knight(Context context, int x, int y, int size, boolean color){
		super(context, x, y, size, color);
		bm = null;
		if(super.getColor() == false){
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.whiteknight);
		}else{
			bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackknight);
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
		//check the legal move
		/*
		 * the legal moves allowed for knights are ones where the change in x is 2 and the change in y is 1 or the other 
		 * way around, where the change in y is 2 and the change in x is 1.  Knights can jump over other pieces,
		 * so there is not need to check if other pieces are in the way.  
		 */
		if(Math.abs(newX - oldX) == 2 && Math.abs(newY - oldY) == 1 && (newX >= 0 && newX <= 7) && (newY >= 0 && newY <= 7)){
			///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
			return true;
		}else if(Math.abs(newX -oldX) == 1 && Math.abs(newY - oldY) == 2 && (newX >= 0 && newX <= 7) && (newY >= 0 && newY <= 7)){
			///*debug*/System.out.println("Legal move: " + getType()  + " from: (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
			return true;
		}else{
			return false;
		}
	}

	/**
	 * the getType() method returns the type of the piece, in this case "Knight"
	 * 
	 * @return the type of piece
	 */
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "Knight";
	}

	public String getFENType(){
		if (color == true)
			return "n";
		else
			return "N";
	}

	@Override
	public String getUTFFigurine() {
		if (color == true)
			return "\u265E";
		else
			return "\u2658";
	}

}
