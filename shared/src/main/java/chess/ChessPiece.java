package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public record ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING(0),
        QUEEN(1),
        BISHOP(2),
        KNIGHT(3),
        ROOK(4),
        PAWN(5);

        private final int pieceID;

        PieceType(int pieceID) {
            this.pieceID = pieceID;
        }

        public int getPieceID() {
            return pieceID;
        }
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceType myPiece = board.getPiece(myPosition).getPieceType();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int[][] setOfMoves;
        switch(myPiece){
            case ROOK:
                setOfMoves = new int[][] {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
                possibleMoves.addAll(checkEachMove(board, myPosition, setOfMoves, true));
            case BISHOP:
                setOfMoves = new int[][] {{1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
                possibleMoves.addAll(checkEachMove(board, myPosition, setOfMoves, true));
            case QUEEN:
                setOfMoves = new int[][] {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
                possibleMoves.addAll(checkEachMove(board, myPosition, setOfMoves, true));
            case KNIGHT:
                setOfMoves = new int[][] {{1, 2}, {-1, 2}, {1, -2}, {-1, -2}, {2, 1}, {-2, 1}, {2, -1}, {-2, -1}};
                possibleMoves.addAll(checkEachMove(board, myPosition, setOfMoves, false));
            case KING:
                setOfMoves = new int[][] {{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
                possibleMoves.addAll(checkEachMove(board, myPosition, setOfMoves, false));
            case PAWN:
                possibleMoves.addAll(checkPawnMoves(board, myPosition));
        }
        return possibleMoves;
    }

    private Collection<ChessMove> checkEachMove(ChessBoard board, ChessPosition myPosition, int[][] setOfMoves, boolean recursive) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessPosition nextPosition;
        for (int[] move : setOfMoves) {
            nextPosition = myPosition.getNextPosition(move[0], move[1]);
            if (nextPosition.getRow() != -1) {
                ChessPiece pieceAtNextSpace = board.getPiece(nextPosition);
                if(pieceAtNextSpace == null){
                    possibleMoves.add(new ChessMove(myPosition, nextPosition, null));
                    if (recursive) {
                        possibleMoves.addAll(checkEachMove(board, nextPosition, move, true));
                    }
                }else {
                    if(board.getPiece(myPosition).pieceColor != pieceAtNextSpace.pieceColor){
                        possibleMoves.add(new ChessMove(myPosition, nextPosition, null));
                    }
                }
            }
        }
        return possibleMoves;
    }

    private Collection<ChessMove> checkEachMove(ChessBoard board, ChessPosition myPosition, int[] move, boolean recursive) {
        int[][] setOfMoves = {move};
        return checkEachMove(board, myPosition, setOfMoves, recursive);
    }

    private Collection<ChessMove> checkPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        int direction;
        if (myPiece.pieceColor() == ChessGame.TeamColor.WHITE){
            direction = 1;
        }
        else{ // TeamColor.Black
            direction = -1;
        }
        final int[] setOfColumns = {-1, 0, 1};

        ChessPosition nextPosition = myPosition.copy();
        ChessPiece pieceAtNextSpace;
        boolean isPromoted;
        for (int columnMove : setOfColumns) {
            nextPosition = nextPosition.getNextPosition(direction, columnMove);
            if (nextPosition.getRow() != -1){
                pieceAtNextSpace = board.getPiece(nextPosition);
                isPromoted = (direction == 1 && nextPosition.getRow() == 8) ||
                        (direction == -1 && nextPosition.getRow() == 1);
                if(columnMove == 0){ // Forward move
                    if (pieceAtNextSpace == null){
                        possibleMoves.addAll(getPromotedPieces(myPosition, nextPosition, isPromoted));
                        if((direction == 1 && myPosition.getRow() == 2) ||
                                (direction == -1 && myPosition.getRow() == 7)){
                            nextPosition = nextPosition.getNextPosition(direction, 0);
                            if(board.getPiece(nextPosition) == null){
                                possibleMoves.add(new ChessMove(myPosition, nextPosition, null));
                            }
                        }
                    }
                }
                else{ // Diagonal move
                    if(pieceAtNextSpace != null &&
                            myPiece.pieceColor != pieceAtNextSpace.pieceColor){
                        possibleMoves.addAll(getPromotedPieces(myPosition, nextPosition, isPromoted));
                    }
                }
            }
        }
        return possibleMoves;
    }

    private Collection<ChessMove> getPromotedPieces(ChessPosition myPosition, ChessPosition nextPosition, boolean isPromoted){
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        if(isPromoted){
            PieceType[] typesOfPieces = PieceType.values();
            for(PieceType promotionType: typesOfPieces){
                if(promotionType != PieceType.PAWN && promotionType != PieceType.KING) {
                    possibleMoves.add(new ChessMove(myPosition, nextPosition, promotionType));
                }
            }
        }
        else{
            possibleMoves.add(new ChessMove(myPosition, nextPosition, null));
        }
        return possibleMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    public char toChar() {
        String blackPieces = "kqbnrp";
        String whitePieces = "KQBNRP";
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            return blackPieces.charAt(type.getPieceID());
        } else {
            return whitePieces.charAt(type.getPieceID());
        }
    }
}