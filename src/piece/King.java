package piece;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import game.Position;

public class King extends Piece {

	public King(Player player) {
		super(player);
	}

	@Override
	public Type getType() {
		return Type.KING;
	}

	@Override
	public BufferedImage getImage() {
		try {
			if (player == Player.WHITE)
				return ImageIO.read(new File("src/res/kingw.png"));
			else
				return ImageIO.read(new File("src/res/kingb.png"));
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}

	@Override
	public Position[] getAvailableMoves(Position pos, Piece [][] board) {
		ArrayList<Position> moves = new ArrayList<>();

		moves.add(new Position(pos.x, pos.y + 1));
		moves.add(new Position(pos.x, pos.y - 1));
		moves.add(new Position(pos.x + 1, pos.y + 1));
		moves.add(new Position(pos.x + 1, pos.y));
		moves.add(new Position(pos.x + 1, pos.y - 1));
		moves.add(new Position(pos.x - 1, pos.y + 1));
		moves.add(new Position(pos.x - 1, pos.y));
		moves.add(new Position(pos.x - 1, pos.y - 1));

		removeIllegalMoves(moves, board);

		return moves.toArray(new Position[0]);
	}
	
	@Override
	public int getValue() {
		return 200;
	}

	@Override
	public int getIndex() {
		if (player == Player.WHITE) {
			return 5;
		}
		else {
			return 11;
		}
	}

}
