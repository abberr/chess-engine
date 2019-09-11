package main.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import main.controller.Controller;
import main.game0x88.Board0x88;
import main.game0x88.Move;
import main.piece.Piece;

public class BoardView extends JPanel {

	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 800;
	private static final int SQUARE_SIZE = SCREEN_WIDTH / 8;
	private static final Color WHITE = new Color(255, 206, 158);
	private static final Color BLACK = new Color(209, 139, 71);

	private Controller contr;
	private String selectedSquare = null;

	public BoardView(Controller contr) {
		this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		this.contr = contr;

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int xSquare = e.getX() / SQUARE_SIZE;
				int ySquare = e.getY() / SQUARE_SIZE;

				String clickedSquare = ""; //TODO

				// Select main.piece

				// Make move with selected main.piece
				if (selectedSquare != null){
					// Check if available move

//					System.out.println("move to " + selectedSquare);
					contr.executeMove(clickedSquare);
					BoardView.this.repaint();

					System.out.println("Moving");

//					contr.computerMove();
//					BoardView.this.repaint();


                    selectedSquare = null;
				}
				else if (selectedSquare == null) {
					selectedSquare = clickedSquare;
					if(selectedSquare != null) {
						for(Move move : contr.getMovesFromSquare(clickedSquare)) {
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
		Board0x88 board = contr.getBoard();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
			    //TODO get piece
                byte piece = 0;
				if (piece != 0) {
//					g.drawImage(p.getImage(), i * SQUARE_SIZE, j * SQUARE_SIZE, (int) (SQUARE_SIZE * 0.8), (int) (SQUARE_SIZE * 0.8), null);
				}
			}
		}
	}

}
