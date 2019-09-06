package main.game;

public enum Player {
	WHITE,
	BLACK;

	private Player opposite;
	private int value;

	static {
		WHITE.opposite = BLACK;
		BLACK.opposite = WHITE;

		WHITE.value = 1;
		BLACK.value = -1;
	}

	public Player getOpponent() {
		return opposite;
	}

	public int getValue() {
		return value;
	}
}
