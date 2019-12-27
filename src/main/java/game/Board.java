package game;

import util.Util;

import java.util.Random;

import static game.Pieces.*;

public class Board {

    private final static int BOARD_SIZE = 128;
    private final static int PIECES_SIZE = 12;

    private final static int MAXIMUM_NUMBER_OF_MOVES = 4096;

    private final static int NO_EN_PASSANT_AVAILABLE = 15;

    private Player playerToMove;
    private byte[] squares = new byte[BOARD_SIZE];

    private long [][] zobristTable = new long[BOARD_SIZE][PIECES_SIZE];
    private long zobristTurn;
    private long [] zobristCastlingRights = new long[16];
    private long [] zobristEnPassant = new long[8];

    private int moveNumber = 0;

    //Store all theses 3 in one array instead (less than 32 bits needed)
    private int[] castlingRightsHistory = new int[MAXIMUM_NUMBER_OF_MOVES];     //MASK: qkQK
    private int[] enPassantHistory = new int[MAXIMUM_NUMBER_OF_MOVES];
    private int[] fiftyMoveHistory = new int[MAXIMUM_NUMBER_OF_MOVES];

    private long[] hashHistory = new long[MAXIMUM_NUMBER_OF_MOVES];

    private int wKingIndex, bKingIndex;

    Move lastMove;

    private long hash, pawnHash;

    public Board() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w qkQK -");
    }

    public Board(String fen) {

        String [] fields = fen.split(" ");

        initZobrist();

        int x = 0, y = 0;
        for(char c : fields[0].toCharArray()) {
            byte piece = EMPTY_SQUARE;
            if (c == 'P')  piece = WHITE_PAWN;
            else if (c == 'p') piece = BLACK_PAWN;
            else if (c == 'N') piece = WHITE_KNIGHT;
            else if (c == 'n') piece = BLACK_KNIGHT;
            else if (c == 'B') piece = WHITE_BISHOP;
            else if (c == 'b') piece = BLACK_BISHOP;
            else if (c == 'R') piece = WHITE_ROOK;
            else if (c == 'r') piece = BLACK_ROOK;
            else if (c == 'Q') piece = WHITE_QUEEN;
            else if (c == 'q') piece = BLACK_QUEEN;
            else if (c == 'K') piece = WHITE_KING;
            else if (c == 'k') piece = BLACK_KING;
            else if (c >= '0' && c <= '8') x += (c - '0') - 1;
            else if (c == '/') {
                y++;
                x = 0;
                continue;
            }
            squares[x + (7*16 - y*16)] = piece;
            if (piece == WHITE_KING) wKingIndex = x + (7*16 - y*16);
            if (piece == BLACK_KING) bKingIndex = x + (7*16 - y*16);

            x++;
        }

        playerToMove = fields[1].charAt(0) == 'b' ? Player.BLACK : Player.WHITE;

        castlingRightsHistory[moveNumber] = 0b0000;
        if (fields[2].charAt(0) != '-') {
            if (fields[2].contains("q")) castlingRightsHistory[moveNumber] |= 0b1000;
            if (fields[2].contains("k")) castlingRightsHistory[moveNumber] |= 0b0100;
            if (fields[2].contains("Q")) castlingRightsHistory[moveNumber] |= 0b0010;
            if (fields[2].contains("K")) castlingRightsHistory[moveNumber] |= 0b0001;
        }

        if (fields[3].charAt(0) != '-') {
            enPassantHistory[moveNumber] = (fields[3].charAt(0) - 'a');
        } else {
            enPassantHistory[moveNumber] = NO_EN_PASSANT_AVAILABLE;
        }

        //TODO fix this. Doesnt have half move clock history
//        if (fields.length > 4) {
//            fiftyMoveHistory[moveNumber] = Integer.parseInt(fields[4]);
//        }

        generateZobristHash();
        hashHistory[moveNumber] = hash;
    }

    public Player getPlayerToMove() {
        return playerToMove;
    }

    public long getHash() {
        return hash;
    }

    public long getPawnHash() {
        return pawnHash;
    }

    public boolean executeMove(String move){
        String moveFrom = move.substring(0,2);
        int moveToIndex = Util.algebraicNotationToIndex(move.substring(2,4));

        for(Move m : getMovesOfPiece(moveFrom, false)) {
            if (moveToIndex == m.getMoveTo()) {
                executeMove(m);
                return true;
            }
        }
        return false;
    }

    public void executeMove(Move move) {
        int oldCastlingRights = castlingRightsHistory[moveNumber];

        moveNumber++;

        squares[move.getMoveFrom()] = EMPTY_SQUARE;
        squares[move.getMoveTo()] = move.getPiece();

        int newCastlingRights = updateCastlingRights(move.getMoveFrom(), move.getMoveTo(), oldCastlingRights);

        if (move.getPromotingPiece() != EMPTY_SQUARE) {
            squares[move.getMoveTo()] = move.getPromotingPiece();
        }
        else if (move.isKingSideCastle()) {
            if (move.getPiece() == WHITE_KING) {
                squares[0x07] = EMPTY_SQUARE;
                squares[0x05] = WHITE_ROOK;
            } else {
                squares[0x77] = EMPTY_SQUARE;
                squares[0x75] = BLACK_ROOK;
            }
        } else if (move.isQueenSideCastle()) {
            if (move.getPiece() == WHITE_KING) {
                squares[0x00] = EMPTY_SQUARE;
                squares[0x03] = WHITE_ROOK;
            } else {
                squares[0x70] = EMPTY_SQUARE;
                squares[0x73] = BLACK_ROOK;
            }
        }

        if (move.isEnPassant()) {
            if (move.getPiece() == WHITE_PAWN) {
                squares[move.getMoveTo() + MoveGenerator.SOUTH] = EMPTY_SQUARE;
            } else {
                squares[move.getMoveTo() + MoveGenerator.NORTH] = EMPTY_SQUARE;
            }
        }

        enPassantHistory[moveNumber] = move.isPawnDoublePush() ? move.getMoveFrom()&0b0111 : NO_EN_PASSANT_AVAILABLE;

        castlingRightsHistory[moveNumber] = newCastlingRights;

        int oldAvailableEnPassant = enPassantHistory[moveNumber - 1];
        int newAvailableEnPassant = enPassantHistory[moveNumber];

        updateHash(move, oldCastlingRights, newCastlingRights, oldAvailableEnPassant, newAvailableEnPassant);
        hashHistory[moveNumber] = hash;

        if (move.isReversibleMove()) {
            fiftyMoveHistory[moveNumber] = fiftyMoveHistory[moveNumber - 1] + 1;
        } else {
            fiftyMoveHistory[moveNumber] = 0;
        }

        if (move.getPiece() == WHITE_KING) wKingIndex = move.getMoveTo();
        if (move.getPiece() == BLACK_KING) bKingIndex = move.getMoveTo();

        playerToMove = playerToMove.getOpponent();
        lastMove = move;
    }

    private int updateCastlingRights(int moveFrom, int moveTo, int newCastlingRights) {
        //if move involved rook or king startPos; update castling rights
        //TODO make constants
        if (moveFrom == 0x00 || moveTo == 0x00) {
            newCastlingRights &= 0b1101;
        } if (moveFrom == 0x07 || moveTo == 0x07) {
            newCastlingRights &= 0b1110;
        } if (moveFrom == 0x70 || moveTo == 0x70) {
            newCastlingRights &= 0b0111;
        } if (moveFrom == 0x77 || moveTo == 0x77) {
            newCastlingRights &= 0b1011;
        } if (moveFrom == 0x04 || moveTo == 0x04) {
            newCastlingRights &= 0b1100;
        } if (moveFrom == 0x74 || moveTo == 0x74) {
            newCastlingRights &= 0b0011;
        }
        
        return newCastlingRights;
    }

    //TODO make sure it can only be called on last move
    public void executeInvertedMove(Move move) {

        squares[move.getMoveFrom()] = move.getPiece();
        if (move.isEnPassant()) {
            int capturedPieceDirection = move.getPiece() == WHITE_PAWN ? MoveGenerator.SOUTH : MoveGenerator.NORTH;
            squares[move.getMoveTo() + capturedPieceDirection] = move.getCapturedPiece();
            squares[move.getMoveTo()] = EMPTY_SQUARE;
        } else {
            squares[move.getMoveTo()] = move.getCapturedPiece();
        }


        //Revert castling
        //TODO simplify
        if (move.isKingSideCastle()) {
            if (move.getPiece() == WHITE_KING) {
                squares[0x07] = WHITE_ROOK;
                squares[0x05] = EMPTY_SQUARE;
            } else {
                squares[0x77] = BLACK_ROOK;
                squares[0x75] = EMPTY_SQUARE;
            }
        } else if (move.isQueenSideCastle()) {
            if (move.getPiece() == WHITE_KING) {
                squares[0x00] = WHITE_ROOK;
                squares[0x03] = EMPTY_SQUARE;
            } else {
                squares[0x70] = BLACK_ROOK;
                squares[0x73] = EMPTY_SQUARE;
            }
        }

        playerToMove = playerToMove.getOpponent();

        int oldCastlingRights = castlingRightsHistory[moveNumber - 1];
        int newCastlingRights = castlingRightsHistory[moveNumber];
        int oldAvailableEnPassant = enPassantHistory[moveNumber - 1];
        int newAvailableEnPassant = enPassantHistory[moveNumber];

        updateHash(move, oldCastlingRights, newCastlingRights, oldAvailableEnPassant, newAvailableEnPassant);   //Has to be after player change to work

//        fiftyMoveHistory[moveNumber] = 0;

        if (move.getPiece() == WHITE_KING) wKingIndex = move.getMoveFrom();
        if (move.getPiece() == BLACK_KING) bKingIndex = move.getMoveFrom();

        moveNumber--;
    }

    public void executeNullMove() {
        int oldAvailableEnPassant = enPassantHistory[moveNumber];
        moveNumber++;
        enPassantHistory[moveNumber] = NO_EN_PASSANT_AVAILABLE;

        //Copy history
        castlingRightsHistory[moveNumber] = castlingRightsHistory[moveNumber - 1];
        fiftyMoveHistory[moveNumber] = fiftyMoveHistory[moveNumber - 1];

        //Update turn hash
        hash ^= zobristTurn;

        //Update en passant hash
        if (oldAvailableEnPassant != NO_EN_PASSANT_AVAILABLE) hash ^= zobristEnPassant[oldAvailableEnPassant];

        hashHistory[moveNumber] = hash;
        playerToMove = playerToMove.getOpponent();
    }

    public void executeInvertedNullMove() {
        moveNumber--;
        int oldAvailableEnPassant = enPassantHistory[moveNumber];

        //Update turn hash
        hash ^= zobristTurn;

        //Update en passant hash
        if (oldAvailableEnPassant != NO_EN_PASSANT_AVAILABLE) hash ^= zobristEnPassant[oldAvailableEnPassant];

        playerToMove = playerToMove.getOpponent();
    }

    public void revertLastMove() {
        executeInvertedMove(lastMove);
    }
    
    

    public MoveList getMovesOfPiece(String position, boolean includePseudoLegal) {
        int index = Util.algebraicNotationToIndex(position);

        if (squares[index] == EMPTY_SQUARE) {
            return null;
        }

        //Generate all moves and pick the right ones
        //TODO cleaner solution
        MoveList allMoves = MoveGenerator.generateMoves(this, playerToMove);
        allMoves.addAll(MoveGenerator.generateMoves(this, playerToMove.getOpponent()));

        MoveList desiredMoves = new MoveList();
        desiredMoves.prepare(this);


        for (Move move : allMoves) {
            if (move.toString().startsWith(position)) {
                desiredMoves.add(move, MoveType.QUIET);
            }
        }


        return desiredMoves;
    }

    public MoveList getAvailableMoves(boolean includePseudoLegal) {
        return MoveGenerator.generateMoves(this, playerToMove);
    }

    public int getValue() {
        return StaticEvaluator.getValue(squares);
    }

    private void updateHash(Move move, int oldCastlingRights, int newCastlingRights, int oldAvailableEnPassant, int newAvailableEnPassant) {
        int moveFromindex = move.getMoveFrom();
        int moveToindex = move.getMoveTo();

        hash ^= zobristTable[moveFromindex][move.getPiece() - 1];             //Remove piece from origin
        if (move.getPiece() == WHITE_PAWN || move.getPiece() == BLACK_PAWN) {
            pawnHash ^= zobristTable[moveFromindex][move.getPiece() - 1];
        }

        //If promoting move
        if (move.getPromotingPiece() != EMPTY_SQUARE) {
            hash ^= zobristTable[moveToindex][move.getPromotingPiece() - 1];  //Add promoting piece to new square
        }
        //If castling move
        else if (move.isKingSideCastle() || move.isQueenSideCastle()) {
            int rookFromX = move.isKingSideCastle() ? 7 : 0;
            int rookToX = move.isKingSideCastle() ? 5 : 3;
            int rookY = move.getMoveFrom() >> 4;

            int rookMoveFromindex = rookY*16 + rookFromX;
            int rookMoveToIndex = rookY*16 + rookToX;

            byte rook = playerToMove == Player.WHITE ? WHITE_ROOK : BLACK_ROOK;

            hash ^= zobristTable[rookMoveFromindex][rook - 1];           //Remove rook from corner
            hash ^= zobristTable[rookMoveToIndex][rook - 1];             //Add rook to new location
        }
        //If regular move
        else {
            hash ^= zobristTable[moveToindex][move.getPiece() - 1];           //Add origin piece to new square
            if (move.getPiece() == WHITE_PAWN || move.getPiece() == BLACK_PAWN) {
                pawnHash ^= zobristTable[moveToindex][move.getPiece() - 1];
            }
        }

        //If capturing move
        if (move.getCapturedPiece() != EMPTY_SQUARE) {
            int moveToIndexTemp = moveToindex;
            //If en passant
            if (move.isEnPassant()) {
                if (move.getPiece() == WHITE_PAWN) {
                    moveToIndexTemp = move.getMoveTo() + MoveGenerator.SOUTH;
                } else {
                    moveToIndexTemp = move.getMoveTo() + MoveGenerator.NORTH;
                }
            }

            hash ^= zobristTable[moveToIndexTemp][move.getCapturedPiece() - 1];   //Remove captured piece from new square
            if (move.getCapturedPiece() == WHITE_PAWN || move.getCapturedPiece() == BLACK_PAWN) {
                pawnHash ^= zobristTable[moveToIndexTemp][move.getCapturedPiece() - 1];
            }
        }

        //Update castlingRights hash
        hash ^= zobristCastlingRights[newCastlingRights ^ oldCastlingRights];

        //Update en passant hash
        if (oldAvailableEnPassant != NO_EN_PASSANT_AVAILABLE) hash ^= zobristEnPassant[oldAvailableEnPassant];
        if (newAvailableEnPassant != NO_EN_PASSANT_AVAILABLE) hash ^= zobristEnPassant[newAvailableEnPassant];

        //Update turn hash
        hash ^= zobristTurn;

    }


    private void initZobrist() {
        //Init zobrist
        Random rnd = new Random(1);
        for (int i = 0; i < zobristTable.length; i++) {
            for (int j = 0; j < zobristTable[0].length; j++) {
                zobristTable[i][j] = rnd.nextLong();
            }
        }

        for (int i = 0; i < zobristCastlingRights.length; i++) {
            zobristCastlingRights[i] = rnd.nextLong();
        }

        for (int i = 0; i < zobristEnPassant.length; i++) {
            zobristEnPassant[i] = rnd.nextLong();
        }

        zobristTurn = rnd.nextLong();
    }

    private void generateZobristHash() {
        //Pieces
        hash = 0;
        pawnHash = 0;
        for (int i = 0; i < 128; i++) {
                byte piece = squares[i];
                if (piece != 0) {
                    hash ^= zobristTable[i][piece - 1];
                    if (piece == WHITE_PAWN || piece == BLACK_PAWN) {
                        pawnHash ^= zobristTable[i][piece - 1];
                    }
                }
        }

        //Castling rights
        int castlingRights = castlingRightsHistory[moveNumber];
        hash ^= zobristCastlingRights[castlingRights];

        //Available en passant
        int enPassantSquare = enPassantHistory[moveNumber];
        if (enPassantSquare != NO_EN_PASSANT_AVAILABLE) {
            hash ^= zobristEnPassant[enPassantSquare];
        }
    }

    public boolean isInCheck() {
        return MoveGenerator.isInCheck(squares, playerToMove, wKingIndex, bKingIndex);
    }

    public int boardValueAfterMove(Move move) {
        executeMove(move);
        int value = getValue();
        executeInvertedMove(move);

        return value;
    }

    public byte[] getSquares() {
        return squares;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public int getHalfMoveClock() {
        return fiftyMoveHistory[moveNumber];
    }

    public int getEnPassantIndex() {
        return enPassantHistory[moveNumber];
    }

    public int getCastlingRights() {
        return castlingRightsHistory[moveNumber];
    }

    public int getWKingIndex() {
        return this.wKingIndex;
    }

    public int getBKingIndex() {
        return this.bKingIndex;
    }

    public boolean isRepetition() {
        int halfMoveClock = getHalfMoveClock();
        for (int i = moveNumber - halfMoveClock; i < moveNumber; i++) {
            if (hashHistory[i] == hash) {
                return true;
            }
        }
        return false;
    }

    public void printBoard() {
        System.out.print("\n--------------------------");
        for (int i = 0; i < 8; i++) {
            System.out.println();
            for (int j = 0; j < 8; j++) {
                byte p = squares[j + (7*16 - i*16)];
                System.out.print("[");
                if (p == EMPTY_SQUARE)
                    System.out.print(" ");
                else {
                    System.out.print(PIECE_CHAR[p]);
                }
                System.out.print("]");
            }
        }
        System.out.println("\nhash: [" + hash + "]");
        System.out.println("fen: [" + generateFen() + "]");
        System.out.println("value: [" + getValue() + "]");
        System.out.println("InCheck: " + MoveGenerator.isInCheck(squares, playerToMove, wKingIndex, bKingIndex));

        System.out.println("--------------------------");
    }

    public String getCastlingRightsString() {
        String castlingRights = "";
        int currentCastlingRights = castlingRightsHistory[moveNumber];
        if (currentCastlingRights != 0b0000) {
            if ((currentCastlingRights&0b1000) != 0) castlingRights += "q";
            if ((currentCastlingRights&0b0100) != 0) castlingRights += "k";
            if ((currentCastlingRights&0b0010) != 0) castlingRights += "Q";
            if ((currentCastlingRights&0b0001) != 0) castlingRights += "K";
        } else {
            castlingRights += "-";
        }

        return castlingRights;
    }

    private String generateFen() {
        String fen = "";
        for (int i = 0; i < 8; i++) {
            int emptySpaces = 0;
            for (int j = 0; j < 8; j++) {
                int index = j + (7*16 - i*16);
                if (squares[index] == EMPTY_SQUARE) {
                    emptySpaces++;
                } else {
                    if(emptySpaces > 0) {
                        fen += emptySpaces;
                    }
                    fen += PIECE_CHAR[squares[index]];
                    emptySpaces = 0;
                }
            }
            if(emptySpaces > 0) {
                fen += emptySpaces;
            }
            fen += "/";
        }

        fen = fen.substring(0,fen.length() - 1);

        fen = playerToMove == Player.WHITE ? fen + " w " : fen + " b ";

        fen += getCastlingRightsString() + " ";

        int currentEnPassantPossibility = enPassantHistory[moveNumber];
        if (currentEnPassantPossibility != NO_EN_PASSANT_AVAILABLE) {
            fen += (char)('a' + currentEnPassantPossibility);
            if (playerToMove == Player.WHITE) {
                fen += 6;
            } else {
                fen += 3;
            }
        } else {
            fen += "-";
        }

        //Todo half move counter
        fen += " " + fiftyMoveHistory[moveNumber];
        fen += " 0 ";

        return fen;
    }
}
