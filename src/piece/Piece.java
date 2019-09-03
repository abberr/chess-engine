package piece;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.Player;
import game.Position;

public abstract class Piece {
	
	public Player player;

	int [][] pieceSquareTable;
	
	public Piece(Player player) {
		this.player = player;
	}
	
	public abstract Position[] getAvailableMoves(Position pos, Piece [][] board);

	public abstract int getValue(Position pos);

	public abstract int getIndex(); //Zobrist hash

	public abstract Type getType();

	public abstract BufferedImage getImage();

	public abstract char getUnicodeSymbol();

	public abstract char getSymbol();

	protected void removeIllegalMoves(ArrayList<Position> moves, Piece [][] board) {
		for (int i = 0; i < moves.size(); i++) {
			int x = moves.get(i).x;
			int y = moves.get(i).y;
			//If out of bounds
			if (isOutOfBounds(x,y)) {
				moves.remove(i);
				i--;
			} 
			//If occupied by piece of same color
			else if (board[x][y] != null && board[x][y].player == player) {
				moves.remove(i);
				i--;
			}
		}
	}
	
	protected static boolean isOutOfBounds(int x, int y) {		
		return (x < 0 || x > 7 || y < 0 || y > 7);		
	}
	
	@Override
	public String toString() {
		return getType() + "";
	}

}
