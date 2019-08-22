package piece;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import game.Position;

public class Bishop extends Piece{

	public Bishop(Player player) {
		super(player);
	}

	@Override
	public Type getType() {
		return Type.BISHOP;
	}

	@Override
	public BufferedImage getImage() {
		try {
			if (player == Player.WHITE)
				return ImageIO.read(new File("src/res/bw.png"));
			else
				return ImageIO.read(new File("src/res/bb.png"));
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}

	@Override
	public Position[] getAvailableMoves(Position pos, Piece [][] board) {
		ArrayList<Position> moves = new ArrayList<>();

		// Traverse southeast
		int x = pos.x + 1;
		int y = pos.y + 1;
		while (!isOutOfBounds(x, y) && board[x][y] == null) {
			moves.add(new Position(x, y));
			y++;
			x++;
		}
		if (!isOutOfBounds(x, y) && board[x][y] != null && board[x][y].player != this.player) {
			moves.add(new Position(x, y));
		}

		// Traverse southwest
		y = pos.y + 1;
		x = pos.x - 1;
		while (!isOutOfBounds(x, y) && board[x][y] == null) {
			moves.add(new Position(x, y));
			y++;
			x--;
		}
		if (!isOutOfBounds(x, y) && board[x][y] != null && board[x][y].player != this.player) {
			moves.add(new Position(x, y));
		}

		// Traverse northwest
		x = pos.x - 1;
		y = pos.y - 1;
		while (!isOutOfBounds(x, y) && board[x][y] == null) {
			moves.add(new Position(x, y));
			x--;
			y--;
		}
		if (!isOutOfBounds(x, y) && board[x][y] != null && board[x][y].player != this.player) {
			moves.add(new Position(x, y));
		}

		// Traverse northeast
		x = pos.x + 1;
		y = pos.y - 1;
		while (!isOutOfBounds(x, y) && board[x][y] == null) {
			moves.add(new Position(x, y));
			x++;
			y--;			
		}
		if (!isOutOfBounds(x, y) && board[x][y] != null && board[x][y].player != this.player) {
			moves.add(new Position(x, y));
		}

		return moves.toArray(new Position[0]);
	}

	@Override
	public int getValue() {
		return 3;
	}

}
