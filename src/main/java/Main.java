import controller.Controller;
import view.BoardView;
import view.ConsoleView;
import view.UciView;

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


		if (args.length == 1) {
			if (args[0].equals("1")) {
				ConsoleView view = new ConsoleView(contr);
				view.startGame();
			}
		} else {
			UciView uciView = new UciView(contr);
			new Thread(uciView).start();
		}

	}
}

