package game0x88;

import util.Util;

import java.util.LinkedList;
import java.util.List;

import static game0x88.Pieces.*;

public class MoveGenerator {

    public static final byte NORTH = 0x10, NORTH_EAST = 0x11, EAST = 0x01, SOUTH_EAST = 0x01 - 0x10, SOUTH = -0x10, SOUTH_WEST = -0x11, WEST = -0x01, NORTH_WEST = 0x10 - 0x01;

    private static final byte[] ROOK_DIRECTIONS = {NORTH, EAST, SOUTH, WEST};
    private static final byte[] BISHOP_DIRECTIONS = {NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST};
    private static final byte[] QUEEN_DIRECTIONS = {NORTH, EAST, SOUTH, WEST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST};
    private static final byte[] KING_DIRECTIONS = QUEEN_DIRECTIONS;

    private static final byte[] PROMO_PIECES_WHITE = {WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT};
    private static final byte[] PROMO_PIECES_BLACK = {BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT};

    private static final byte[] KNIGHT_DIRECTIONS = {
            NORTH + NORTH_EAST, NORTH + NORTH_WEST,
            EAST + NORTH_EAST, EAST + SOUTH_EAST,
            SOUTH + SOUTH_EAST, SOUTH + SOUTH_WEST,
            WEST + SOUTH_WEST, WEST + NORTH_WEST};

    private static boolean SEARCH_MODE_QUIESCENCE = false;

    private static boolean includePseudolegalMoves;

    private static LinkedList<Move> moves;

    public static void setSearchModeNormal() {
        SEARCH_MODE_QUIESCENCE = false;
    }

    public static void setSearchModeQuiescence() {
        SEARCH_MODE_QUIESCENCE = true;
    }

    public static LinkedList<Move> generateMoves(byte[] squares, Player player, int castlingRights, int enPassantIndex, boolean includePseudolegal) {
        includePseudolegalMoves = includePseudolegal;
        moves = new LinkedList<>();

        for (int i = 0; i < 120; i++) {
            if (isOutOfBounds(i)) continue;
            byte square = squares[i];
            if (square == EMPTY_SQUARE) continue;
            if (player == Player.WHITE && isWhitePiece(square) ||
                    player == Player.BLACK && isBlackPiece(square)) {
                MoveGenerator.generateMovesOfPiece(squares,i,castlingRights, enPassantIndex);
            }

        }

        return moves;
    }

    //TODO use same instance of arrayList instead of creating new one for each piece
    //Maybe set initial size to
    public static void generateMovesOfPiece(byte[] squares, int index, int castlingRights, int enPassantIndex) {

        byte piece = squares[index];
        if (piece == WHITE_PAWN || piece == BLACK_PAWN) {
            generatePawnMoves(squares, index, piece, enPassantIndex);
        } else if (piece == WHITE_KNIGHT || piece == BLACK_KNIGHT) {
            generateKnightMoves(squares, index, piece);
        } else if (piece == WHITE_ROOK || piece == BLACK_ROOK) {
            generateRookMoves(squares, index, piece);
        } else if (piece == WHITE_BISHOP || piece == BLACK_BISHOP) {
            generateBishopMoves(squares, index, piece);
        } else if (piece == WHITE_QUEEN || piece == BLACK_QUEEN) {
            generateQueenMoves(squares, index, piece);
        } else if (piece == WHITE_KING || piece == BLACK_KING) {
            generateKingMoves(squares, index, piece, castlingRights);
        }
    }


    //TODO make private
    public static boolean isInCheck(byte[] squares, Player player) {
        //Find king
        //TODO: speed up by checking castling rights to see if king is in starting position
        int kingIndex = -1;
        for (int i = 0; i < 120; i++) {
            if (isOutOfBounds(i)) continue;
            byte square = squares[i];
            if (square == EMPTY_SQUARE) continue;
            if (player == Player.WHITE && square == WHITE_KING ||
                    player == Player.BLACK && square == BLACK_KING) {
                kingIndex = i;
                break;
            }
        }

        return isSquareUnderAttack(squares, kingIndex, player);
    }

    private static boolean isSquareUnderAttack(byte[] squares, int squareIndex, Player player) {
        //Check for pawn checks
        if ((player == Player.WHITE && !isOutOfBounds(squareIndex + NORTH_WEST) && squares[squareIndex + NORTH_WEST] == BLACK_PAWN) ||
                (player == Player.WHITE && !isOutOfBounds(squareIndex + NORTH_EAST) && squares[squareIndex + NORTH_EAST] == BLACK_PAWN)) {
            return true;
        } else if ((player == Player.BLACK && !isOutOfBounds(squareIndex + SOUTH_WEST) && squares[squareIndex + SOUTH_WEST] == WHITE_PAWN) ||
                (player == Player.BLACK && !isOutOfBounds(squareIndex + SOUTH_EAST)  && squares[squareIndex + SOUTH_EAST] == WHITE_PAWN)) {
            return true;
        }

        //Raytrace from king position
        //Rook and queen
        for(byte direction : ROOK_DIRECTIONS) {
            int desitnationIndex = squareIndex + direction;
            while (!isOutOfBounds(desitnationIndex)) {
                byte pieceOnSquare = squares[desitnationIndex];
                if (pieceOnSquare == EMPTY_SQUARE) {
                }
                //If enemy rook or queen
                else if (player == Player.WHITE && pieceOnSquare == BLACK_ROOK ||
                        player == Player.WHITE && pieceOnSquare == BLACK_QUEEN ||
                        player == Player.BLACK && pieceOnSquare == WHITE_ROOK ||
                        player == Player.BLACK&& pieceOnSquare == WHITE_QUEEN) {
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
        for(byte direction : BISHOP_DIRECTIONS) {
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
        for(byte direction : KNIGHT_DIRECTIONS) {
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
        for(byte direction : KING_DIRECTIONS) {
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

    private static void generatePawnMoves(byte[] squares, int index, byte piece, int enPassantIndex) {
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
                for (Move m : promoMoves) {
                    m.setCapturedPiece(capturedPiece1);
                }
                moves.addAll(promoMoves);
            } else {
                Move move = new Move(piece, index, capturingMoveIndex1);
                move.setCapturedPiece(capturedPiece1);
                addMove(move, squares);
            }
        }

        if ((piece == WHITE_PAWN && isBlackPiece(capturedPiece2)) || (piece == BLACK_PAWN && isWhitePiece(capturedPiece2))) {

            //Todo refactor
            //Promoting move if landing on last rank
            if ((piece == WHITE_PAWN && capturingMoveIndex2 >> 4 == 7) || (piece == BLACK_PAWN && capturingMoveIndex2 >> 4 == 0)) {
                List<Move> promoMoves = generatePromoMoves(index, capturingMoveIndex2, piece, squares);
                for (Move m : promoMoves) {
                    m.setCapturedPiece(capturedPiece2);
                }
                moves.addAll(promoMoves);
            } else {
                Move move = new Move(piece, index, capturingMoveIndex2);
                move.setCapturedPiece(capturedPiece2);
                addMove(move, squares);
            }
        }

        //All captures found
        if (SEARCH_MODE_QUIESCENCE) {
            return;
        }

        int moveToIndex = piece == WHITE_PAWN ? index + NORTH : index + SOUTH;

        //Regular and promo moves
        if (squares[moveToIndex] == EMPTY_SQUARE) {
            //Promoting move if landing on last rank
            if (piece == WHITE_PAWN && moveToIndex >> 4 == 7) {
                moves.addAll(generatePromoMoves(index, moveToIndex, piece, squares));
            } else if (piece == BLACK_PAWN && moveToIndex >> 4 == 0) {
                moves.addAll(generatePromoMoves(index, moveToIndex, piece,squares));
            }
            //Regular
            else {
                addMove(new Move(piece, index, moveToIndex), squares);
            }
        }

        //Move 2 squares if starting pos and no piece blocking
        if (piece == WHITE_PAWN && (index >> 4) == 1 && squares[index + NORTH] == EMPTY_SQUARE && squares[index + NORTH + NORTH] == EMPTY_SQUARE) {
            Move move = new Move(piece, index, index + NORTH + NORTH);
            move.setPawnDoublePush(true);
            addMove(move, squares);
        } else if (piece == BLACK_PAWN && (index >> 4) == 6 && squares[index + SOUTH] == EMPTY_SQUARE && squares[index + SOUTH + SOUTH] == EMPTY_SQUARE) {
            Move move = new Move(piece, index, index + SOUTH + SOUTH);
            move.setPawnDoublePush(true);
            addMove(move, squares);
        }

        //En passantMove
        if (isWhitePiece(piece) && (index>>4)== 4 &&
                ((index&0b0111) == enPassantIndex - 1 || (index&0b0111) == enPassantIndex + 1)) {
            Move move = new Move(piece, index, 0x50+enPassantIndex);
            move.setEnPassant(true);
            move.setCapturedPiece(BLACK_PAWN);
            addMove(move, squares);
        }
        else if (!isWhitePiece(piece) && (index>>4)== 3 &&
                ((index&0b0111) == enPassantIndex - 1 || (index&0b0111) == enPassantIndex + 1)) {
            Move move = new Move(piece, index, 0x20+enPassantIndex);
            move.setEnPassant(true);
            move.setCapturedPiece(WHITE_PAWN);
            addMove(move, squares);
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
            addMove(m, squares);
        }

        return moves;
    }

    private static void generateKnightMoves(byte[] squares, int index, byte piece) {
        generateMovesByDirections(squares, index, piece, KNIGHT_DIRECTIONS);
    }

    //Generate moves based on input directions, used for knight and king moves
    private static void generateMovesByDirections(byte[] squares, int index, byte piece, byte[] directions) {
        for (byte direction : directions) {
            int destinationIndex = index + direction;
            if (isOutOfBounds(destinationIndex)) {
                continue;
            }

            byte destinationSquare = squares[destinationIndex];

            //Non capturing moves
            if (destinationSquare == EMPTY_SQUARE) {
                if (!SEARCH_MODE_QUIESCENCE) {
                    addMove(new Move(piece, index, destinationIndex), squares);
                }
            }
            //capturing move
            else if (isOppositeTeams(piece, destinationSquare)) {
                Move move = new Move(piece, index, destinationIndex);
                move.setCapturedPiece(destinationSquare);
                addMove(move, squares);
            }
        }
    }

    private static void generateKingMoves(byte[] squares, int index, byte piece, int castlingRights) {

        //Start by adding standard moves
        generateMovesByDirections(squares, index, piece, KING_DIRECTIONS);

        if (SEARCH_MODE_QUIESCENCE) {
            return;
        }


        //Castling
        if (isWhitePiece(piece)) {
            //Kingside white
            if ((castlingRights&0b0001) == 1 &&
                    squares[0x07] == WHITE_ROOK &&
                    squares[0x06] == EMPTY_SQUARE &&
                    squares[0x05] == EMPTY_SQUARE) {
                Move move = new Move(piece, index, 0x06);
                move.setKingSideCastle(true);
                addMove(move, squares);
            }
            //Queenside white
            if ((castlingRights&0b0010) == 0b10 &&
                    squares[0x00] == WHITE_ROOK &&
                    squares[0x03] == EMPTY_SQUARE &&
                    squares[0x02] == EMPTY_SQUARE &&
                    squares[0x01] == EMPTY_SQUARE) {
                Move move = new Move(piece, index, 0x02);
                move.setQueenSideCastle(true);
                addMove(move, squares);
            }
        } else {
            //Kingside black
            if ((castlingRights&0b0100) == 0b0100 &&
                    squares[0x77] == BLACK_ROOK &&
                    squares[0x76] == EMPTY_SQUARE &&
                    squares[0x75] == EMPTY_SQUARE) {
                Move move = new Move(piece, index, 0x76);
                move.setKingSideCastle(true);
                addMove(move, squares);
            }
            //Queenside black
            if ((castlingRights&0b1000) == 0b1000 &&
                    squares[0x70] == BLACK_ROOK &&
                    squares[0x73] == EMPTY_SQUARE &&
                    squares[0x72] == EMPTY_SQUARE &&
                    squares[0x71] == EMPTY_SQUARE) {
                Move move = new Move(piece, index, 0x72);
                move.setQueenSideCastle(true);
                addMove(move, squares);
            }
        }
    }

    private static void generateRookMoves(byte[] squares, int index, byte piece) {
        generateRayPieceMoves(squares, index, piece, ROOK_DIRECTIONS);
    }

    private static void generateBishopMoves(byte[] squares, int index, byte piece) {
        generateRayPieceMoves(squares, index, piece, BISHOP_DIRECTIONS);
    }


    private static void generateQueenMoves(byte[] squares, int index, byte piece) {
        generateRayPieceMoves(squares, index, piece, QUEEN_DIRECTIONS);
    }

    //Raypiece == Rook, bishop & queen
    private static void generateRayPieceMoves(byte[] squares, int index, byte piece, byte[] directions) {

        //Traverse every direction
        for (byte direction : directions) {
            int desitnationIndex = index + direction;
            while (!isOutOfBounds(desitnationIndex)) {
                Move move = new Move(piece, index, desitnationIndex);
                byte pieceOnSquare = squares[desitnationIndex];
                if (pieceOnSquare == EMPTY_SQUARE) {
                    if (!SEARCH_MODE_QUIESCENCE) {
                        addMove(move, squares);
                    }
                }
                //If capture
                else if (isOppositeTeams(piece, pieceOnSquare)) {
                    move.setCapturedPiece(pieceOnSquare);
                    addMove(move, squares);
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

    private static void addMove(Move move, byte[] squares) {

        //Remove moves that causes check on self
        if (!includePseudolegalMoves) {
            Player player = isWhitePiece(move.getPiece()) ? Player.WHITE : Player.BLACK;

                byte[] copySquares = Util.copySquares(squares);
                copySquares[move.getMoveFrom()] = EMPTY_SQUARE;
                copySquares[move.getMoveTo()] = move.getPiece();

                if (move.isEnPassant()) {
                    if (move.getPiece() == WHITE_PAWN) {
                        copySquares[move.getMoveTo() + SOUTH] = EMPTY_SQUARE;
                    } else {
                        copySquares[move.getMoveTo() + NORTH] = EMPTY_SQUARE;
                    }
                }

                if (isInCheck(copySquares, player)) {
                    return;
                }

                //If castling move
                else if (move.isKingSideCastle()) {
                    if (isSquareUnderAttack(squares, move.getMoveFrom()+1, player) || isSquareUnderAttack(squares, move.getMoveFrom(), player)) {
                        return;
                    }
                } else if (move.isQueenSideCastle()) {
                    if (isSquareUnderAttack(squares, move.getMoveFrom()-1, player) || isSquareUnderAttack(squares, move.getMoveFrom(), player)) {
                        return;
                    }
                }

                moves.add(move);
        }
        else {
            moves.add(move);
        }
    }

    private static boolean isOutOfBounds(int index) {
        return (index & 0x88) != 0;
    }

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
