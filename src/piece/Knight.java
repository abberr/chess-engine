package piece;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import game.Position;

public class Knight extends Piece{

	public Knight(Player player) {
		super(player);
	}

	@Override
	public Type getType() {
		return Type.KNIGHT;
	}

	@Override
	public BufferedImage getImage() {
		try {
			if (player == Player.WHITE)
				return ImageIO.read(new File("src/res/kw.png"));
			else
				return ImageIO.read(new File("src/res/kb.png"));
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}

	@Override
	public Position[] getAvailableMoves(Position pos , Piece [][] board) {
		ArrayList<Position> moves = new ArrayList<>();
		
		moves.add(new Position(pos.x+2, pos.y+1));
		moves.add(new Position(pos.x+2, pos.y-1));
		moves.add(new Position(pos.x+1, pos.y+2));
		moves.add(new Position(pos.x+1, pos.y-2));
		moves.add(new Position(pos.x-1, pos.y+2));
		moves.add(new Position(pos.x-1, pos.y-2));
		moves.add(new Position(pos.x-2, pos.y+1));
		moves.add(new Position(pos.x-2, pos.y-1));
		
		removeIllegalMoves(moves, board);
		
		return moves.toArray(new Position[0]);
	}
	
	@Override
	public int getValue() {
		return 3;
	}

	@Override
	public int getIndex() {
		if (player == Player.WHITE) {
			return 2;
		}
		else {
			return 8;
		}
	}

}
