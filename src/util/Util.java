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

    public static int[][] reverseArray(int[][] myArray){
        int my_rows = myArray.length;
        int my_cols = myArray[0].length;
        int array[][]=new int[my_rows][my_cols];
        for(int i = my_rows-1; i >= 0; i--) {
            for(int j = my_cols-1; j >= 0; j--) {
                array[my_rows-1-i][my_cols-1-j] = myArray[i][j];
            }
        }
        return array;
    }
}
