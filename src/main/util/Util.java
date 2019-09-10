package main.util;

import main.piece.Piece;

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

    public static byte[] copySquares(byte[] array) {
        byte [] copy = new byte[array.length];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = array[i];
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

    public static int[] invertSquareValueTable(int[] array){
        int [] reversedArray = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            if ((i&0x88) != 0) continue;
            int row = i >> 4;
            int col = i&0b111;
            int newRowLoc = 0x70 - (row*16) + col;
            reversedArray[newRowLoc] = array[i];
        }

        return reversedArray;
    }

    //index mask: 0rrr0ccc
    public static String indexToAlgebraicNotation(int index) {
        int rank = index>>4;
        int file = index&7;

        return "" + (char)('a' + file) + (rank + 1);
    }

    public static int algebraicNotationToIndex(String position) {
        int file = position.charAt(0) - 'a';
        int rank = (position.charAt(1) - '0') - 1;

        return file + 16*rank;
    }
}
