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
        if (myPiece == PieceType.ROOK || myPiece == PieceType.QUEEN) {
            possibleMoves.addAll(getMovesInLine(board, myPosition, 1, 0));
            possibleMoves.addAll(getMovesInLine(board, myPosition, 0, 1));
            possibleMoves.addAll(getMovesInLine(board, myPosition, -1, 0));
            possibleMoves.addAll(getMovesInLine(board, myPosition, 0, -1));
        }
        if (myPiece == PieceType.BISHOP || myPiece == PieceType.QUEEN) {
            possibleMoves.addAll(getMovesInLine(board, myPosition, 1, 1));
            possibleMoves.addAll(getMovesInLine(board, myPosition, -1, -1));
            possibleMoves.addAll(getMovesInLine(board, myPosition, -1, 1));
            possibleMoves.addAll(getMovesInLine(board, myPosition, 1, -1));
        } else if (myPiece == PieceType.KNIGHT) {
            final int[][] setOfMoves = {{1, 2}, {-1, 2}, {1, -2}, {-1, -2}, {2, 1}, {-2, 1}, {2, -1}, {-2, -1}};
            possibleMoves.addAll(checkEachMove(board, myPosition, setOfMoves));
        } else if (myPiece == PieceType.KING) {
            final int[][] setOfMoves = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
            possibleMoves.addAll(checkEachMove(board, myPosition, setOfMoves));
        }
        else{ // PieceType.PAWN
            possibleMoves.addAll(checkPawnMoves(board, myPosition));
        }
        return possibleMoves;
    }

    public Collection<ChessMove> getMovesInLine(ChessBoard board, ChessPosition myPosition, int xDirection, int yDirection) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int nextColumn = myPosition.getColumn() + xDirection;
        int nextRow = myPosition.getRow() + yDirection;
        ChessPosition nextPosition = new ChessPosition(nextRow, nextColumn);

        while (nextColumn > 0 && nextColumn < 9 &&
                nextRow > 0 && nextRow < 9 &&
                board.getPiece(nextPosition) == null) {
            possibleMoves.add(new ChessMove(myPosition, nextPosition, null));
            nextColumn += xDirection;
            nextRow += yDirection;
            nextPosition = new ChessPosition(nextRow, nextColumn);
        }

        if (nextColumn > 0 && nextColumn < 9 &&
                nextRow > 0 && nextRow < 9) {
            if (board.getPiece(myPosition).pieceColor != board.getPiece(nextPosition).pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, nextPosition, null));
                // You can capture!
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> checkEachMove(ChessBoard board, ChessPosition myPosition, int[][] setOfMoves) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int nextColumn;
        int nextRow;
        ChessPosition nextPosition;
        for (int[] move : setOfMoves) {
            nextColumn = myPosition.getColumn() + move[0];
            nextRow = myPosition.getRow() + move[1];
            if (nextColumn > 0 && nextColumn < 9 &&
                    nextRow > 0 && nextRow < 9) {
                nextPosition = new ChessPosition(nextRow, nextColumn);
                ChessPiece pieceAtNextSpace = board.getPiece(nextPosition);
                if (pieceAtNextSpace == null ||
                        board.getPiece(myPosition).pieceColor != pieceAtNextSpace.pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, nextPosition, null));
                }
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> checkPawnMoves(ChessBoard board, ChessPosition myPosition) {
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

        int nextColumn;
        int nextRow;
        ChessPosition nextPosition;
        ChessPiece pieceAtNextSpace;
        PieceType promotionPiece;
        for (int columnMove : setOfColumns) {
            nextColumn = myPosition.getColumn() + columnMove;
            nextRow = myPosition.getRow() + direction;
            if (nextColumn > 0 && nextColumn < 9 &&
                    nextRow > 0 && nextRow < 9) {
                nextPosition = new ChessPosition(nextRow, nextColumn);
                pieceAtNextSpace = board.getPiece(nextPosition);
                if ((direction == 1 && nextRow == 8) ||
                        (direction == -1 && nextRow == 1)){
                    promotionPiece = PieceType.QUEEN;
                }
                else{ // Not at the end of the board
                    promotionPiece = null;
                }
                if(columnMove == 0){ // Forward move
                    if (pieceAtNextSpace == null){
                        possibleMoves.add(new ChessMove(myPosition, nextPosition, promotionPiece));
                    }
                    if((direction == 1 && myPosition.getRow() == 2) ||
                            (direction == -1 && myPosition.getRow() == 7)){
                        nextPosition = new ChessPosition(nextRow + direction, nextColumn);
                        if(board.getPiece(nextPosition) == null){
                            possibleMoves.add(new ChessMove(myPosition, nextPosition, promotionPiece));
                        }
                    }
                }
                else{ // Diagonal move
                    if(pieceAtNextSpace != null &&
                            myPiece.pieceColor != pieceAtNextSpace.pieceColor){
                        possibleMoves.add(new ChessMove(myPosition, nextPosition, promotionPiece));
                    }
                }
            }
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
