package piece;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import game.Position;
import util.Util;

public class Queen extends Piece{

	
	public Queen(Player player) {
		super(player);
	}

	@Override
	public Type getType() {
		return Type.QUEEN;
	}

	@Override
	public BufferedImage getImage() {
		try {
			if (player == Player.WHITE)
				return ImageIO.read(new File("src/res/qw.png"));
			else
				return ImageIO.read(new File("src/res/qb.png"));
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}

	@Override
	public Position[] getAvailableMoves(Position pos, Piece [][] board) {
		ArrayList<Position> moves = new ArrayList<>();

		//Create temporary rook and bishop and get their available moves
		Piece[][] tempBoard = Util.copyBoard(board);
		tempBoard[pos.x][pos.y] = new Rook(player);
		moves.addAll(new ArrayList<Position>(Arrays.asList(tempBoard[pos.x][pos.y].getAvailableMoves(pos, board))));
		tempBoard[pos.x][pos.y] = new Bishop(player);
		moves.addAll(new ArrayList<Position>(Arrays.asList(tempBoard[pos.x][pos.y].getAvailableMoves(pos, board))));	

		return moves.toArray(new Position[0]);
	}
	
	@Override
	public int getValue() {
		return 9;
	}

	@Override
	public int getIndex() {
		if (player == Player.WHITE) {
			return 4;
		}
		else {
			return 10;
		}
	}

}
