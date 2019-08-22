import javax.swing.JFrame;
import javax.swing.WindowConstants;

import controller.Controller;
import view.BoardView;

public class Main {
	
	
	
	public static void main(String [] args) {
		JFrame frame = new JFrame();
		Controller contr = new Controller();
		frame.add(new BoardView(contr));
		frame.setSize(830, 830);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
