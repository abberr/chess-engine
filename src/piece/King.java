package piece;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import game.Position;
import util.Util;

public class King extends Piece {

	private int[][] pieceSquareTable = {
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-20,-30,-30,-40,-40,-30,-30,-20},
			{-10,-20,-20,-20,-20,-20,-20,-10},
			{20, 20,  0,  0,  0,  0, 20, 20},
			{20, 30, 10,  0,  0, 10, 30, 20}};

	public King(Player player) {
        super(player);
        if (player == Player.BLACK) {
            pieceSquareTable = Util.reverseArray(pieceSquareTable);
        }
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
	public int getValue(Position pos) {
		return 20000 + pieceSquareTable[pos.y][pos.x];
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
