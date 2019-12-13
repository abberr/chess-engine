package game;

import java.util.LinkedList;

import static game.Pieces.EMPTY_SQUARE;
import static game.Pieces.PIECES_SIZE;

public class Evaluator {

    private static final int MAX_PLY = 4096;

    private static final int SEARCH_DEPTH_DEFAULT = 6;
    private static final int MATE_SCORE = Integer.MAX_VALUE - 1;
    private final static int DRAW_SCORE = 0;

    private static final int KILLER_MOVES_TO_STORE = 2;

    private static final int NULL_MOVE_DEPTH_REDUCE = 3;

    private static long sortingTime;
    private static long moveGenTime;
    private static long evalTime;
    private static long quiscenceTime;

    private static int searchDepth = SEARCH_DEPTH_DEFAULT;
    private static long endTime;

    private static boolean useHash;
    private static long moveCounter;
    private static long cacheHitCounter;

    private static TranspositionTable transpositionTable = new TranspositionTable();

    private static Move[][] killerMoves = new Move[MAX_PLY][KILLER_MOVES_TO_STORE];
    private static int[][][] historyMoves = new int[2][PIECES_SIZE][128];

    public static Move findBestMove(Board board, long timeToSearch) {
        endTime = System.currentTimeMillis() + timeToSearch - 50;
        searchDepth = Integer.MAX_VALUE;
        return findBestMove(board);
    }

    public static Move findBestMove(Board board, int depth) {
        searchDepth = depth;
        endTime = Long.MAX_VALUE;
        return findBestMove(board);
    }

    public static Move findBestMove(Board board) {

//        transpositionTable.clear();

        useHash = true;

        int depth = 1;
        int value = 0;
        Move pvMove = null;

        while (System.currentTimeMillis() < endTime && depth <= searchDepth) {
            resetCounters();
            long time = System.currentTimeMillis();
            value = minMax(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, depth, board, false);
//            value *= board.getPlayerToMove().getValue();
            long searchTime = System.currentTimeMillis() - time;

            if (System.currentTimeMillis() > endTime)
                break;

            pvMove = transpositionTable.lookup(board.getHash()).bestMove;

//            System.out.println("info currmove " + pvMove);
            StringBuilder sb = new StringBuilder();
            sb.append("info depth " + depth);
            sb.append(" time " + searchTime);
            sb.append(" nodes " + moveCounter);
            sb.append(" cacheHit " + cacheHitCounter);
            sb.append(" quiscenceTime " + quiscenceTime);
            sb.append(" movegenTime " + moveGenTime);
            sb.append(" sortingTime " + sortingTime);
            sb.append(" evalTime " + evalTime);
            sb.append(" pv ");
            getPvMoves(board, depth).forEach(m -> sb.append(m + " "));
            sb.append(" score cp " + value);
            System.out.println(sb);

            //Stop searching if check mate found
            if (value == Integer.MAX_VALUE - 1) {
                break;
            }

            depth++;
        }

        return pvMove;
    }

//    private static void findMate(Board board, int depth) {
//        depth = depth - 2;
//        Move shortestMateMove = bestMove;
//        useHash = false;
//        while (depth > 0 ) {
//            int value = minMax(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, depth, board, board.getPlayerToMove(), null);
//            if (value != Integer.MAX_VALUE - 1 ) {
//                break;
//            }
//            shortestMateMove = bestMove;
//            depth -= 2;
//        }
//        useHash = true;
//        bestMove = shortestMateMove;
//    }

    private static int minMax(int alpha, int beta, int depth, Board board, boolean makeNullMove) {

        if (depth != 0) {
            //Cache lookup - has this position been evaluated before?
            State lookUpState = transpositionTable.lookup(board.getHash());
            if (useHash && lookUpState != null && lookUpState.depth >= depth) {
                cacheHitCounter++;

                if (lookUpState.nodeType == NodeType.EXACT) {
                    return lookUpState.score;
                } else if (lookUpState.nodeType == NodeType.ALPHA) {
                    if (lookUpState.score <= alpha) {
                        return alpha;
                    } else {
        //                    alpha = lookUpState.score;
                    }
                } else if (lookUpState.nodeType == NodeType.BETA) {
                    if (lookUpState.score >= beta) {
                        return beta;
                    } else {
        //                    beta = lookUpState.score;
                    }
                }
            }

//            if (alpha >= beta) {
//                return lookUpState.score;
//            }
        }

        moveCounter++;

        // Quiscence search when reaching a leaf node
        if (depth <= 0) {

            MoveGenerator.setSearchModeQuiescence();
            long time = System.currentTimeMillis();
//            int value = board.getValue() * player.getValue();
            int value = quisence(alpha, beta, board);
            quiscenceTime += System.currentTimeMillis() - time;

            MoveGenerator.setSearchModeNormal();

            //Dont need to save state since we wont lookup at depth 0 anyways
//            transpositionTable.saveState(board.getHash(), depth, value, null, NodeType.EXACT);
            return value;
        }

        //Null move
        if (makeNullMove && depth >= NULL_MOVE_DEPTH_REDUCE && !MoveGenerator.isInCheck(board.getSquares(), board.getPlayerToMove())) {
            board.executeNullMove();
            int value = -minMax(-beta, -beta + 1, depth - NULL_MOVE_DEPTH_REDUCE, board,false);
            board.executeInvertedNullMove();

            if (value >= beta) {
                return beta;
            }
        }


        //Move generation
        long time = System.currentTimeMillis();
        MoveList moves = board.getAvailableMoves(false);
        moveGenTime += System.currentTimeMillis() - time;

        //Mate or stalemate if no moves
        if (moves.isEmpty()) {
            //Min value if in check (check mate)
            int value = MoveGenerator.isInCheck(board.getSquares(), board.getPlayerToMove()) ? Integer.MIN_VALUE + 2 : 0;
            //set depth to +infinity since it's a terminal node anyways
            transpositionTable.saveState(board.getHash(), Integer.MAX_VALUE, value, null, NodeType.EXACT);
            return value;
        }

        NodeType nodeType = NodeType.ALPHA;
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        moves.prepare(board, transpositionTable, killerMoves, historyMoves);

        //Find best move
        while (!moves.isEmpty()) {
            time = System.currentTimeMillis();
            Move move = moves.getNextMove();
            sortingTime += System.currentTimeMillis() - time;


            board.executeMove(move);
            if (board.isRepetition()) {
                bestValue = DRAW_SCORE;
            } else {
                bestValue = Math.max(bestValue, -minMax(-beta, -alpha, depth - 1, board, true));
                if (System.currentTimeMillis() > endTime) {
                    board.executeInvertedMove(move);
                    return bestValue;
                }
            }
            board.executeInvertedMove(move);

            if (bestValue > alpha) {
                alpha = bestValue;
                nodeType = NodeType.EXACT;
                bestMove = move;

                if (alpha >= beta) {
                    if (move.getCapturedPiece() == EMPTY_SQUARE) {
                        storeKillerMove(move, board.getMoveNumber());
                        historyMoves[board.getPlayerToMove().getHashValue()][move.getPiece()][move.getMoveTo()] += depth*depth;
                    }
                    transpositionTable.saveState(board.getHash(), depth, beta, move, NodeType.BETA);
                    return beta;
                }
            }
        }

        transpositionTable.saveState(board.getHash(), depth, bestValue, bestMove, nodeType);
        return bestValue;
    }

    //TODO optimize
    private static void storeKillerMove(Move move, int ply) {
        //check if move already is killer move
        for (int i = 0; i < KILLER_MOVES_TO_STORE; i++) {
            if (move.equals(killerMoves[ply][i])) {
                return;
            }
        }
        //Shift right
        for (int i = KILLER_MOVES_TO_STORE - 2; i >= 0; i--) {
            killerMoves[ply][i + 1] = killerMoves[ply][i];
        }
        killerMoves[ply][0] = move;
    }

    public static void reset() {
        killerMoves = new Move[MAX_PLY][KILLER_MOVES_TO_STORE];
        historyMoves = new int[2][PIECES_SIZE][128];
        transpositionTable = new TranspositionTable();
    }

    private static void resetCounters() {
        moveCounter = 0;
        sortingTime = 0;
        moveGenTime = 0;
        evalTime = 0;
        cacheHitCounter = 0;
        quiscenceTime = 0;
    }

    //https://www.chessprogramming.org/Quiescence_Search
    //Find a quiet position to evaluate
    private static int quisence(int alpha, int beta, Board board) {
        long time = System.currentTimeMillis();
        int boardValue = board.getValue() * board.getPlayerToMove().getValue();
        evalTime += System.currentTimeMillis() - time;

        if (boardValue >= beta) {
            return beta;
        }
        if (alpha < boardValue) {
            alpha = boardValue;
        }

        time = System.currentTimeMillis();
        MoveList moves = board.getAvailableMoves(false);
        moveGenTime += System.currentTimeMillis() - time;

        moves.prepare(board);
        for (Move m : moves) {
            board.executeMove(m);
            int score = -quisence(-beta, -alpha, board);
            board.executeInvertedMove(m);

            if (score >= beta)
                return beta;
            if (score > alpha)
                alpha = score;
        }

        return alpha;
    }

    //Recursive
    public static LinkedList<Move> getPvMoves(Board board, int depth) {
        State state = transpositionTable.lookup(board.getHash());
        if (state == null || state.bestMove == null || depth == 0) {
            if (state != null && depth != 0) {
                System.out.println(state.score + " " + state.nodeType + " " + state.depth + " " + state.bestMove);
            }
            return new LinkedList<>();
        }
        Move bestMove = state.bestMove;
        board.executeMove(bestMove);
        LinkedList<Move> pvMoves = getPvMoves(board, depth - 1);
        board.executeInvertedMove(bestMove);
        pvMoves.addFirst(bestMove);

        return pvMoves;
    }}
