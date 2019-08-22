package game;


public class Position {
	
	public int x,y;
	private static char[] xAxis = {'A' , 'B' , 'C' , 'D', 'E' , 'F' , 'G' , 'H'};

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		Position pos2 = (Position) o;
		return x == pos2.x && y == pos2.y;
	}

	@Override
	public String toString() {
		return "" + xAxis[x] + (8-y);
		//return "[" + x + " , " + y + "]";
	}

	

}
