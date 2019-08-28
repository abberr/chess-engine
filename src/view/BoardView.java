package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import controller.Controller;
import game.Board;
import game.Move;
import game.Position;
import piece.Piece;

public class BoardView extends JPanel {

	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 800;
	private static final int SQUARE_SIZE = SCREEN_WIDTH / 8;
	private static final Color WHITE = new Color(255, 206, 158);
	private static final Color BLACK = new Color(209, 139, 71);

	private Controller contr;
	private Piece selectedPiece = null;

	public BoardView(Controller contr) {
		this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		this.contr = contr;

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int xSquare = e.getX() / SQUARE_SIZE;
				int ySquare = e.getY() / SQUARE_SIZE;

				// Select piece
				Position selectedSquare = new Position(xSquare, ySquare);

				// Make move with selected piece
				if (selectedPiece != null){
					// Check if available move
					boolean checksOut = false;
					for(Move move : contr.getMoves(selectedPiece)) {
						if (selectedSquare.equals(move.moveTo)) {
							checksOut = true;
						}
					}

					if (checksOut) {
//						System.out.println("move to " + selectedSquare);
						contr.executeMove(selectedPiece, selectedSquare);
						BoardView.this.repaint();
					}

                    selectedPiece = null;
				}
				else if (selectedPiece == null) {
					selectedPiece = contr.getPieceAt(selectedSquare);
					if(selectedPiece != null) {
						for(Move move : contr.getMoves(selectedPiece)) {
							System.out.print(move + ",");
						}
						System.out.println();
					}
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
			    Piece p = board.getPieceAt(new Position(i,j));
				if (p != null) {
					g.drawImage(p.getImage(), i * SQUARE_SIZE, j * SQUARE_SIZE, (int) (SQUARE_SIZE * 0.8), (int) (SQUARE_SIZE * 0.8), null);
				}
			}
		}
	}

}
