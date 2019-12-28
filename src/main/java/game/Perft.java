package game;

import static game.Pieces.EMPTY_SQUARE;

public class Perft {
    static long epCounter, checkCounter, promotionsCounter, nodesCounter, capturesCounter, castlingCounter;

    public static long perft(Board board, int searchDepth) {
        long time = System.currentTimeMillis();
        long calculations = perftRecursive(board, searchDepth);

        long evalTime = System.currentTimeMillis() - time;
        float evalsPerSecond = ((float) calculations / evalTime) * 1000;

        System.out.println(calculations + " moves calculated in " + evalTime + "ms. Evaluations per second: " + evalsPerSecond);

        return calculations;
    }

    private static long perftRecursive(Board board, int depth) {
        if (depth == 0) return 1;
        long nodes = 0;
        MoveList moves = board.getAvailableMoves();
        for (Move move : moves) {
            board.executeMove(move);
            if (board.isInCheck(board.getPlayerToMove().getOpponent())) {
                board.executeInvertedMove(move);
                continue;
            }
            nodes += perftRecursive(board, depth - 1);
            board.executeInvertedMove(move);
        }

        return nodes;
    }

    public static long perftDetailed(Board board, int searchDepth) {
        capturesCounter = 0;
        nodesCounter = 0;
        promotionsCounter = 0;
        epCounter = 0;
        checkCounter = 0;
        castlingCounter = 0;

        long time = System.currentTimeMillis();
        perftDetailedRecursive(board, searchDepth);

        long evalTime = System.currentTimeMillis() - time;
        float evalsPerSecond = ((float) nodesCounter / evalTime) * 1000;

        System.out.println(nodesCounter + " moves calculated in " + evalTime + "ms. Nodes per second: " + evalsPerSecond);
        System.out.println("captures: " + capturesCounter);
        System.out.println("promos: " + promotionsCounter);
        System.out.println("ep: " + epCounter);
        System.out.println("checks: " + checkCounter);
        System.out.println("castles: " + castlingCounter);

        return nodesCounter;
    }


    private static void perftDetailedRecursive(Board board, int depth) {
        if (depth == 0) return;
        MoveList moves = board.getAvailableMoves();
        for (Move move : moves) {

            board.executeMove(move);
            if (board.isInCheck(board.getPlayerToMove().getOpponent())) {
                board.executeInvertedMove(move);
                continue;
            }

            if (depth == 1) {
                nodesCounter++;
                if (move.getCapturedPiece() != EMPTY_SQUARE) {
                    capturesCounter++;
                }
                if (move.getPromotingPiece() != EMPTY_SQUARE) {
                    promotionsCounter++;
                }
                if (move.isEnPassant()) {
                    epCounter++;
                }
                if (board.isInCheck(board.getPlayerToMove())) {
                    checkCounter++;
                }
                if (move.isKingSideCastle() || move.isQueenSideCastle()) {
                    castlingCounter++;
                }
            }

            perftDetailedRecursive(board, depth - 1);
            board.executeInvertedMove(move);
        }
    }

}
