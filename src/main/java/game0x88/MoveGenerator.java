package game0x88;

import util.Util;

import java.util.ArrayList;
import java.util.List;

import static game0x88.Pieces.*;
import static game0x88.Pieces.EMPY_SQUARE;

public class MoveGenerator {

    public static final byte NORTH = 0x10, NORTH_EAST = 0x11, EAST = 0x01, SOUTH_EAST = 0x01 - 0x10, SOUTH = -0x10, SOUTH_WEST = -0x11, WEST = -0x01, NORTH_WEST = 0x10 - 0x01;

    private static final byte[] ROOK_DIRECTIONS = {NORTH, EAST, SOUTH, WEST};
    private static final byte[] BISHOP_DIRECTIONS = {NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST};
    private static final byte[] QUEEN_DIRECTIONS = {NORTH, EAST, SOUTH, WEST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST};
    private static final byte[] KING_DIRECTIONS = QUEEN_DIRECTIONS;

    private static final byte[] KNIGHT_DIRECTIONS = {
            NORTH + NORTH_EAST, NORTH + NORTH_WEST,
            EAST + NORTH_EAST, EAST + SOUTH_EAST,
            SOUTH + SOUTH_EAST, SOUTH + SOUTH_WEST,
            WEST + SOUTH_WEST, WEST + NORTH_WEST};

    private static boolean SEARCH_MODE_QUIESCENCE = false;

    public static void setSearchModeNormal() {
        SEARCH_MODE_QUIESCENCE = false;
    }

    public static void setSearchModeQuiescence() {
        SEARCH_MODE_QUIESCENCE = true;
    }

    public static List<Move> generateMoves(byte[] squares, Player player, int castlingRights, int enPassantIndex, boolean includePseudolegal) {
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < 120; i++) {
            if (isOutOfBounds(i)) continue;
            byte square = squares[i];
            if (square == EMPY_SQUARE) continue;
            if (player == Player.WHITE && isWhitePiece(square) ||
                    player == Player.BLACK && !isWhitePiece(square)) {
                moves.addAll(MoveGenerator.generateMovesOfPiece(squares,i,castlingRights, enPassantIndex, includePseudolegal));
            }

        }

        return moves;
    }

    //TODO use same instance of arrayList instead of creating new one for each piece
    //Maybe set initial size to
    public static List<Move> generateMovesOfPiece(byte[] squares, int index, int castlingRights, int enPassantIndex, boolean includePseudolegal) {

        byte piece = squares[index];
        List<Move> moves = null;
        if (piece == WHITE_PAWN || piece == BLACK_PAWN) {
            moves = generatePawnMoves(squares, index, piece, enPassantIndex);
        } else if (piece == WHITE_KNIGHT || piece == BLACK_KNIGHT) {
            moves = generateKnightMoves(squares, index, piece);
        } else if (piece == WHITE_ROOK || piece == BLACK_ROOK) {
            moves = generateRookMoves(squares, index, piece);
        } else if (piece == WHITE_BISHOP || piece == BLACK_BISHOP) {
            moves = generateBishopMoves(squares, index, piece);
        } else if (piece == WHITE_QUEEN || piece == BLACK_QUEEN) {
            moves = generateQueenMoves(squares, index, piece);
        } else if (piece == WHITE_KING || piece == BLACK_KING) {
            moves = generateKingMoves(squares, index, piece, castlingRights);
        }

        //Remove moves that causes check on self
        if (!includePseudolegal) {
            Player player = isWhitePiece(piece) ? Player.WHITE : Player.BLACK;

            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
                byte[] copySquares = Util.copySquares(squares);
                copySquares[move.getMoveFrom()] = EMPY_SQUARE;
                copySquares[move.getMoveTo()] = move.getPiece();
                if (isInCheck(copySquares, player)) {
                    moves.remove(i);
                    i--;
                }

                //If castling move
                else if (move.isKingSideCastle()) {
                    if (isSquareUnderAttack(squares, move.getMoveFrom()+1, player)) {
                        moves.remove(i);
                        i--;
                    }
                } else if (move.isQueenSideCastle()) {
                    if (isSquareUnderAttack(squares, move.getMoveFrom()-1, player)) {
                        moves.remove(i);
                        i--;
                    }
                }
            }
        }


        return moves;
    }


    //TODO make private
    public static boolean isInCheck(byte[] squares, Player player) {
        //Find king
        //TODO: speed up by checking castling rights to see if king is in starting position
        int kingIndex = -1;
        for (int i = 0; i < 120; i++) {
            if (isOutOfBounds(i)) continue;
            byte square = squares[i];
            if (square == EMPY_SQUARE) continue;
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
                if (pieceOnSquare == EMPY_SQUARE) {
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
                if (pieceOnSquare == EMPY_SQUARE) {
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

    private static List<Move> generatePawnMoves(byte[] squares, int index, byte piece, int enPassantIndex) {
        List<Move> moves = new ArrayList<>();

        int capturingMoveIndex1 = piece == WHITE_PAWN ? index + NORTH_WEST : index + SOUTH_WEST;
        int capturingMoveIndex2 = piece == WHITE_PAWN ? index + NORTH_EAST : index + SOUTH_EAST;

        //Capturing moves
        byte capturedPiece1 = isOutOfBounds(capturingMoveIndex1) ? EMPY_SQUARE : squares[capturingMoveIndex1];
        byte capturedPiece2 = isOutOfBounds(capturingMoveIndex2) ? EMPY_SQUARE : squares[capturingMoveIndex2];
        if ((piece == WHITE_PAWN && capturedPiece1 >= 7) || (piece == BLACK_PAWN && capturedPiece1 <= 6 && capturedPiece1 > 0)) {
            Move move = new Move(piece, index, capturingMoveIndex1);
            move.setCapturedPiece(capturedPiece1);
            //Todo refactor
            //Promoting move if landing on last rank
            if (piece == WHITE_PAWN && capturingMoveIndex1 >> 4 == 7) {
                move.setPromotingPiece(WHITE_QUEEN);
            } else if (piece == BLACK_PAWN && capturingMoveIndex1 >> 4 == 0) {
                move.setPromotingPiece(BLACK_QUEEN);
            }
            moves.add(move);
        }
        if ((piece == WHITE_PAWN && capturedPiece2 >= 7) || (piece == BLACK_PAWN && capturedPiece2 <= 6 && capturedPiece2 > 0)) {
            Move move = new Move(piece, index, capturingMoveIndex2);
            move.setCapturedPiece(capturedPiece2);
            //Todo refactor
            //Promoting move if landing on last rank
            if (piece == WHITE_PAWN && capturingMoveIndex2 >> 4 == 7) {
                move.setPromotingPiece(WHITE_QUEEN);
            } else if (piece == BLACK_PAWN && capturingMoveIndex2 >> 4 == 0) {
                move.setPromotingPiece(BLACK_QUEEN);
            }
            moves.add(move);
        }

        //All captures found
        if (SEARCH_MODE_QUIESCENCE) {
            return moves;
        }

        int moveToIndex = piece == WHITE_PAWN ? index + NORTH : index + SOUTH;

        //Regular move
        if (squares[moveToIndex] == EMPY_SQUARE) {
            Move move = new Move(piece, index, moveToIndex);

            //Promoting move if landing on last rank
            if (piece == WHITE_PAWN && moveToIndex >> 4 == 7) {
                move.setPromotingPiece(WHITE_QUEEN);
            } else if (piece == BLACK_PAWN && moveToIndex >> 4 == 0) {
                move.setPromotingPiece(BLACK_QUEEN);
            }

            moves.add(move);
        }

        //Move 2 squares if starting pos and no piece blocking
        if (piece == WHITE_PAWN && (index >> 4) == 1 && squares[index + NORTH] == EMPY_SQUARE && squares[index + NORTH + NORTH] == EMPY_SQUARE) {
            Move move = new Move(piece, index, index + NORTH + NORTH);
            move.setPawnDoublePush(true);
            moves.add(move);
        } else if (piece == BLACK_PAWN && (index >> 4) == 6 && squares[index + SOUTH] == EMPY_SQUARE && squares[index + SOUTH + SOUTH] == EMPY_SQUARE) {
            Move move = new Move(piece, index, index + SOUTH + SOUTH);
            move.setPawnDoublePush(true);
            moves.add(move);
        }

        //En passantMove
        if (isWhitePiece(piece) && (index>>4)== 4 &&
                ((index&0b0111) == enPassantIndex - 1 || (index&0b0111) == enPassantIndex + 1)) {
            Move move = new Move(piece, index, 0x50+enPassantIndex);
            move.setEnPassant(true);
            move.setCapturedPiece(BLACK_PAWN);
            moves.add(move);
        }
        else if (!isWhitePiece(piece) && (index>>4)== 3 &&
                ((index&0b0111) == enPassantIndex - 1 || (index&0b0111) == enPassantIndex + 1)) {
            Move move = new Move(piece, index, 0x20+enPassantIndex);
            move.setEnPassant(true);
            move.setCapturedPiece(WHITE_PAWN);
            moves.add(move);
        }
        return moves;
    }

    private static List<Move> generateKnightMoves(byte[] squares, int index, byte piece) {
        return generateMovesByDirections(squares, index, piece, KNIGHT_DIRECTIONS);
    }

    //Generate moves based on input directions, used for knight and king moves
    private static List<Move> generateMovesByDirections(byte[] squares, int index, byte piece, byte[] directions) {
        List<Move> moves = new ArrayList<>();

        for (byte direction : directions) {
            int destinationIndex = index + direction;
            if (isOutOfBounds(destinationIndex)) {
                continue;
            }

            byte destinationSquare = squares[destinationIndex];

            //Non capturing moves
            if (destinationSquare == EMPY_SQUARE) {
                if (!SEARCH_MODE_QUIESCENCE) {
                    moves.add(new Move(piece, index, destinationIndex));
                }
            }
            //capturing move
            else if (isOppositeTeams(piece, destinationSquare)) {
                Move move = new Move(piece, index, destinationIndex);
                move.setCapturedPiece(destinationSquare);
                moves.add(move);
            }
        }

        return moves;
    }

    private static List<Move> generateKingMoves(byte[] squares, int index, byte piece, int castlingRights) {

        //Start by adding standard moves
        List<Move> moves = generateMovesByDirections(squares, index, piece, KING_DIRECTIONS);

        if (SEARCH_MODE_QUIESCENCE) {
            return moves;
        }


        //Castling
        if (isWhitePiece(piece)) {
            //Kingside white
            if ((castlingRights&0b0001) == 1 &&
                    squares[0x07] == WHITE_ROOK &&
                    squares[0x06] == EMPY_SQUARE &&
                    squares[0x05] == EMPY_SQUARE) {
                Move m = new Move(piece, index, 0x06);
                m.setKingSideCastle(true);
                moves.add(m);
            }
            //Queenside white
            if ((castlingRights&0b0010) == 0b10 &&
                    squares[0x00] == WHITE_ROOK &&
                    squares[0x03] == EMPY_SQUARE &&
                    squares[0x02] == EMPY_SQUARE &&
                    squares[0x01] == EMPY_SQUARE) {
                Move m = new Move(piece, index, 0x02);
                m.setQueenSideCastle(true);
                moves.add(m);
            }
        } else {
            //Kingside black
            if ((castlingRights&0b0100) == 0b0100 &&
                    squares[0x77] == BLACK_ROOK &&
                    squares[0x76] == EMPY_SQUARE &&
                    squares[0x75] == EMPY_SQUARE) {
                Move m = new Move(piece, index, 0x76);
                m.setKingSideCastle(true);
                moves.add(m);
            }
            //Queenside black
            if ((castlingRights&0b1000) == 0b1000 &&
                    squares[0x70] == BLACK_ROOK &&
                    squares[0x73] == EMPY_SQUARE &&
                    squares[0x72] == EMPY_SQUARE &&
                    squares[0x71] == EMPY_SQUARE) {
                Move m = new Move(piece, index, 0x72);
                m.setQueenSideCastle(true);
                moves.add(m);
            }
        }

        return moves;
    }

    private static List<Move> generateRookMoves(byte[] squares, int index, byte piece) {
        return generateRayPieceMoves(squares, index, piece, ROOK_DIRECTIONS);
    }

    private static List<Move> generateBishopMoves(byte[] squares, int index, byte piece) {
        return generateRayPieceMoves(squares, index, piece, BISHOP_DIRECTIONS);
    }


    private static List<Move> generateQueenMoves(byte[] squares, int index, byte piece) {
        return generateRayPieceMoves(squares, index, piece, QUEEN_DIRECTIONS);
    }

    //Raypiece == Rook, bishop & queen
    private static List<Move> generateRayPieceMoves(byte[] squares, int index, byte piece, byte[] directions) {
        List<Move> moves = new ArrayList<>();

        //Traverse every direction
        for (byte direction : directions) {
            int desitnationIndex = index + direction;
            while (!isOutOfBounds(desitnationIndex)) {
                Move move = new Move(piece, index, desitnationIndex);
                byte pieceOnSquare = squares[desitnationIndex];
                if (pieceOnSquare == EMPY_SQUARE) {
                    if (!SEARCH_MODE_QUIESCENCE) {
                        moves.add(move);
                    }
                }
                //If capture
                else if (isOppositeTeams(piece, pieceOnSquare)) {
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


        return moves;
    }

    private static boolean isOutOfBounds(int index) {
        return (index & 0x88) != 0;
    }

    private static boolean isWhitePiece(byte piece) {
        return !(piece >= 7);
    }

    private static boolean isOppositeTeams(byte piece1, byte piece2) {
        return (isWhitePiece(piece1) && !isWhitePiece(piece2)) || (!isWhitePiece(piece1) && isWhitePiece(piece2));
    }
}
