import controller.Controller;
import view.BoardView;
import view.ConsoleView;

import javax.swing.*;

public class Main {
	
	
	
	public static void main(String [] args) {
		Controller contr = new Controller();

//		JFrame frame = new JFrame();
//		frame.add(new BoardView(contr));
//		frame.setSize(830, 830);
//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);

        ConsoleView view = new ConsoleView(contr);
        view.startGame();

	}
}

