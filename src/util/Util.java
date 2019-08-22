package util;

import piece.Piece;

public class Util {
	public static Piece[][] copyBoard(Piece[][] array) {
		Piece [][] copy = new Piece[array.length][array[0].length];
		for (int i = 0; i < copy.length; i++) {
			for (int j = 0; j < copy.length; j++) {
				copy[i][j] = array[i][j];
			}
		}
		return copy;
	}
}
