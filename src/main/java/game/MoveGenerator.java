package game;

import java.util.LinkedList;
import java.util.List;

import static game.MoveType.*;
import static game.Pieces.*;

public class MoveGenerator {

    public static final byte NORTH = 0x10, NORTH_EAST = 0x11, EAST = 0x01, SOUTH_EAST = 0x01 - 0x10, SOUTH = -0x10, SOUTH_WEST = -0x11, WEST = -0x01, NORTH_WEST = 0x10 - 0x01;

    private static final byte[] ROOK_DIRECTIONS = {NORTH, EAST, SOUTH, WEST};
    private static final byte[] BISHOP_DIRECTIONS = {NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST};
    protected static final byte[] QUEEN_DIRECTIONS = {NORTH, EAST, SOUTH, WEST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST};
    private static final byte[] KING_DIRECTIONS = QUEEN_DIRECTIONS;

    private static final byte[] PROMO_PIECES_WHITE = {WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT};
    private static final byte[] PROMO_PIECES_BLACK = {BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT};

    private static final byte[] KNIGHT_DIRECTIONS = {
            NORTH + NORTH_EAST, NORTH + NORTH_WEST,
            EAST + NORTH_EAST, EAST + SOUTH_EAST,
            SOUTH + SOUTH_EAST, SOUTH + SOUTH_WEST,
            WEST + SOUTH_WEST, WEST + NORTH_WEST};

    private static LinkedList<Move> moves;

    public static LinkedList<Move> generateMoves(Board board, MoveType moveType) {
        moves = new LinkedList<>();
        Player player = board.getPlayerToMove();

        byte [] squares = board.getSquares();

        for (int i = 0; i < 120; i++) {
            if (isOutOfBounds(i)) continue;
            byte square = squares[i];
            if (square == EMPTY_SQUARE) continue;
            if (player == Player.WHITE && isWhitePiece(square) ||
                    player == Player.BLACK && isBlackPiece(square)) {
                MoveGenerator.generateMovesOfPiece(squares, i, board.getCastlingRights(), board.getEnPassantIndex(), moveType);
            }

        }

        return moves;
    }

    private static void generateMovesOfPiece(byte[] squares, int index, int castlingRights, int enPassantIndex, MoveType moveType) {

        byte piece = squares[index];
        if (piece == WHITE_PAWN || piece == BLACK_PAWN) {
            generatePawnMoves(squares, index, piece, enPassantIndex, moveType);
        } else if (piece == WHITE_KNIGHT || piece == BLACK_KNIGHT) {
            generateKnightMoves(squares, index, piece, moveType);
        } else if (piece == WHITE_ROOK || piece == BLACK_ROOK) {
            generateRookMoves(squares, index, piece, moveType);
        } else if (piece == WHITE_BISHOP || piece == BLACK_BISHOP) {
            generateBishopMoves(squares, index, piece, moveType);
        } else if (piece == WHITE_QUEEN || piece == BLACK_QUEEN) {
            generateQueenMoves(squares, index, piece, moveType);
        } else if (piece == WHITE_KING || piece == BLACK_KING) {
            generateKingMoves(squares, index, piece, castlingRights, moveType);
        }
    }

    public static boolean isSquareUnderAttack(byte[] squares, int squareIndex, Player player) {
        //Check for pawn checks
        if ((player == Player.WHITE && !isOutOfBounds(squareIndex + NORTH_WEST) && squares[squareIndex + NORTH_WEST] == BLACK_PAWN) ||
                (player == Player.WHITE && !isOutOfBounds(squareIndex + NORTH_EAST) && squares[squareIndex + NORTH_EAST] == BLACK_PAWN)) {
            return true;
        } else if ((player == Player.BLACK && !isOutOfBounds(squareIndex + SOUTH_WEST) && squares[squareIndex + SOUTH_WEST] == WHITE_PAWN) ||
                (player == Player.BLACK && !isOutOfBounds(squareIndex + SOUTH_EAST) && squares[squareIndex + SOUTH_EAST] == WHITE_PAWN)) {
            return true;
        }

        //Raytrace from king position
        //Rook and queen
        for (byte direction : ROOK_DIRECTIONS) {
            int desitnationIndex = squareIndex + direction;
            while (!isOutOfBounds(desitnationIndex)) {
                byte pieceOnSquare = squares[desitnationIndex];
                if (pieceOnSquare == EMPTY_SQUARE) {
                }
                //If enemy rook or queen
                else if (player == Player.WHITE && pieceOnSquare == BLACK_ROOK ||
                        player == Player.WHITE && pieceOnSquare == BLACK_QUEEN ||
                        player == Player.BLACK && pieceOnSquare == WHITE_ROOK ||
                        player == Player.BLACK && pieceOnSquare == WHITE_QUEEN) {
                    return true;
                }

                //If blocked by piece of same color
                else {
                    break;
                }

                desitnationIndex += direction;
            }
        }

        //Bishop and queen
        for (byte direction : BISHOP_DIRECTIONS) {
            int desitnationIndex = squareIndex + direction;
            while (!isOutOfBounds(desitnationIndex)) {
                byte pieceOnSquare = squares[desitnationIndex];
                if (pieceOnSquare == EMPTY_SQUARE) {
                }
                //If enemy rook or queen
                else if (player == Player.WHITE && pieceOnSquare == BLACK_BISHOP ||
                        player == Player.WHITE && pieceOnSquare == BLACK_QUEEN ||
                        player == Player.BLACK && pieceOnSquare == WHITE_BISHOP ||
                        player == Player.BLACK && pieceOnSquare == WHITE_QUEEN) {
                    return true;
                }

                //If blocked by piece of same color
                else {
                    break;
                }

                desitnationIndex += direction;
            }
        }

        //Knight
        for (byte direction : KNIGHT_DIRECTIONS) {
            int destinationIndex = squareIndex + direction;
            if (isOutOfBounds(destinationIndex)) {
                continue;
            }

            byte destinationSquare = squares[destinationIndex];

            if (player == Player.WHITE && destinationSquare == BLACK_KNIGHT ||
                    player == Player.BLACK && destinationSquare == WHITE_KNIGHT) {
                return true;
            }
        }

        //King
        for (byte direction : KING_DIRECTIONS) {
            int destinationIndex = squareIndex + direction;
            if (isOutOfBounds(destinationIndex)) {
                continue;
            }

            byte destinationSquare = squares[destinationIndex];

            if (player == Player.WHITE && destinationSquare == BLACK_KING ||
                    player == Player.BLACK && destinationSquare == WHITE_KING) {
                return true;
            }
        }

        return false;
    }

    private static void generatePawnMoves(byte[] squares, int index, byte piece, int enPassantIndex, MoveType moveType) {

        if (moveType == CAPTURING) {
            //Capturing moves
            int capturingMoveIndex1 = piece == WHITE_PAWN ? index + NORTH_WEST : index + SOUTH_WEST;
            int capturingMoveIndex2 = piece == WHITE_PAWN ? index + NORTH_EAST : index + SOUTH_EAST;

            byte capturedPiece1 = isOutOfBounds(capturingMoveIndex1) ? EMPTY_SQUARE : squares[capturingMoveIndex1];
            byte capturedPiece2 = isOutOfBounds(capturingMoveIndex2) ? EMPTY_SQUARE : squares[capturingMoveIndex2];
            if ((piece == WHITE_PAWN && isBlackPiece(capturedPiece1)) || (piece == BLACK_PAWN && isWhitePiece(capturedPiece1))) {

                //Todo refactor
                //Promoting move if landing on last rank
                if ((piece == WHITE_PAWN && capturingMoveIndex1 >> 4 == 7) || (piece == BLACK_PAWN && capturingMoveIndex1 >> 4 == 0)) {
                    List<Move> promoMoves = generatePromoMoves(index, capturingMoveIndex1, piece, squares);
                    for (Move move : promoMoves) {
                        move.setCapturedPiece(capturedPiece1);
                        moves.add(move);
                    }
                } else {
                    Move move = new Move(piece, index, capturingMoveIndex1);
                    move.setCapturedPiece(capturedPiece1);
                    moves.add(move);
                }
            }

            if ((piece == WHITE_PAWN && isBlackPiece(capturedPiece2)) || (piece == BLACK_PAWN && isWhitePiece(capturedPiece2))) {

                //Todo refactor
                //Promoting move if landing on last rank
                if ((piece == WHITE_PAWN && capturingMoveIndex2 >> 4 == 7) || (piece == BLACK_PAWN && capturingMoveIndex2 >> 4 == 0)) {
                    List<Move> promoMoves = generatePromoMoves(index, capturingMoveIndex2, piece, squares);
                    for (Move move : promoMoves) {
                        move.setCapturedPiece(capturedPiece2);
                        moves.add(move);
                    }
                } else {
                    Move move = new Move(piece, index, capturingMoveIndex2);
                    move.setCapturedPiece(capturedPiece2);
                    moves.add(move);
                }
            }
        }
        else if (moveType == QUIET) {
            int moveToIndex = piece == WHITE_PAWN ? index + NORTH : index + SOUTH;

            //Regular and promo moves
            if (squares[moveToIndex] == EMPTY_SQUARE) {
                //Promoting move if landing on last rank
                if (piece == WHITE_PAWN && moveToIndex >> 4 == 7 ||
                        piece == BLACK_PAWN && moveToIndex >> 4 == 0) {
                    generatePromoMoves(index, moveToIndex, piece, squares).forEach(m -> moves.add(m));
                }
                //Regular
                else {
                    moves.add(new Move(piece, index, moveToIndex));
                }
            }

            //Move 2 squares if starting pos and no piece blocking
            if (piece == WHITE_PAWN && (index >> 4) == 1 && squares[index + NORTH] == EMPTY_SQUARE && squares[index + NORTH + NORTH] == EMPTY_SQUARE) {
                Move move = new Move(piece, index, index + NORTH + NORTH);
                move.setPawnDoublePush(true);
                moves.add(move);
            } else if (piece == BLACK_PAWN && (index >> 4) == 6 && squares[index + SOUTH] == EMPTY_SQUARE && squares[index + SOUTH + SOUTH] == EMPTY_SQUARE) {
                Move move = new Move(piece, index, index + SOUTH + SOUTH);
                move.setPawnDoublePush(true);
                moves.add(move);
            }

            //En passantMove
            if (isWhitePiece(piece) && (index >> 4) == 4 &&
                    ((index & 0b0111) == enPassantIndex - 1 || (index & 0b0111) == enPassantIndex + 1)) {
                Move move = new Move(piece, index, 0x50 + enPassantIndex);
                move.setEnPassant(true);
                move.setCapturedPiece(BLACK_PAWN);
                moves.add(move);
            } else if (!isWhitePiece(piece) && (index >> 4) == 3 &&
                    ((index & 0b0111) == enPassantIndex - 1 || (index & 0b0111) == enPassantIndex + 1)) {
                Move move = new Move(piece, index, 0x20 + enPassantIndex);
                move.setEnPassant(true);
                move.setCapturedPiece(WHITE_PAWN);
                moves.add(move);
            }
        }
    }

    private static List<Move> generatePromoMoves(int moveFrom, int moveTo, byte piece, byte[] squares) {
        List<Move> moves = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            Move m = new Move(piece, moveFrom, moveTo);
            if (piece == WHITE_PAWN)
                m.setPromotingPiece(PROMO_PIECES_WHITE[i]);
            else {
                m.setPromotingPiece(PROMO_PIECES_BLACK[i]);
            }
            moves.add(m);
        }

        return moves;
    }

    private static void generateKnightMoves(byte[] squares, int index, byte piece, MoveType moveType) {
        generateMovesByDirections(squares, index, piece, KNIGHT_DIRECTIONS, moveType);
    }

    //Generate moves based on input directions, used for knight and king moves
    private static void generateMovesByDirections(byte[] squares, int index, byte piece, byte[] directions, MoveType moveType) {
        for (byte direction : directions) {
            int destinationIndex = index + direction;
            if (isOutOfBounds(destinationIndex)) {
                continue;
            }

            byte destinationSquare = squares[destinationIndex];

            //Non capturing moves
            if (destinationSquare == EMPTY_SQUARE) {
                if (moveType == QUIET) {
                    moves.add(new Move(piece, index, destinationIndex));
                }
            }
            //capturing move
            else if (moveType == CAPTURING && isOppositeTeams(piece, destinationSquare)) {
                Move move = new Move(piece, index, destinationIndex);
                move.setCapturedPiece(destinationSquare);
                moves.add(move);
            }
        }
    }

    private static void generateKingMoves(byte[] squares, int index, byte piece, int castlingRights, MoveType moveType) {

        //Start by adding standard moves
        generateMovesByDirections(squares, index, piece, KING_DIRECTIONS, moveType);

        if (moveType == CAPTURING) {
            return;
        }


        //Castling
        if (isWhitePiece(piece)) {
            //Kingside white
            if ((castlingRights & 0b0001) == 1 &&
                    squares[0x07] == WHITE_ROOK &&
                    squares[0x06] == EMPTY_SQUARE &&
                    squares[0x05] == EMPTY_SQUARE &&
                    !isSquareUnderAttack(squares, 0x04, Player.WHITE) &&
                    !isSquareUnderAttack(squares, 0x05, Player.WHITE)) {
                Move move = new Move(piece, index, 0x06);
                move.setKingSideCastle(true);
                moves.add(move);
            }
            //Queenside white
            if ((castlingRights & 0b0010) == 0b10 &&
                    squares[0x00] == WHITE_ROOK &&
                    squares[0x03] == EMPTY_SQUARE &&
                    squares[0x02] == EMPTY_SQUARE &&
                    squares[0x01] == EMPTY_SQUARE &&
                    !isSquareUnderAttack(squares, 0x03, Player.WHITE) &&
                    !isSquareUnderAttack(squares, 0x04, Player.WHITE)
            ) {
                Move move = new Move(piece, index, 0x02);
                move.setQueenSideCastle(true);
                moves.add(move);
            }
        } else {
            //Kingside black
            if ((castlingRights & 0b0100) == 0b0100 &&
                    squares[0x77] == BLACK_ROOK &&
                    squares[0x76] == EMPTY_SQUARE &&
                    squares[0x75] == EMPTY_SQUARE &&
                    !isSquareUnderAttack(squares, 0x74, Player.BLACK) &&
                    !isSquareUnderAttack(squares, 0x75, Player.BLACK)) {
                Move move = new Move(piece, index, 0x76);
                move.setKingSideCastle(true);
                moves.add(move);
            }
            //Queenside black
            if ((castlingRights & 0b1000) == 0b1000 &&
                    squares[0x70] == BLACK_ROOK &&
                    squares[0x73] == EMPTY_SQUARE &&
                    squares[0x72] == EMPTY_SQUARE &&
                    squares[0x71] == EMPTY_SQUARE &&
                    !isSquareUnderAttack(squares, 0x73, Player.BLACK) &&
                    !isSquareUnderAttack(squares, 0x74, Player.BLACK)) {
                Move move = new Move(piece, index, 0x72);
                move.setQueenSideCastle(true);
                moves.add(move);
            }
        }
    }

    private static void generateRookMoves(byte[] squares, int index, byte piece, MoveType moveType) {
        generateRayPieceMoves(squares, index, piece, ROOK_DIRECTIONS, moveType);
    }

    private static void generateBishopMoves(byte[] squares, int index, byte piece, MoveType moveType) {
        generateRayPieceMoves(squares, index, piece, BISHOP_DIRECTIONS, moveType);
    }


    private static void generateQueenMoves(byte[] squares, int index, byte piece, MoveType moveType) {
        generateRayPieceMoves(squares, index, piece, QUEEN_DIRECTIONS, moveType);
    }

    //Raypiece == Rook, bishop & queen
    private static void generateRayPieceMoves(byte[] squares, int index, byte piece, byte[] directions, MoveType moveType) {

        //Traverse every direction
        for (byte direction : directions) {
            int desitnationIndex = index + direction;
            while (!isOutOfBounds(desitnationIndex)) {
                Move move = new Move(piece, index, desitnationIndex);
                byte pieceOnSquare = squares[desitnationIndex];
                if (pieceOnSquare == EMPTY_SQUARE) {
                    if (moveType == QUIET) {
                        moves.add(move);
                    }
                }
                //If capture
                else if (moveType == CAPTURING && isOppositeTeams(piece, pieceOnSquare)) {
                    move.setCapturedPiece(pieceOnSquare);
                    moves.add(move);
                    break;
                }
                //If blocked by piece of same color
                else {
                    break;
                }

                desitnationIndex += direction;
            }
        }
    }

    private static boolean isOutOfBounds(int index) {
        return (index & 0x88) != 0;
    }

    //TODO use method in Pieces.java
    private static boolean isWhitePiece(byte piece) {
        return piece != 0 && piece <= 6;
    }

    private static boolean isBlackPiece(byte piece) {
        return piece >= 7;
    }

    private static boolean isOppositeTeams(byte piece1, byte piece2) {
        return (isWhitePiece(piece1) && isBlackPiece(piece2)) || (isBlackPiece(piece1) && isWhitePiece(piece2));
    }
}
