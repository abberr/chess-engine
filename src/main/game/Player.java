package main.game;

public enum Player {
	WHITE,
	BLACK;

	private Player opposite;
	private int value;
	private int hashValue;

	static {
		WHITE.opposite = BLACK;
		BLACK.opposite = WHITE;

		WHITE.value = 1;
		BLACK.value = -1;

		WHITE.hashValue = 1;
		BLACK.hashValue = 0;
	}

	public Player getOpponent() {
		return opposite;
	}

	public int getValue() {
		return value;
	}

	public int getHashValue() {
		return hashValue;
	}
}
