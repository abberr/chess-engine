package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import controller.Controller;
import game.Board;
import game.Move;
import game.MoveList;
import game.Pieces;
import util.Util;

public class BoardView extends JPanel {

	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 800;
	private static final int SQUARE_SIZE = SCREEN_WIDTH / 8;
	private static final Color WHITE = new Color(255, 206, 158);
	private static final Color BLACK = new Color(209, 139, 71);

	private static final BufferedImage[] pieceImages = new BufferedImage[12];

	private Controller contr;
	private String selectedSquare = null;

	public BoardView(Controller contr) {
		this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		this.contr = contr;
		initImages();

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int xSquare = e.getX() / SQUARE_SIZE;
				int ySquare = e.getY() / SQUARE_SIZE;

				int clickedSquareIndex = ((7*16) - 16*ySquare) + xSquare;
				String clickedSquare = Util.indexToAlgebraicNotation(clickedSquareIndex);

				// Make move with selected main.piece
				if (selectedSquare != null){
					// Check if available move

//					System.out.println("move to " + selectedSquare);
					if (!contr.executeMove(selectedSquare + clickedSquare)) {
					    selectedSquare = null;
						return;
					}
					BoardView.this.repaint();
					contr.getBoard().printBoard();

					System.out.println("Moving");

					contr.computerMove();
					BoardView.this.repaint();


                    selectedSquare = null;
				}
				else if (selectedSquare == null) {
					List<Move> moves = contr.getMovesFromSquare(clickedSquare);
					if (moves != null) {
						selectedSquare = clickedSquare;
					}
//					if(selectedSquare != null) {
//						for(Move move : contr.getMovesFromSquare(clickedSquare)) {
//							System.out.print(move + ",");
//						}
//						System.out.println();
//					}
				}

			}
		});
	}

	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(BLACK);
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		g.setColor(WHITE);
		// Paint white squares
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 4; j++) {
				g.fillRect((j * 2 * SQUARE_SIZE) + (i % 2) * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}
		}

		// Paint pieces
		Board board = contr.getBoard();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
                byte piece = board.getSquares()[((7*16) - 16*j) + i];
				if (piece != Pieces.EMPTY_SQUARE) {
					g.drawImage(pieceImages[piece - 1], i * SQUARE_SIZE, j * SQUARE_SIZE, (int) (SQUARE_SIZE * 0.8), (int) (SQUARE_SIZE * 0.8), null);
				}
			}
		}
	}

	public BufferedImage initImages() {
		try {
			pieceImages[0] = ImageIO.read(new File("src/main/java/res/pw.png"));
			pieceImages[1] = ImageIO.read(new File("src/main/java/res/kw.png"));
			pieceImages[2] = ImageIO.read(new File("src/main/java/res/bw.png"));
			pieceImages[3] = ImageIO.read(new File("src/main/java/res/rw.png"));
			pieceImages[4] = ImageIO.read(new File("src/main/java/res/qw.png"));
			pieceImages[5] = ImageIO.read(new File("src/main/java/res/kingw.png"));
			pieceImages[6] = ImageIO.read(new File("src/main/java/res/pb.png"));
			pieceImages[7] = ImageIO.read(new File("src/main/java/res/kb.png"));
			pieceImages[8] = ImageIO.read(new File("src/main/java/res/bb.png"));
			pieceImages[9] = ImageIO.read(new File("src/main/java/res/rb.png"));
			pieceImages[10] = ImageIO.read(new File("src/main/java/res/qb.png"));
			pieceImages[11] = ImageIO.read(new File("src/main/java/res/kingb.png"));
		} catch (IOException e) {
			System.out.println(e);
		}
		return null;
	}

}
