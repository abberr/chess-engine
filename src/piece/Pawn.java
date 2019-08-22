package piece;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import game.Position;

public class Pawn extends Piece{

	
	public Pawn(Player player) {
		super(player);
	}

	@Override
	public Type getType() {
		return Type.PAWN;
	}
		

	@Override
	public BufferedImage getImage() {
		try {
			if (player == Player.WHITE)
				return ImageIO.read(new File("src/res/pw.png"));
			else
				return ImageIO.read(new File("src/res/pb.png"));
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}

	@Override
	public Position[] getAvailableMoves(Position pos, Piece [][] board) {
		ArrayList<Position> moves = new ArrayList<>();
		int direction = -1;
		
		boolean hasMoved = true;
		if ((player == Player.BLACK && pos.y == 1) || (player == Player.WHITE && pos.y == 6)) {
			hasMoved = false;
		}
		
		if (player == Player.BLACK)
			direction = 1;
			if (pos.y + direction <8 && pos.y + direction >= 0) {
				//Straigt forward
				if (board[pos.x][pos.y + direction] == null) {
					moves.add(new Position(pos.x,pos.y + direction));
					
					//If startpos, two moves available
					if (!hasMoved && pos.y+2*direction < 6 && pos.y+2*direction > 1 && board[pos.x][pos.y+2*direction] == null) {
						moves.add(new Position(pos.x,pos.y+2*direction));
					}
				}
				
				//Capturing moves
				if (pos.x>0 && board[pos.x-1][pos.y + direction] != null && board[pos.x-1][pos.y + direction].player != player) {
					moves.add(new Position(pos.x-1,pos.y + direction));
				}
				if (pos.x<7 && board[pos.x+1][pos.y + direction] != null && board[pos.x+1][pos.y + direction].player != player) {
					moves.add(new Position(pos.x+1,pos.y + direction));
				}	
			}	
		
		
		return moves.toArray(new Position[0]);
	}
	
	@Override
	public int getValue() {
		return 1;
	}

}
